package com.tang.mexicomaven.location;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class LocationService extends IntentService {
    private ArrayList<String> PROVIDER_ARRAY;

    public static boolean isDestory;
    private String locationProvider;
    private android.location.LocationManager locationManager;
    private LocationListener gpsLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            getBestLocationProvider();
        }

        @Override
        public void onProviderDisabled(String provider) {
            getBestLocationProvider();
        }
    };
    private LocationListener networkLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            getBestLocationProvider();
        }

        @Override
        public void onProviderDisabled(String provider) {
            getBestLocationProvider();
        }
    };
    private LocationListener passiveLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            getBestLocationProvider();
        }

        @Override
        public void onProviderDisabled(String provider) {
            getBestLocationProvider();
        }
    };

    public LocationService() {
        super("GPS");
        PROVIDER_ARRAY = new ArrayList<>();
        PROVIDER_ARRAY.add(android.location.LocationManager.GPS_PROVIDER);
        PROVIDER_ARRAY.add(android.location.LocationManager.NETWORK_PROVIDER);
        PROVIDER_ARRAY.add(android.location.LocationManager.PASSIVE_PROVIDER);
        isDestory = false;
    }

    private synchronized void getBestLocationProvider() {
        if (locationManager == null) {
            locationProvider = null;
            return;
        }

        List<String> providers = locationManager.getAllProviders();
        if (providers == null || providers.size() <= 0) {
            locationProvider = null;
            return;
        }

        String bestProvider = null;
        Location bestLocation = null;
        for (String provider : providers) {
            if ((provider != null) && (PROVIDER_ARRAY.contains(provider))) {
                @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(provider);
                if (location == null) {
                    continue;
                }

                if (bestLocation == null) {
                    bestLocation = location;
                    bestProvider = provider;
                    continue;
                }

                if (Float.valueOf(location.getAccuracy()).compareTo(bestLocation.getAccuracy()) >= 0) {
                    bestLocation = location;
                    bestProvider = provider;
                }
            }
        }

        locationProvider = bestProvider;
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onHandleIntent(Intent intent) {
        try {
        locationProvider = null;
        locationManager = null;
        locationManager = (android.location.LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            return;
        }

        List<String> allProviders = locationManager.getAllProviders();
        if (allProviders != null&&allProviders.size()!=0) {
            for (String provider : allProviders) {
                if ((provider != null) && (PROVIDER_ARRAY.contains(provider))) {
                    if (android.location.LocationManager.GPS_PROVIDER.equals(provider)) {
                        locationManager.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER, LocationManager.FAST_UPDATE_INTERVAL, 0, gpsLocationListener);
                    } else if (android.location.LocationManager.NETWORK_PROVIDER.equals(provider)) {
                        locationManager.requestLocationUpdates(android.location.LocationManager.NETWORK_PROVIDER, LocationManager.FAST_UPDATE_INTERVAL, 0, networkLocationListener);
                    } else if (android.location.LocationManager.PASSIVE_PROVIDER.equals(provider)) {
                        locationManager.requestLocationUpdates(android.location.LocationManager.PASSIVE_PROVIDER, LocationManager.FAST_UPDATE_INTERVAL, 0, passiveLocationListener);
                    }
                }
            }
        }

        while (!isDestory) {
            getBestLocationProvider();
            updateLocation();
            if (isDestory) return;
            if ((locationProvider != null) && (PROVIDER_ARRAY.contains(locationProvider))) {//如果成功获取到了位置
                isDestory = true;
            } else {
                try {
                    Thread.sleep(LocationManager.FAST_UPDATE_INTERVAL);
                } catch (Exception ex) {
                }
            }
        }

        }catch (Exception e){
        }
    }

    private void updateLocation() {
        if ((locationProvider != null) && (!locationProvider.equals("")) && (PROVIDER_ARRAY.contains(locationProvider))) {
            try {
                @SuppressLint("MissingPermission") Location currentLocation = locationManager.getLastKnownLocation(locationProvider);
                if (currentLocation != null) {
                    final double newLatitude = currentLocation.getLatitude();
                    final double newLongitude = currentLocation.getLongitude();
                    final float accuracy = currentLocation.getAccuracy();
                    if (!isWrongPosition(newLatitude, newLongitude))
                        LocationManager.recordLocation(newLatitude, newLongitude, "gps");
                    if (!isWrongPosition(newLatitude, newLongitude)) isDestory = true;
                }
            } catch (Exception ex) {
            }
        }
    }

    public static boolean isWrongPosition(double latitude, double longitude) {
        if (Math.abs(latitude) < 0.01 && Math.abs(longitude) < 0.1) return true;
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestory = true;

        if ((locationManager != null) && (gpsLocationListener != null)) {
            locationManager.removeUpdates(gpsLocationListener);
        }

        if ((locationManager != null) && (networkLocationListener != null)) {
            locationManager.removeUpdates(networkLocationListener);
        }

        if ((locationManager != null) && (passiveLocationListener != null)) {
            locationManager.removeUpdates(passiveLocationListener);
        }
    }
}
