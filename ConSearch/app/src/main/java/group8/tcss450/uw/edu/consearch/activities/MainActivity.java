package group8.tcss450.uw.edu.consearch.activities;

/**
 * @Team 8
 * @version 1.0
 * Main activity when the application begins.
 */

import android.content.Intent;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;

import group8.tcss450.uw.edu.consearch.R;
import group8.tcss450.uw.edu.consearch.database.Maps;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener   {

    public static Maps DataBase ;
    public static GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //object database takes the current context
        DataBase = new Maps();

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        mGoogleApiClient.connect();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
}
