package group8.tcss450.uw.edu.consearch.tasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

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

import group8.tcss450.uw.edu.consearch.R;
import group8.tcss450.uw.edu.consearch.connectors.Constants;
import group8.tcss450.uw.edu.consearch.database.JSONDeveloper;
import group8.tcss450.uw.edu.consearch.model.ConSearchObject;

/**
 * @author Team 8
 * @version 1.0
 * Task to get Artist and Venue Information.
 */
public class GetInfoTask extends AsyncTask<String, Integer, ArrayList<ConSearchObject>> {

    private AppCompatActivity mActivity;
    private String url;
    private ProgressDialog pd;
    public String objectType;
    private AsyncResponse delegate;

    public interface AsyncResponse {
        //delegaete interface to send back response to caller
        void processFinish(ArrayList<ConSearchObject> output);
    }

    /**
     * Constructs the GetInfoTask Async task.
     * @param activity the calling activity.
     * @param url the url to be querried.
     * @param pd the progress dialog.
     * @param objectType the object type to be retrieved.
     * @param delegate the response dalegate.
     */
    public GetInfoTask(AppCompatActivity activity, String url, ProgressDialog pd, String objectType,  AsyncResponse delegate) {
        mActivity = activity;
        this.url = url;
        this.pd = pd;
        this.objectType = objectType;
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd.show();
    }

    /**
     * Calls API and returns a list of consearch objects
     * @param strings the api URL
     * @return a list of Artist, Events or Venues.
     */
    @Override
    protected ArrayList<ConSearchObject> doInBackground(String... strings) {
        String songKickUrl = url;
        ArrayList<ConSearchObject> objects = new ArrayList<>();
        try {
            URL url = new URL(songKickUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream stream = new BufferedInputStream(connection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder builder = new StringBuilder();
            String inputString;
            while ((inputString = bufferedReader.readLine()) != null) {
                builder.append(inputString); //build string from response
            }
            JSONObject songRoot = new JSONObject(builder.toString());
            //returns a list of ConsearchObjects returned from JSON.
            objects = getObjectsFromJSON(songRoot);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return objects;
    }

    /*
     * Calls the appropriate JSON formatting method based on the type of object expected.
     */
    private ArrayList<ConSearchObject> getObjectsFromJSON(JSONObject theJSON) {
        ArrayList<ConSearchObject> result = new ArrayList<>();
        switch(objectType) {
            case Constants.TYPE_ARTIST:
                result = JSONDeveloper.developArtistSearch(theJSON);
                break;
            case Constants.TYPE_VENUE:
                result = JSONDeveloper.developVenues(theJSON);
                break;
        }
        return result;
    }

    /**
     * Dismisses the Progress Dialog and notifies the caller or returned results.
     * @param names the  list of all ConSearchObjects.
     */
    @Override
    protected void onPostExecute(ArrayList<ConSearchObject> names) {
        pd.dismiss();
        delegate.processFinish(names);
    }
}
