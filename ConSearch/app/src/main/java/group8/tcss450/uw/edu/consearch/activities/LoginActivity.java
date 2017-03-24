package group8.tcss450.uw.edu.consearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;

import group8.tcss450.uw.edu.consearch.connectors.Constants;
import group8.tcss450.uw.edu.consearch.connectors.SessionManager;
import group8.tcss450.uw.edu.consearch.connectors.Uploader;
import group8.tcss450.uw.edu.consearch.R;

/**
 * @author Team 8
 * @version 1.0
 * Instantiates a login interface where a user can enter existing
 * login credentials. Authenticates valid credentials and otherwise
 * displays an error.
 */
public class LoginActivity extends AppCompatActivity {
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            startHomeActivity(sessionManager.getUsername());
        }
    }

    /**
     * Executes when a user attempts to sign in. Displays errors for
     * blank fields or invalid login credentials. Otherwise authenticates
     * the user.
     * @param view Holds UI elements for LoginActivity.
     */
    public void clickSignIn(View view) {
        boolean authenticate = true;
        EditText usernameET = (EditText) findViewById(R.id.usernameField);
        EditText passwordET = (EditText) findViewById(R.id.passwordField);
        String username = usernameET.getText().toString();
        String password = passwordET.getText().toString();
        if (usernameET.getText().toString().trim().equalsIgnoreCase("")) {
            usernameET.setError("This field can not be blank");
            authenticate = false;
        }
        if (passwordET.getText().toString().trim().equalsIgnoreCase("")) {
            passwordET.setError("This field can not be blank");
            authenticate = false;
        }
        if (!isValid(username, password)) {
            authenticate = false;
            usernameET.setText("");
            passwordET.setText("");
        }
        if (authenticate) {
            Map<String, String> parameters = new TreeMap<>();
            parameters.put("username", username);
            parameters.put("password", password);
            Uploader up = new Uploader(this, Constants.URL_LOGIN, parameters, new Uploader.AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    validateResponse(output);
                }
            });
            up.execute();
        }
    }

    /**
     * Queries database for JSON response to test for valid login credentials.
     * @param response Response from uploader
     */
    private void validateResponse(String response) {
        JSONObject jObj;
        try {
            jObj = new JSONObject(response);
            boolean error = jObj.getBoolean("error");

            if (!error) { //successfully logged in
                String id = jObj.getString("id");
                String name = jObj.getString("name");

                sessionManager.setIsLoggedIn(true);
                sessionManager.setUsername(name);

                startHomeActivity(name);
                Toast.makeText(this, sessionManager.getUsername(), Toast.LENGTH_SHORT).show();
            } else {
                String message = jObj.getString("message");
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Starts the Home Activity after login.
     * @param username The user's username for display.
     */
    private void startHomeActivity(String username) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }

    /**
     * Dummy check for valid user.
     * @param username User entered username.
     * @param password User entered password.
     * @return Returns true if valid user.
     */
    protected boolean isValid(String username, String password) {
        return true;
    }

    /**
     * Redirects the user to registration.
     * @param view Holds UI elements.
     */
    public void clickCreateAccount(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

}