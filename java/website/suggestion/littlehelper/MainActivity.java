package website.suggestion.littlehelper;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import static website.suggestion.littlehelper.LocationClass.mLocation;
import static website.suggestion.littlehelper.R.id.addGeofence;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // Views
    private TextView addLocationTextView;
    private TextView addPermissionTextView;
    private TextView messageTextView;
    private ImageView addLocationButton;
    private CheckBox addPermissionCheckBox;
    private EditText messageEditText;

    // Variables
    private LocationClass locationClass;
    private GeofenceClass mGeofence;
    protected String mMessage;
    protected String mStoredMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .build();

        // Create instance of GeofenceClass
        mGeofence = new GeofenceClass(this, mGoogleApiClient);

        initializeViews();
        displayCurrentReminderMessage();

        // Create instance of LocationClass
        locationClass = new LocationClass(this, this);
        locationClass.setUpLocationListener();
    }

    /**
     * Check if the user has added location permission
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (locationClass.hasLocationPermission()) { // Permission has been granted
            addPermissionCheckBox.setVisibility(View.GONE);
            addPermissionTextView.setVisibility(View.GONE);

            addLocationButton.setEnabled(true);
            addLocationTextView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        } else if (!locationClass.hasLocationPermission()) { // Permission hasn't been granted
            // Uncheck check box
            addPermissionCheckBox.setChecked(false);

            // Gray out and disable add location button
            addLocationButton.setImageAlpha(120);
            addLocationButton.setEnabled(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeLocationListener();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.permissionCheckBox:
                locationClass.addLocationPermission();
                break;
            case R.id.addLocationTextView:
            case R.id.addLocationButton:
                locationClass.getCurrentLocation();
                break;
            case addGeofence:
                // save message in shared preferences
                mMessage = messageEditText.getText().toString();
                if (!TextUtils.isEmpty(mMessage)) {
                    // Save message
                    SharedPreferences.Editor editor = getSharedPreferences(getResources().getString(R.string.my_prefs), MODE_PRIVATE).edit();
                    editor.putString("Message", mMessage);
                    editor.apply();

                    messageTextView.setText(String.format(getResources().getString(R.string.stored_message), mMessage));
                    messageEditText.setText("");
                    startGeofence();
                } else {
                    Toast.makeText(this, R.string.no_message_error, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * Initialize all views and set up on click listeners
     */
    private void initializeViews() {
        Button addGeofence = (Button) findViewById(R.id.addGeofence);
        addLocationTextView = (TextView) findViewById(R.id.addLocationTextView);
        addPermissionTextView = (TextView) findViewById(R.id.locationPermissionTextView);
        messageTextView = (TextView) findViewById(R.id.messageTextView);
        addLocationButton = (ImageView) findViewById(R.id.addLocationButton);
        addPermissionCheckBox = (CheckBox) findViewById(R.id.permissionCheckBox);
        messageEditText = (EditText) findViewById(R.id.messageEditText);

        addLocationButton.setOnClickListener(this);
        addLocationTextView.setOnClickListener(this);
        addPermissionCheckBox.setOnClickListener(this);
        addGeofence.setOnClickListener(this);
    }

    /**
     * Retrieve and display current message stored in shared preferences
     */
    private void displayCurrentReminderMessage() {
        SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.my_prefs), MODE_PRIVATE);
        mStoredMessage = prefs.getString("Message", null);
        if (!TextUtils.isEmpty(mStoredMessage)) {
            messageTextView.setText(String.format(getResources().getString(R.string.stored_message), mStoredMessage));
        }
    }

    /**
     * Create and register geofence.
     */
    private void startGeofence() {
        if (mGeofence.mGeofenceList != null) {
            removeAllPreviousGeofences();
        } else if (mLocation != null) {
            mGeofence.createGeofence();
            mGeofence.createGeofenceRequest();
            mGeofence.registerGeofence();
            Toast.makeText(this, "Geofence added", Toast.LENGTH_SHORT).show();
            removeLocationListener();
        } else {
            Toast.makeText(this, "Need Location", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Unregistered current geofence and clear geofence list.
     */
    private void removeAllPreviousGeofences() {
        mGeofence.unRegisterGeofence();
        mGeofence.mGeofenceList.clear();
    }

    /**
     * Remove location listener
     */
    private void removeLocationListener() {
        if (locationClass.mLocationManager != null | locationClass.mLocationListener != null) {
            locationClass.removeLocationListener();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
