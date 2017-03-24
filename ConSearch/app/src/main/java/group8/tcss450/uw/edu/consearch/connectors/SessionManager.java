package group8.tcss450.uw.edu.consearch.connectors;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * @author Team 8
 * @version 1.0
 * Class that manages the user login information.
 */
public class SessionManager {

    private Context context;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String username;

    public static String PREF_NAME = "Login Info";
    private static String KEY_IS_LOGGEDIN = "Logged In";
    private static String KEY_USERNAME = "Username";

    /**
     * Constructs the Sessionmanager Object.
     * @param context the associated context.
     */
    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /**
     * Gets the Username if the user exists, else returns an empty string.
     * @return the Saved Username.
     */
    public String getUsername() {
        return pref.getString(KEY_USERNAME, "");
    }

    /**
     * Sets the Username.
     * @param newUsername the new Username.
     */
    public void setUsername(String newUsername) {
        editor.putString(KEY_USERNAME, newUsername);
        editor.commit();
        this.username = newUsername;
    }

    /**
     * Checks if the user is logged in already.
     * @return true of the user is logged in, false otherwise.
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    /**
     * Logs the user out.
     */
    public void logout() {
        editor.putBoolean(KEY_IS_LOGGEDIN, false);
        editor.remove(KEY_IS_LOGGEDIN);
        editor.commit();
    }

    /**
     * Sets the loogged in property.
     * @param isLoggedIn the new logged in value.
     */
    public void setIsLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        // commit changes
        editor.commit();
        Log.d("Session Manager", "User login session modified!");
    }
}
