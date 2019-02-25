package com.pbd.housecure.housecure;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import android.location.LocationListener;


public class GPSTracker extends Service implements LocationListener {
    private final Context context;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    Location location;
    double latitude, longitude;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BETWEEN_UPDATES = 1000 * 60 * 1; // 1 minute

    protected LocationManager locationManager;

    public GPSTracker(Context context) {
        this.context = context;
        getLocation();
    }

    @Override
    public void onLocationChanged(Location location) {
        float bestAccuracy = -1f;
        if (location.getAccuracy() != 0.0f
                && location.getAccuracy() < bestAccuracy) {
            locationManager.removeUpdates((android.location.LocationListener) this);
        }
        bestAccuracy = location.getAccuracy();
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
