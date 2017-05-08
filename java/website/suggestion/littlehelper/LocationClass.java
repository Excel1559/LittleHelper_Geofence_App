package website.suggestion.littlehelper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import static website.suggestion.littlehelper.Constants.MIN_DISTANCE_IN_METERS;
import static website.suggestion.littlehelper.Constants.MIN_TIME_IN_SECONDS;

/**
 * Created by Excelsior on 5/5/17.
 */

public class LocationClass {

    protected LocationManager mLocationManager;
    protected LocationListener mLocationListener;
    protected static Location mLocation;
    protected Context mContext;
    protected Activity mActivity;


    LocationClass(Context context, Activity activity) {
        mContext = context;
        mActivity = activity;
    }

    /**
     * Check if user has added location permission, if they haven't create dialog to allow location permission
     */
    protected void addLocationPermission() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

    /**
     * Check if user has added location permission
     */
    protected boolean hasLocationPermission() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Sets up the location listener and use this location to create a geofence
     */
    protected void setUpLocationListener() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        }
        // Define a listener that responds to location updates
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(android.location.Location location) {
                mLocation = location;
                removeLocationListener();
                Toast.makeText(mContext, R.string.location_found, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

    /**
     * Check if user has their GPS on.
     */
    protected void getCurrentLocation() {
        // Acquire a reference to the system Location Manager
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            Toast.makeText(mContext, R.string.getting_location, Toast.LENGTH_SHORT).show();
            attachLocationListener();
        }
    }

    /**
     * Create a pop up dialog asking the user to go to their settings and turn on their GPS
     */
    protected void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.gps_off_message)
                .setCancelable(false)
                .setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        mContext.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(R.string.not_now, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Start listening for location updates
     */
    protected void attachLocationListener() {
        try {
            // Register the listener with the LocationClass Manager to receive location updates
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_IN_SECONDS, MIN_DISTANCE_IN_METERS, mLocationListener);
        } catch (SecurityException se) {
            Log.e("Location Listener Error", se.getMessage());
        }
    }

    /**
     * Stop listening for location updates
     */
    protected void removeLocationListener() {
        try {
            mLocationManager.removeUpdates(mLocationListener);
        } catch (SecurityException se) {
            Log.e("Location Listener Error", se.getMessage());
        }
    }
}
