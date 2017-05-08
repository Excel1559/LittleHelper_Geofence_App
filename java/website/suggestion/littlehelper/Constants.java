package website.suggestion.littlehelper;

/**
 * Created by Excelsior on 5/6/17.
 */

public class Constants {

    // Geofence constants
    protected static final int NOTIFICATION_REQUEST_CODE = 0;
    protected static final int GEOFENCE_REQUEST_CODE = 1;
    protected static final int GEOFENCE_RADIUS_IN_METERS = 50;
    protected static final int GEOFENCE_EXPIRATION_IN_MILLISECONDS = 1000 * 60 * 60; // 1 hour
    protected static final String GEOFENCE_REQUEST_ID = "1";

    // Location constants
    // Minimum time that must pass before location updates
    protected static final int MIN_TIME_IN_SECONDS = 0;
    // Minimum distance that must be traveled before location updates.
    protected static final int MIN_DISTANCE_IN_METERS = 0;
}
