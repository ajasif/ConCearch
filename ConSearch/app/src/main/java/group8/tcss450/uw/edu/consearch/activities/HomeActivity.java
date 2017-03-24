package group8.tcss450.uw.edu.consearch.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import group8.tcss450.uw.edu.consearch.connectors.Constants;
import group8.tcss450.uw.edu.consearch.connectors.SessionManager;
import group8.tcss450.uw.edu.consearch.database.ObjectDatabase;
import group8.tcss450.uw.edu.consearch.fragments.artistFragments.ArtistResultsUpperFragment;
import group8.tcss450.uw.edu.consearch.fragments.homeFragments.HomeResultsFragment;
import group8.tcss450.uw.edu.consearch.fragments.homeFragments.SearchFragment;
import group8.tcss450.uw.edu.consearch.database.JSONDeveloper;
import group8.tcss450.uw.edu.consearch.R;
import group8.tcss450.uw.edu.consearch.fragments.artistFragments.ArtistBioFragment;
import group8.tcss450.uw.edu.consearch.fragments.artistFragments.SimilarArtistsFragment;
import group8.tcss450.uw.edu.consearch.fragments.eventFragments.EventListFragment;
import group8.tcss450.uw.edu.consearch.model.Artist;
import group8.tcss450.uw.edu.consearch.model.ConSearchObject;
import group8.tcss450.uw.edu.consearch.model.Event;
import group8.tcss450.uw.edu.consearch.model.Venue;
import group8.tcss450.uw.edu.consearch.tasks.GetInfoTask;

/**
 * @author Team 8
 * @version 1.0
 * The home activity where the SearchFragment and EventResultsFragment are contained.
 */

