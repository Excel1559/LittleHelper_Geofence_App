package website.suggestion.littlehelper;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import static website.suggestion.littlehelper.LocationClass.mLocation;
import static website.suggestion.littlehelper.Constants.*;

/**
 * Created by Excelsior on 5/4/17.
 */

public class GeofenceClass implements ResultCallback {

    private static final String TAG = Geofence.class.getSimpleName();
    private Context mContext;
    private PendingIntent mGeofencePendingIntent;
    private GoogleApiClient mGoogleApiClient;
    protected List<Geofence> mGeofenceList;

    GeofenceClass(Context context, GoogleApiClient client) {
        mContext = context;
        mGoogleApiClient = client;
    }

    /**
     * Create a new geofence using the users current location
     */
    protected void createGeofence() {
        mGeofenceList = new ArrayList<>();
        mGeofenceList.add(new Geofence.Builder()
                .setRequestId(GEOFENCE_REQUEST_ID)
                .setCircularRegion(mLocation.getLatitude(), mLocation.getLongitude(), GEOFENCE_RADIUS_IN_METERS)
                .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());
    }

    /**
     * Create geofence request
     *
     * @return list of geofences to listen to
     */
    protected GeofencingRequest createGeofenceRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    /**
     * Register geofence with google services
     */
    public void registerGeofence() {
        // Check client is connected and list isn't empty
        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected() ||
                mGeofenceList == null || mGeofenceList.size() == 0) {
            return;
        }
        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    createGeofenceRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException securityException) {
            // Exception caught if ACCESS_FINE_LOCATION permission isn't granted
            Log.e(TAG, securityException.getMessage());
        }
    }

    /**
     * Unregister current Geofence from google services
     */
    public void unRegisterGeofence() {
        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
            return;
        }
        try {
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    getGeofencePendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException securityException) {
            // Exception caught if ACCESS_FINE_LOCATION permission isn't granted
            Log.e(TAG, securityException.getMessage());
        }
    }

    /**
     * Creates a PendingIntent that is used by GeofenceBroadcastReceiver
     *
     * @return the PendingIntent object
     */
    public PendingIntent getGeofencePendingIntent() {
        // Check if we already created a PendingIntent
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(mContext, GeofenceBroadcastReceiver.class);
        mGeofencePendingIntent = PendingIntent.getBroadcast(mContext, GEOFENCE_REQUEST_CODE, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    @Override
    public void onResult(@NonNull Result result) {
        Log.e(TAG, "Geofence Error " + result.getStatus().toString());
    }
}
