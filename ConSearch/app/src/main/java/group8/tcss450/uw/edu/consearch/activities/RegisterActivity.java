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
import group8.tcss450.uw.edu.consearch.connectors.Uploader;
import group8.tcss450.uw.edu.consearch.R;

/**
 * @author Team 8
 * @version 1.0
 * Allows a user to fill in registration details and account preferences.
 * Builds user profile based on these details. Displays errors for incorrect
 * input.
 */
public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    /**
     * Validates user registration information. Stores credentials and
     * sends to database.
     * @param view Holds UI Elements
     */
    public void clickRegister(View view) {
        boolean registerConfirm = true;
        EditText usernameEt = (EditText) findViewById(R.id.usernameRegister);
        String username = usernameEt.getText().toString();
        EditText passwordET = (EditText) findViewById(R.id.passwordRegister);
        String password = passwordET.getText().toString();
        EditText confirmET = (EditText) findViewById(R.id.passwordConfirm);
        String confirmPassword = confirmET.getText().toString();


        String[] fields = {username, password, confirmPassword};
        EditText[] editTexts = {usernameEt, passwordET, confirmET};
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].trim().equalsIgnoreCase("")) {
                editTexts[i].setError("This field can not be blank");
                registerConfirm = false;
            }
        }

        if (!password.equals(confirmPassword)) {
            passwordET.setError("Passwords don't match");
            confirmET.setError("Passwords don't match");
            registerConfirm = false;
        }

        /*if (usernameExists(username)) {
            usernameEt.setError("Username already exists");
            registerConfirm = false;
        }*/

        for (int i = 0; i < username.length(); i++) {
            int ascii = username.charAt(i);
            if ((ascii >= 48 && ascii <= 57) || (ascii >= 65 && ascii <= 90)
                    || (ascii >= 97 && ascii <= 122)) {
                continue;
            } else {
                usernameEt.setError("Must contain only letters and digits");
                registerConfirm = false;
                break;
            }
        }

        if (username.length() < 3) {
            usernameEt.setError("Must be at least 3 characters");
            registerConfirm = false;
        }

        if (password.length() < 3) {
            passwordET.setError("Must be at least 3 characters");
            confirmET.setError("Must be at least 3 characters");
            registerConfirm = false;
        }

        if (registerConfirm) {
            Map<String, String> userInfo = new TreeMap<>();
            userInfo.put("username", username);
            userInfo.put("password", password);
            OpenHomeActivitySafe(userInfo);
        }
    }

    /**
     * Checks if username is Unique otherwise displays error.
     * @param response Response from uploader
     */
    private void showSuccess(final String response) {
        JSONObject jObj;
        try {
            jObj = new JSONObject(response);
            boolean error = jObj.getBoolean("error");

            if (!error) { //successfully registered
                String name = jObj.getString("name");

                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("USERNAME", name);
                startActivity(intent);
                finish();
            } else {
                String message = jObj.getString("message");
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
        }
    }

    private void OpenHomeActivitySafe(Map<String, String> myMap) {
        Uploader up = new Uploader(this, Constants.URL_REGISTER, myMap, new Uploader.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                showSuccess(output);
            }
        });
        up.execute();
    }

}