package group8.tcss450.uw.edu.consearch.fragments.eventFragments;

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
import android.widget.TextView;

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

import group8.tcss450.uw.edu.consearch.activities.MainActivity;
import group8.tcss450.uw.edu.consearch.database.JSONDeveloper;
import group8.tcss450.uw.edu.consearch.database.ObjectDatabase;
import group8.tcss450.uw.edu.consearch.fragments.artistFragments.ArtistResultsUpperFragment;
import group8.tcss450.uw.edu.consearch.model.Artist;
import group8.tcss450.uw.edu.consearch.model.Event;
import group8.tcss450.uw.edu.consearch.R;

/**
 * @author Team 8
 * @version 1.0
 * Fragment to show a list view of results.
 */

public class EventResultsFragment extends Fragment implements Serializable  {

    private static final String OBJECT_KEY = "object_key";
    private static final String TYPE_KEY = "type_key";
    private static final String HTML_STRING_BEGINNING = "<a href=\"";
    private static final String HTML_STRING_END = "\">Event Website</a>";
    private static final String LASTFM_URL_BEGINNING =
            "http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist=";
    private static final String LASTFM_URL_END = "" +
            "&api_key=43d8e0220d48794ceade532ae023c947&format=json";
    private static final String SONGKICK_URL_BEGINNING = "http://api.songkick.com/api/3.0/artists/";
    private static final String SONGKICK_ARTIST_URL_END = "/calendar.json?&apikey=w6Xcjutcc9YOPSeQ";

    Event mEvent;

    /**
     * Empty constructor to prevent instantiation
     */
    public EventResultsFragment() {    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_results, container, false);
        mEvent = (Event) getArguments().getSerializable(OBJECT_KEY);
        TextView eventName = (TextView) view.findViewById(R.id.event_name_textView);
        TextView venueName = (TextView) view.findViewById(R.id.venue_name_textView);
        TextView website = (TextView) view.findViewById(R.id.event_website_textView);
        ListView listView = (ListView) view.findViewById(R.id.event_artist_list);
        eventName.setText(mEvent.getName());
        venueName.setText(mEvent.getVenueName());
        website.setText(mEvent.getWebsite());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, mEvent.getArtists());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String artistSelected = parent.getItemAtPosition(position).toString();
                new GetExtraArtistInfoTask().execute(artistSelected);
            }
        });
        return view;
    }

    /**
     * AsyncTask to get JSON Objects from the LastFM and SongKick APIs to populate the Artist
     * object that the user selects.
     */
    private class GetExtraArtistInfoTask extends AsyncTask<String, Integer, Artist> {
        @Override
        protected Artist doInBackground(String... strings) {
            String lastURL = LASTFM_URL_BEGINNING + strings[0] + LASTFM_URL_END;

            Artist artistObject = (MainActivity.DataBase.getArtist(strings[0]) != null )
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
                while ((songString = bufferedReader.readLine()) != null) {
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
}
