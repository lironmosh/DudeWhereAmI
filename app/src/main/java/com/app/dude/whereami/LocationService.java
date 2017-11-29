package com.app.dude.whereami;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by liron.moshkovich on 11/28/17.
 */

public class LocationService implements LocationListener, LifecycleObserver {

    private LocationManager locationManager;
    private OnCurrentPlaceChanged callback;

    @SuppressLint("MissingPermission")
    public LocationService(LocationManager locationManager, OnCurrentPlaceChanged callback){
        this.locationManager = locationManager;
        this.callback = callback;

        if(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null){
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            callback.onCurrentPlaceChanged(location);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        callback.onCurrentPlaceChanged(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @SuppressLint("MissingPermission")
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void connectListener() {
        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void disconnectListener() {
        locationManager.removeUpdates(this);
    }
}
