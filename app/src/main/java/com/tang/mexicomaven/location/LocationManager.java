package com.tang.mexicomaven.location;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.tang.mexicomaven.AppApplication;

import java.util.List;


public class LocationManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final int FAST_UPDATE_INTERVAL = 20000;
    private static final int FATEST_INTERVAL = 5000;
    public static final int FAST_DISPLACEMENT = 10;
    private GoogleApiClient mGoogleApiClient;
    public static LocationManager manager;

    public static LocationManager getInstance() {
        return manager;
    }

    static LocationCallBack mLocationCallBack;


    public static void initGPS(LocationCallBack locationCallBack, Context mContext) {
        if (manager != null && manager.mGoogleApiClient != null) return;
        mLocationCallBack = locationCallBack;
        manager = new LocationManager();
        manager.mGoogleApiClient = new GoogleApiClient.Builder(AppApplication.getInstance().getApplicationContext())
                .addConnectionCallbacks(manager)
                .addOnConnectionFailedListener(manager)
                .addApi(LocationServices.API)
                .build();
        manager.mGoogleApiClient.connect();
        try {
            new Handler().post(() -> mContext.startService(new Intent(mContext, LocationService.class)));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void stopGPS() {
        if (manager == null) return;
        pauseGPS();
        manager.mGoogleApiClient = null;
        manager = null;
    }

    public static void pauseGPS() {
        if (manager == null || manager.mGoogleApiClient == null) return;
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(manager.mGoogleApiClient, manager);
            if (manager.mGoogleApiClient.isConnected() || manager.mGoogleApiClient.isConnecting())
                manager.mGoogleApiClient.disconnect();
            manager.mGoogleApiClient = null;
        } catch (Exception e) {
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, createFastLocationRequest(), this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) return;
        recordLocation(location.getLatitude(), location.getLongitude(), "google");
        stopGPS();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private static LocationRequest createFastLocationRequest() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(FAST_UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(FAST_DISPLACEMENT);
        return mLocationRequest;
    }

    public static void recordLocation(double latitude, double longitude, String type) {
        if (!type.equals("google")) {
            mLocationCallBack.onLocationGps(latitude, longitude);
        } else {
            mLocationCallBack.onLocationGoogle(latitude, longitude);
        }
    }

    public static String getAddress(double latitude, double longitude, Activity activity) {
        Geocoder ge = new Geocoder(activity);
        String address = "";

        try {
            List<Address> addList = ge.getFromLocation(latitude, longitude, 1);
            if (addList != null && addList.size() > 0) {
                for(int i = 0; i < addList.size(); ++i) {
                    Address ad = (Address)addList.get(i);
                    address = ad.getAddressLine(0);
                }
            }
        } catch (Exception var11) {
            var11.printStackTrace();
        }

        return address;
    }

}