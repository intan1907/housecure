package com.pbd.housecure.housecure;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import android.location.LocationListener;


public class GPSTracker extends Service implements LocationListener {
    private Context context;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    Location location;
    double latitude, longitude;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BETWEEN_UPDATES = 100 * 60 * 1; // 5 minute

    protected LocationManager locationManager;

    public GPSTracker() {
    }

    public GPSTracker(Context context) {
        this.context = context;
        Log.d("Location", "Location service enabled");
        getLocation();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("Location", "Location Changed");
        /*float bestAccuracy = -1f;
        if (location.getAccuracy() != 0.0f
                && location.getAccuracy() < bestAccuracy) {
            locationManager.removeUpdates((android.location.LocationListener) this);
        }
        bestAccuracy = location.getAccuracy();*/
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean status = mPreferences.getBoolean(context.getString(R.string.pref_sensors_key), false);
        if (status) {
            return;
        }
        String loc = mPreferences.getString(context.getString(R.string.pref_location_key), "0,0");
        String[] coordinate;
        try {
            coordinate = loc.split(",");
        } catch (NullPointerException e) {
            return;
        }
        double lat = Double.valueOf(coordinate[0]);
        double lng = Double.valueOf(coordinate[1]);
        Location oldLocation = new Location(LocationManager.GPS_PROVIDER);
        oldLocation.setLatitude(lat);
        oldLocation.setLongitude(lng);
        Log.d("Location Update", loc);
        double distance = oldLocation.distanceTo(location);
        Log.d("Location Update", String.format("%.2f", distance));
        if (distance > 1000) {
            mPreferences.edit().putBoolean(context.getString(R.string.pref_sensors_key), true).apply();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int x, int y) {
        Log.d("Location", "Location Service Intent Called");
        context = getApplicationContext();
        getLocation();
        return START_NOT_STICKY;
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

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public Location getLocation() {
        try {

            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            // getting gps status
            isGPSEnabled = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                Toast.makeText(context, "No network provider is enabled", Toast.LENGTH_SHORT);
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    if (checkPermission()) {
                        locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER,
                                MIN_TIME_BETWEEN_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                                (android.location.LocationListener) this);
                    }
                    Log.d("Network enabled", "Network enabled");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                MIN_TIME_BETWEEN_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                                (android.location.LocationListener) this);
                        Log.d("GPS enabled", "GPS enabled");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("GPS Tracker", e.toString());
        }
        return location;
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates((android.location.LocationListener) GPSTracker.this);
        }
    }

    public double getLatitude(){
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();

        }
        return longitude;
    }

    public boolean isCanGetLocation() {
        return canGetLocation;
    }

    public float getAccuracy(){
        return location.getAccuracy();
    }
}
