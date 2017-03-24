package group8.tcss450.uw.edu.consearch.connectors;

/**
 * @author Team 8
 * @version 1.0
 * Class to contain string constants for the Login and Register activities.
 */

public class Constants {
    private static final String ROOT_URL =
            "https://winter2017.000webhostapp.com/consearch/operations/";

    public static final String URL_LOGIN = ROOT_URL+"userLogin_tcss450.php";
    public static final String URL_REGISTER = ROOT_URL+"registerUser_tcss450.php";


    public static final String ARTIST_URL_BEGINNING =
            "http://api.songkick.com/api/3.0/search/artists.json?query=";
    public static final String VENUE_URL_BEGINNING =
            "http://api.songkick.com/api/3.0/search/venues.json?query=";
    public static final String LOCATION_URL_BEGINNING =
            "http://api.songkick.com/api/3.0/events.json?location=geo:";
    public static final String SONGKICK_URL_END = "&apikey=w6Xcjutcc9YOPSeQ";
    public static final String GEOCODE_URL_BEGINNING =
            "https://maps.googleapis.com/maps/api/geocode/json?address=";
    public static final String GEOCODE_URL_END = "&key=AIzaSyCatkP9llznK2pGhW3l9IF6ptbnxKKIpF4";

    public static final String OBJECT_KEY = "object_key";
    public static final String LIST_KEY = "artist_list";

    public static final String TYPE_ARTIST = "artist";
    public static final String TYPE_VENUE = "venue";

    /* Empty constructor to prevent instantiation. */
    private Constants() {}
}
