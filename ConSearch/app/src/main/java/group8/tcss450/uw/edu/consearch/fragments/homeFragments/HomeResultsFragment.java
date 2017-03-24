package group8.tcss450.uw.edu.consearch.fragments.homeFragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import group8.tcss450.uw.edu.consearch.activities.MainActivity;
import group8.tcss450.uw.edu.consearch.database.ObjectDatabase;
import group8.tcss450.uw.edu.consearch.fragments.artistFragments.ArtistResultsUpperFragment;
import group8.tcss450.uw.edu.consearch.fragments.eventFragments.EventListFragment;
import group8.tcss450.uw.edu.consearch.fragments.eventFragments.EventResultsFragment;
import group8.tcss450.uw.edu.consearch.fragments.venueFragments.VenueResultsFragment;
import group8.tcss450.uw.edu.consearch.model.Artist;
import group8.tcss450.uw.edu.consearch.database.JSONDeveloper;
import group8.tcss450.uw.edu.consearch.R;
import group8.tcss450.uw.edu.consearch.model.Event;
import group8.tcss450.uw.edu.consearch.model.Venue;


/**
 * @author Team 8
 * @version 1.0
 * Fragment to show a list view of results based on a user's query.
 */
public class HomeResultsFragment extends Fragment implements Serializable   {


    private static final String OBJECT_KEY = "object_key";
    private static final String LIST_KEY = "artist_list";
    private static final String TYPE_KEY = "type_key";

    private static final String LASTFM_URL_BEGINNING =
            "http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist=";
    private static final String LASTFM_URL_END = "" +
            "&api_key=43d8e0220d48794ceade532ae023c947&format=json";
    private static final String SONGKICK_URL_BEGINNING = "http://api.songkick.com/api/3.0/artists/";
    private static final String SONGKICK_ARTIST_URL_END = "/calendar.json?&apikey=w6Xcjutcc9YOPSeQ";
    private static final String SONGKICK_VENUE_URL_BEGINNING =
            "http://api.songkick.com/api/3.0/venues/";
    private static final String SONGKICK_VENUE_URL_END = "/calendar.json?&apikey=w6Xcjutcc9YOPSeQ";



    ArrayList<String> list;
    String type;

    public HomeResultsFragment() {    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_results, container, false);
        list = getArguments().getStringArrayList(LIST_KEY);
        type = getArguments().getString(OBJECT_KEY);
        if (type != null) {
            switch (type) {
                case "Artist":
                    setArtistListView(view, list);
                    break;
                case "Venue":
                    setVenueListView(view, list);
                    break;
                case "Event":
                    setEventListView(view, list);
            }
        }
        return view;
    }

    /**
     * Helper method to set the ListView for this fragment to view the search results of an artist
     * search.
     * @param view The view
     * @param strings The list of artist names
     */
    private void setArtistListView(View view, ArrayList<String> strings)   {
        ListView list = (ListView) view.findViewById(R.id.home_results_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, strings);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String artistSelected = parent.getItemAtPosition(position).toString();
                new GetExtraArtistInfoTask().execute(artistSelected);
            }
        });
    }

    /**
     * Helper method to set the ListView for this fragment to view the search results of a venue
     * search.
     * @param view The view
     * @param strings The list of venue names
     */
    private void setVenueListView(View view, ArrayList<String> strings) {
        ListView list = (ListView) view.findViewById(R.id.home_results_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, strings);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String venueSelected = parent.getItemAtPosition(position).toString();
                Venue venue = MainActivity.DataBase.getVenue(venueSelected);
                new GetVenueEventsTask().execute(venue);
            }
        });
    }

    /**
     * Helper method to set the ListView for this fragment to view the search results of a location
     * search.
     * @param view The view
     * @param strings The list of event names
     */
    private void setEventListView(View view, ArrayList<String> strings) {
        ListView list = (ListView) view.findViewById(R.id.home_results_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, strings);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
    }

    /**
     * AsyncTask to get JSON Objects from the LastFM and SongKick APIs to populate the Artist
     * object that the user selects.
     */
    private class GetExtraArtistInfoTask extends AsyncTask<String, Integer, Artist> {
        @Override
        protected Artist doInBackground(String... strings) {
            String lastURL = LASTFM_URL_BEGINNING + strings[0] + LASTFM_URL_END;
            Artist artistObject = (MainActivity.DataBase.getArtist(strings[0]) != null)
                    ? MainActivity.DataBase.getArtist(strings[0]) : new ObjectDatabase(getActivity()).getArtist(strings[0]);

            String id = artistObject.getID();
            String songURL = SONGKICK_URL_BEGINNING + id + SONGKICK_ARTIST_URL_END;
            try {
                URL url = new URL(lastURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream stream = new BufferedInputStream(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();
                String lastString;
                while ((lastString = bufferedReader.readLine()) != null) {
                    builder.append(lastString);
                }
                JSONObject lastRoot = new JSONObject(builder.toString());

                url = new URL(songURL);
                connection = (HttpURLConnection) url.openConnection();
                stream = new BufferedInputStream(connection.getInputStream());
                bufferedReader = new BufferedReader(new InputStreamReader(stream));
                builder = new StringBuilder();
                String songString;
                while ((songString = bufferedReader.readLine()) != null)   {
                    builder.append(songString);
                }
                JSONObject songRoot = new JSONObject(builder.toString());

                JSONDeveloper.developArtist(songRoot, lastRoot, artistObject);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return artistObject;
        }

        @Override
        protected void onPostExecute(Artist artistSelected)   {
            ArtistResultsUpperFragment upperFragment = new ArtistResultsUpperFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(OBJECT_KEY, artistSelected);
            bundle.putString(TYPE_KEY, "artist");
            upperFragment.setArguments(bundle);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_upper_frame, upperFragment)
                    .addToBackStack(null);
            transaction.commit();
        }
    }

    /**
     * ASyncTask to get a JSON object of a venue from the SongKick API.
     */
    private class GetVenueEventsTask extends AsyncTask<Venue, Integer, Venue>   {
        @Override
        protected Venue doInBackground(Venue... venues) {
            Venue venue = venues[0];
            String venueURL = SONGKICK_VENUE_URL_BEGINNING + venue.getId()
                    + SONGKICK_VENUE_URL_END;
            try {
                URL url = new URL(venueURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream stream = new BufferedInputStream(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();
                String lastString;
                while ((lastString = bufferedReader.readLine()) != null) {
                    builder.append(lastString);
                }
                JSONObject root = new JSONObject(builder.toString());
                venue.setEventList(JSONDeveloper.addVenueEvents(root));
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return venue;
        }

        @Override
        protected void onPostExecute(Venue venue)  {
            Bundle bundle = new Bundle();
            bundle.putSerializable(OBJECT_KEY, venue);
            bundle.putString(TYPE_KEY, "venue");
            VenueResultsFragment venueFragment = new VenueResultsFragment();
            EventListFragment eventFragment = new EventListFragment();
            venueFragment.setArguments(bundle);
            eventFragment.setArguments(bundle);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_upper_frame, venueFragment)
                    .replace(R.id.home_lower_frame, eventFragment)
                    .addToBackStack(null);
            transaction.commit();
        }
    }
}
