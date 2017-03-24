package group8.tcss450.uw.edu.consearch.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.TreeMap;

import group8.tcss450.uw.edu.consearch.R;
import group8.tcss450.uw.edu.consearch.connectors.Constants;
import group8.tcss450.uw.edu.consearch.connectors.SessionManager;
import group8.tcss450.uw.edu.consearch.database.ObjectDatabase;
import group8.tcss450.uw.edu.consearch.model.Artist;
import group8.tcss450.uw.edu.consearch.model.ConSearchObject;import group8.tcss450.uw.edu.consearch.tasks.GetInfoTask;
import group8.tcss450.uw.edu.consearch.tasks.PickerDialog;
import group8.tcss450.uw.edu.consearch.tasks.PickerDialog.AlertPositiveListener;

import android.widget.Toast;

/**
 * @author Team 8
 * @version 1.0
 * Grabs and stores user preferences locally. Preferences used in home screen.
 */
public class ProfileActivity extends AppCompatActivity implements AlertPositiveListener {

    private SessionManager sm;
    private int artistCount = 0;
    private static String[] OBJECTS_ARRAY;// = new String[5];
    private String chosenOptionText;

    private ArrayList<Artist> selectedArtist = new ArrayList<>();
    private TreeMap<String, ConSearchObject>  sharedMap = new TreeMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        sm = new SessionManager(this);
        TextView userDisplay = (TextView) findViewById(R.id.user_display);

        String hello = getResources().getString(R.string.hello) + " " + sm.getUsername();
        userDisplay.setText(hello);
    }

    /**
     * Activated when the user clicks save. Reads all data existing in UI text boxes.
     * @param view The View
     */
    public void clickSave(View view) {
        ObjectDatabase db = new ObjectDatabase(this);

        for (int i = 0; i < selectedArtist.size(); i++)
            if (!db.isArtistExist(selectedArtist.get(i).getName())) //if artist doesn't exist in database,
                db.addArtist(selectedArtist.get(i)); //add artist to database.

        makeToast("Successfully added Preferences to database..");
    }

    /**
     * Adds a text box for imputing more artist preferences.
     * @param view The View
     */
    public void clickArtistAdd(View view) {
        EditText current;
        LinearLayout layout = (LinearLayout) findViewById(R.id.artist_block);
        //if artistCount == 0, current = profile_artist (declared in XML)
        //else it will be equal to the most recently created editText.
        current = (artistCount == 0) ? (EditText) findViewById(R.id.profile_artist) :
                (EditText) layout.getChildAt(artistCount);

        //call api, get Input suggestions, add in array and put in bundle.
        String getArtistUrl = Constants.ARTIST_URL_BEGINNING + current.getText().toString() +
                Constants.SONGKICK_URL_END;
        new GetInfoTask(this, getArtistUrl, new ProgressDialog(this), Constants.TYPE_ARTIST,
                new GetInfoTask.AsyncResponse() {
            @Override
            public void processFinish(ArrayList<ConSearchObject> output) {
                if (output.isEmpty())
                    makeToast("No Suggestions, check your spelling..");
                else {
                    //if i have less than 5 in returned list, set array size to size of list
                    //if i have more than 5 in returned list, set array size to 5
                    OBJECTS_ARRAY = new String[(output.size() < 5) ? output.size() : 5];
                    for (int i = 0; i < OBJECTS_ARRAY.length; i++) {
                        if (output.get(i) instanceof Artist) {
                            Artist a = (Artist) output.get(i);
                            OBJECTS_ARRAY[i] = a.getName();
                        sharedMap.put(a.getName(), a);
                        }
                    }
                    showDialog(R.string.choose_artist, Constants.TYPE_ARTIST);
                }
            }
        }).execute();
    }

    /**
     * Creates a toast from the given text.
     * @param text Text for Toast
     */
    private void makeToast(String text) {
        Toast.makeText(this, text , Toast.LENGTH_LONG).show();
    }

    /**
     * Displays a box for selecting/confirming an artist.
     * @param title Title of artist
     * @param objectType key
     */
    private void showDialog(int title, String objectType) {
        PickerDialog pickerDialog = new PickerDialog();
        Bundle args = new Bundle();
        args.putStringArray("array", OBJECTS_ARRAY);
        args.putInt("title", title);
        args.putString("objectType", objectType);

        pickerDialog.setArguments(args);
        pickerDialog.show(getFragmentManager(), "Dialog Picker");
    }

    /**
     * Creates new text box after accepting an artist.
     */
    private void handleReturnedArtist() {
        EditText current;
        LinearLayout layout = (LinearLayout) findViewById(R.id.artist_block);
        current = (artistCount == 0) ? (EditText) findViewById(R.id.profile_artist) :
                (EditText) layout.getChildAt(artistCount);

        current.setText(chosenOptionText);
        selectedArtist.add((Artist) sharedMap.get(chosenOptionText));
        sharedMap.clear();
        current.setEnabled(false);

        artistCount++;

        EditText editText = new EditText(getApplicationContext());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics());
        int marginBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
        editText.setTextSize(20);
        editText.setHint("Artist");
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setTextColor(Color.BLACK);

        //Uses depreciated call for API 15 to match minimum API
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)  {
            editText.setBackgroundDrawable(findViewById(R.id.profile_artist).getBackground());
        } else {
            editText.setBackground(findViewById(R.id.profile_artist).getBackground());
        }


        ActionBar.LayoutParams params = new ActionBar.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, marginBottom);
        editText.setLayoutParams(params);

        layout.addView(editText);
    }

    @Override
    public void onPositiveClick(int position, String returnedObjectType) {
        chosenOptionText = OBJECTS_ARRAY[position];
        // showDialog();
        if (returnedObjectType.equals(Constants.TYPE_ARTIST))
            handleReturnedArtist();
    }


    /**
     * Logs the user out and redirects to the login activity.
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

        MenuItem profile = menu.findItem(R.id.menu_home);
        MenuItem logout = menu.findItem(R.id.menu_Logout);
        Intent profileActivity = new Intent(this, group8.tcss450.uw.edu.consearch.activities.HomeActivity.class);
        profile.setIntent(profileActivity);

        logout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                logout();
                return false;
            }
        });
        return true;
    }
}