package group8.tcss450.uw.edu.consearch.connectors;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Team 8
 * @version 1.0
 * Makes the connection with the server and passes and receives data.
 */

public class Uploader extends AsyncTask<Void, Void, String> {

    private Context context;
    private String urlAddress;
    private Map<String, String> keysValues;
    private ProgressDialog pd;
    private AsyncResponse delegate;

    public interface AsyncResponse {
        void processFinish(String output);
    }

    /**
     * Constructor for the Uploader.
     * @param context The Uploader's context
     * @param urlAddress The address to be accessed
     * @param keyValues The string values, username and password
     * @param delegate The response from the server
     */
    public Uploader(Context context, String urlAddress, Map<String, String> keyValues, AsyncResponse delegate) {
        this.context = context;
        this.urlAddress = urlAddress;
        this.keysValues = keyValues;
        this.delegate = delegate;

        pd = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd.setTitle("Send");
        pd.setMessage("loading...");
        pd.show();
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        pd.dismiss();
        if (response != null) {
            //SUCCESS
            //Toast.makeText(context, response, Toast.LENGTH_LONG).show();
            delegate.processFinish(response);
        } else {
            //NO SUCCESS
            Toast.makeText(context, null, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Makes the connection and sends the data to the server and receives response.
     * @return Error to be displayed if connection failed, or response from server if succeeded
     */
    private String send() {
        //CONNECT
        HttpURLConnection con = Connector.connect(urlAddress);
        if (con == null) {
            return "Could not connect to URL";
        } else {
            try {
                OutputStream os = con.getOutputStream();
                //WRITE
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                bw.write(packData());
                bw.flush();
                //RELEASE RES
                bw.close();
                os.close();
                //HAS IT BEEN SUCCESSFUL?
                int responseCode = con.getResponseCode();
                if (responseCode == con.HTTP_OK) {
                    //GET EXACT RESPONSE
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    //READ LINE BY LINE
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    //RELEASE RES
                    br.close();
                    return response.toString();
                } else {return "error code " +responseCode + " occured"; }
            } catch (IOException e) {
                e.printStackTrace();
                return "Some error occured!";
            }
        }
    }


    /**
     * Generates a string for the URL based on the input from the user; username and password.
     * @return The generated string
     */
    private String packData() {
        JSONObject jo = new JSONObject();
        StringBuilder packedData = new StringBuilder();
        String[] keys = new String[keysValues.size()];
        keysValues.keySet().toArray(keys);
        try {
            for (int i = 0; i < keys.length; i++)
                jo.put(keys[i], keysValues.get(keys[i]));

            Boolean firstValue = true;
            Iterator it = jo.keys();
            do {
                String key = it.next().toString();
                String value = jo.get(key).toString();
                if (firstValue) {
                    firstValue = false;
                } else {
                    packedData.append("&");
                }
                packedData.append(URLEncoder.encode(key, "UTF-8"));
                packedData.append("=");
                packedData.append(URLEncoder.encode(value, "UTF-8"));
            } while (it.hasNext());
            return packedData.toString();
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected String doInBackground(Void... params) {
        return this.send();
    }
}