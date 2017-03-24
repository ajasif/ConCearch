package group8.tcss450.uw.edu.consearch.connectors;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Team 8
 * @version 1.0
 * Constructor object for URL connection.
 */

class Connector {

    /**
     * Empty constructor to prevent instantiation.
     */
    private Connector() {}

    /**
     * Creates the URL connection.
     * @param urlAddress The URL address to be connected to
     * @return The HttpURLConnection object
     */
    static HttpURLConnection connect(String urlAddress) {

        try {
            URL url = new URL(urlAddress);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            //SET PROPERTIES
            con.setRequestMethod("POST");
            con.setConnectTimeout(10000);
            con.setReadTimeout(10000);
            con.setDoInput(true);
            con.setDoOutput(true);

            //RETURN
            return con;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}