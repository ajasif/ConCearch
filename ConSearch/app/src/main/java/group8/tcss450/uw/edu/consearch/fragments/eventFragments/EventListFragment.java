package group8.tcss450.uw.edu.consearch.fragments.eventFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import group8.tcss450.uw.edu.consearch.activities.MainActivity;
import group8.tcss450.uw.edu.consearch.model.Artist;
import group8.tcss450.uw.edu.consearch.model.Event;
import group8.tcss450.uw.edu.consearch.R;
import group8.tcss450.uw.edu.consearch.model.Venue;

/**
 * @author Team 8
 * @version 1.0
 * Fragment to show a list of events.
 */
public class EventListFragment extends Fragment {

    private static final String OBJECT_KEY = "object_key";
    private static final String TYPE_KEY = "type_key";

    ArrayList<Event> eventObjects;
    ArrayList<String> events;
    String type;

    public EventListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);
        type = getArguments().getString(TYPE_KEY);
        events = new ArrayList<>();
        if (type != null)   {
            if (type.equals("artist"))  {
                Artist artist = (Artist) getArguments().getSerializable(OBJECT_KEY);
                if (artist != null)   {
                    if (artist.getArtistEvents() != null)   {
                        eventObjects = artist.getArtistEvents();
                        for (int i = 0; i < eventObjects.size(); i++)   {
                            events.add(eventObjects.get(i).getName());
                        }
                    }
                }
            } else if (type.equals("venue"))    {
                Venue venue = (Venue) getArguments().getSerializable(OBJECT_KEY);
                if (venue != null)  {
                    if (venue.getEventList() != null)   {
                        eventObjects = venue.getEventList();
                        for (int i = 0; i < eventObjects.size(); i++)    {
                            events.add(eventObjects.get(i).getName());
                        }
                    }
                }
            }
        }
        ListView listView = (ListView)  view.findViewById(R.id.event_list_fragment_listView);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, events);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String eventSelected = parent.getItemAtPosition(position).toString();
                Event event = MainActivity.DataBase.getEvent(eventSelected);
                Bundle bundle = new Bundle();
                bundle.putSerializable(OBJECT_KEY, event);
                EventResultsFragment fragment = new EventResultsFragment();
                Fragment upperFragment = getActivity().getSupportFragmentManager()
                        .findFragmentById(R.id.home_upper_frame);
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.home_lower_frame, fragment)
                        .hide(upperFragment)
                        .addToBackStack(null);
                transaction.commit();
            }
        });
        return view;
    }
}