public class HomeActivity extends AppCompatActivity {
    private static final int ZIPCODE_MINIMUM_LENGTH = 5;
    private SessionManager sm;
    private ProgressDialog pd;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        SearchFragment fragment = new SearchFragment();
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.home_upper_frame, fragment);
        transaction.commit();
        sm = new SessionManager(this);
        pd = new ProgressDialog(this);

        ArrayList<ConSearchObject> l = new ObjectDatabase(this).getAllArtists();
        if (l.isEmpty())
            makeToast("No Saved preferences to display...");
        else
            handleReturnValue(l);
    }

    /**
     * Creates toast based on text provided.
     * @param text The text to display on the toast
     */
    private void makeToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    /**
     * Method called by the Search button in the Search Fragment.
     * @param view The view
     */
    public void homeSearchButtonClick(View view) {
        searchEditText = (EditText) findViewById(R.id.home_search_editText);
        String query = searchEditText.getText().toString();
        runAsyncTask(query);
    }

    /**
     * Helper method to run the correct ASyncTask per the user's search.
     * @param query What the user has typed in the EditText box in the Search Fragment
     */
    public void runAsyncTask(String query) {
        RadioGroup group = (RadioGroup) findViewById(R.id.search_radio_group);
        pd.setMessage("Retrieving Information...");
        switch (group.getCheckedRadioButtonId()) {
            case R.id.artist_search_radio_button:
                String getArtistQuery = Constants.ARTIST_URL_BEGINNING + query + Constants.SONGKICK_URL_END;
                new GetInfoTask(this, getArtistQuery, pd, Constants.TYPE_ARTIST, new GetInfoTask.AsyncResponse() {
                    @Override
                    public void processFinish(ArrayList<ConSearchObject> output) {
                        handleReturnValue(output);
                    }
                }).execute();
                break;
            case R.id.venue_search_radio_button:
                String getVenurQuery = Constants.VENUE_URL_BEGINNING + query + Constants.SONGKICK_URL_END;
                new GetInfoTask(this, getVenurQuery, pd, Constants.TYPE_VENUE, new GetInfoTask.AsyncResponse() {
                    @Override
                    public void processFinish(ArrayList<ConSearchObject> output) {
                        handleReturnValue(output);
                    }
                }).execute();
                break;
            case R.id.location_search_radio_button:
                if (query.length() < ZIPCODE_MINIMUM_LENGTH)    {
                    searchEditText.setError("Zipcode must be five numbers.");
                } else {
                    new GetGeoCodeTask().execute(query);
                }
                break;
        }
    }

    /**
     * Handles the retured response from the GetInfoTask class.
     * @param response The ArrayList GetInfoTask returns
     */
    private void handleReturnValue(ArrayList<ConSearchObject> response) {
        ArrayList<String> theNamesList = new ArrayList<>();
        String theObject = null;
        for (int i = 0; i < response.size(); i++) {
            if (response.get(i) instanceof Artist) {
                theObject = "Artist";
                Artist artist = (Artist) response.get(i);
                theNamesList.add(artist.getName());
            } else if (response.get(i) instanceof Venue) {
                theObject = "Venue";
                Venue venue = (Venue) response.get(i);
                theNamesList.add(venue.getName());
            } else if (response.get(i) instanceof Event) {
                theObject = "Event";
                Event event = (Event) response.get(i);
                theNamesList.add(event.getName());
            }
        }
        HomeResultsFragment fragment = new HomeResultsFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(Constants.LIST_KEY, theNamesList);
        bundle.putString(Constants.OBJECT_KEY, theObject);
        fragment.setArguments(bundle);
        if (getSupportFragmentManager().findFragmentById(R.id.home_lower_frame) == null) {
            FragmentTransaction trans = getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.home_lower_frame, fragment)
                    .addToBackStack(null);
            trans.commit();
        } else {
            FragmentTransaction trans = getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_lower_frame, fragment)
                    .addToBackStack(null);
            trans.commit();
        }
    }

    /**
     * AsyncTask to get the lat/lon information from address or zip code using Google GeoCode API.
     */
    private class GetGeoCodeTask extends AsyncTask<String, Integer, String>    {

        GetGeoCodeTask() {
            //Empty Constructor
        }

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder latlon = new StringBuilder();
            String geoURL = Constants.GEOCODE_URL_BEGINNING + strings[0] + Constants.GEOCODE_URL_END;
            try {
                URL url = new URL(geoURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                InputStream stream = new BufferedInputStream(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();

                String inputString;
                while ((inputString = bufferedReader.readLine()) != null) {
                    builder.append(inputString);
                }
                JSONObject topLevel = new JSONObject(builder.toString());
                JSONArray results = topLevel.getJSONArray("results");
                JSONObject geo = results.getJSONObject(0);
                JSONObject geometry = geo.getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");
                latlon.append(location.getString("lat"));
                latlon.append(",");
                latlon.append(location.getString("lng"));
                connection.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return latlon.toString();
        }

        @Override
        protected void onPostExecute(String location)  {
            if (location.matches(""))    {
                Toast.makeText(getApplicationContext(),
                        "GeoCode Error", Toast.LENGTH_LONG).show();
            }
            String url = Constants.LOCATION_URL_BEGINNING + location + Constants.SONGKICK_URL_END;
            new GetLocationTask().execute(url);
        }
    }

    /**
     * AsyncTask to request event information based off of location from the SongKick API.
     */
    private class GetLocationTask extends AsyncTask<String, Integer,
            ArrayList<String>>    {

        ArrayList<String> eventList = new ArrayList<>();

        GetLocationTask() {
            //Empty Constructor
        }

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream stream = new BufferedInputStream(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();

                String inputString;
                while ((inputString = bufferedReader.readLine()) != null) {
                    builder.append(inputString);
                }
                JSONObject songRoot = new JSONObject(builder.toString());
                JSONObject resultsPage = songRoot.getJSONObject("resultsPage");
                int entries = resultsPage.getInt("totalEntries");
                if (entries > 0)    {
                    eventList = JSONDeveloper.developEvents(songRoot);
                }
                connection.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return eventList;
        }

        @Override
        protected void onPostExecute(ArrayList<String> locationEvents)  {
            if (eventList.size() > 0)   {
                Bundle bundle = new Bundle();
                bundle.putStringArrayList(Constants.LIST_KEY, locationEvents);
                bundle.putString(Constants.OBJECT_KEY, "Event");
                HomeResultsFragment fragment = new HomeResultsFragment();
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.home_lower_frame, fragment)
                        .addToBackStack(null);
                transaction.commit();
            } else {
                Toast.makeText(getApplicationContext(), R.string.search_no_results,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Helper method to log the user out and return to the log-in screen.
     */
    private void logout() {
        if (sm.isLoggedIn()) {
            sm.logout();
        }
        Intent loginActivity = new Intent(this, LoginActivity.class);
        startActivity(loginActivity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);

        MenuItem profile = menu.findItem(R.id.menu_profile);
        MenuItem logout = menu.findItem(R.id.menu_Logout);

        logout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                logout();
                return false;
            }
        });
        MenuItem home = menu.findItem(R.id.menu_home);
        Intent profileActivity = new Intent(this, group8.tcss450.uw.edu.consearch.activities.ProfileActivity.class);
        profile.setIntent(profileActivity);

        Intent homeActivity = new Intent(this, HomeActivity.class);
        home.setIntent(homeActivity);
        return true;
    }

    @Override
    public void onBackPressed() {
        Fragment upper = getSupportFragmentManager().findFragmentById(R.id.home_upper_frame);
        Fragment lower = getSupportFragmentManager().findFragmentById(R.id.home_lower_frame);
        if (lower != null)  {
            if (lower instanceof ArtistBioFragment || lower instanceof SimilarArtistsFragment
                    || (upper instanceof ArtistResultsUpperFragment &&
                    lower instanceof EventListFragment))  {
                FragmentTransaction transaction = getSupportFragmentManager()
                        .beginTransaction()
                        .remove(lower);
                transaction.commit();
                super.onBackPressed();
            }
        }
        super.onBackPressed();
    }
}

