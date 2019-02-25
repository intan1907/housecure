package com.pbd.housecure.housecure;


import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v14.preference.SwitchPreference;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.List;

public class SettingsFragment extends PreferenceFragmentCompat {
    private SharedPreferences sharedPreferences;
    private SwitchPreference switchNotification;
    private Preference prefLocation;
    private GoogleApiClient googleApiClient = null;

    public static final String TAG = "Setting Fragment";

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        setNotificationListener();
        setEmergencySummary();
        setLocationPreferenceListener();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!isGPSEnable() && hasGPSDevice(getContext())) {
            showEnableLocationDialog();
        }
    }

    public void createGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        googleApiClient.connect();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                    }
                }).build();
    }

    @Override
    public void onResume() {
        super.onResume();
        switchNotification.setChecked(NotificationManagerCompat
                .from(getContext())
                .areNotificationsEnabled());
    }

    private void setNotificationListener() {
        switchNotification = (SwitchPreference) findPreference(getString(R.string.pref_notifications_key));
        switchNotification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Intent intent = new Intent();
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.putExtra("app_package", getActivity().getPackageName());
                    intent.putExtra("app_uid", getActivity().getApplicationInfo().uid);
                    startActivity(intent);
                } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    private void setEmergencySummary() {
        final String emergencyKey = getString(R.string.pref_emergency_key);
        String emergencyDefault = getString(R.string.pref_emergency_default_value);
        String emergencyValue = sharedPreferences.getString(emergencyKey, emergencyDefault);

        final EditTextPreference editTextPreference = (EditTextPreference) findPreference(emergencyKey);
        editTextPreference.setSummary(emergencyValue);

        editTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String newValue = o.toString();
                sharedPreferences.edit().putString(emergencyKey, newValue).apply();
                editTextPreference.setSummary(newValue);
                return true;
            }
        });
    }

    private void setLocationPreferenceListener() {
        prefLocation = findPreference(getString(R.string.pref_location_key));
        prefLocation.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // set location here
                if (!isGPSEnable() && hasGPSDevice(getContext())) {
                    Toast.makeText(getContext(), "GPS not enabled. Turn on GPS to continue", Toast.LENGTH_SHORT).show();
                } else {
                    GPSTracker gps = new GPSTracker(getContext());
                    double lat = gps.getLatitude();
                    double lng = gps.getLongitude();

                    String longitude = "Longitude: " + lat;
                    Log.v(TAG, longitude);
                    String latitude = "Latitude: " + lng;
                    Log.v(TAG, latitude);

                    Toast.makeText(getContext(), latitude + " " + longitude, Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    private boolean isGPSEnable() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private boolean hasGPSDevice(Context context) {
        final LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
            return false;
        final List<String> providers = locationManager.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    private void showEnableLocationDialog() {
        if (googleApiClient == null) {
            createGoogleApiClient();
        }
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices
                        .SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(getActivity(), 0x1);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                            Log.e("setting location error", "Error in intenTsender");
                        }
                        break;
                }
            }
        });
    }
}