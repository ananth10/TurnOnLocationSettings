package com.turnonlocationsettings;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class TurnOnLocationActivity extends AppCompatActivity  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,
        ResultCallback<LocationSettingsResult> {


    private Button mTurnOnLocation;
    public static GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected LocationSettingsRequest mLocationSettingsRequest;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    protected static final String TAG = "location-settings";
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    protected Location mLastLocation;
    private boolean mSatisfied = false;
    private Double mLatitude = 0.0;
    private Double mLongitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn_on_location);
        mTurnOnLocation=(Button)findViewById(R.id.location_settings);
        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();

        mTurnOnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLocationSettings();
            }
        });
    }
    public synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.setAlwaysShow(true);
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval
        // is
        // inexact. You may not receive updates at all if no location sources
        // are available, or
        // you may receive them slower than requested. You may also receive
        // updates faster than
        // requested if other applications are requesting location at a faster
        // interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is
        // exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                .checkLocationSettings(mGoogleApiClient,
                        mLocationSettingsRequest);
        result.setResultCallback(this);
    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        // TODO Auto-generated method stub

        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(TAG, "All location settings are satisfied.");
                startLocationUpdates();
                // startLocationUpdates();
                Toast.makeText(TurnOnLocationActivity.this,"already location enabled",Toast.LENGTH_SHORT).show();
                mSatisfied = true;
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG,
                        "Location settings are not satisfied. Show the user a dialog to"
                                + "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(TurnOnLocationActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG,
                        "Location settings are inadequate, and cannot be fixed here. Dialog "
                                + "not created.");
                mSatisfied = false;
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Check for the integer request code originally supplied to
            // startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG,
                                "User agreed to make required location settings changes.");
                        Toast.makeText(TurnOnLocationActivity.this,"location enabled",Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG,
                                "User chose not to make required location settings changes.");
                        Toast.makeText(TurnOnLocationActivity.this,"canceled",Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                        //update ui
                    }
                });

    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        mLastLocation = location;mLatitude = mLastLocation.getLatitude();
        mLongitude = mLastLocation.getLatitude();

    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnected(Bundle arg0) {
        // TODO Auto-generated method stub
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {

            mLatitude = mLastLocation.getLatitude();
            mLongitude = mLastLocation.getLongitude();
            // getAddress();
            // mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            // mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        } else {
            // Toast.makeText(this, R.string.no_location_detected,
            // Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        // TODO Auto-generated method stub

        mGoogleApiClient.connect();

    }
}
