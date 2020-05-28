package com.irenbus.app.model;

import android.util.Log;

import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class BusInfo {

    String distanceFromUser, timeFromUser, busRoute, busNumberPlate, currLocation, destLocation;
    GeoApiContext geoApiContext;
    boolean updateComplete = false;

    public BusInfo(Bus bus){
        this.busRoute = bus.getBusRoute();
        this.busNumberPlate = bus.getNumberPlate();
        this.distanceFromUser = "";
        this.timeFromUser = "";
        this.currLocation="";
        this.destLocation="";

        if(geoApiContext == null){
            geoApiContext = new GeoApiContext.Builder().apiKey("AIzaSyA2oDioGs6wGU6A4RFr379CjC4xdMyfjAw").build();
        }

    }

    public String getBusRoute() {
        return busRoute;
    }

    public String getBusNumberPlate() {
        return busNumberPlate;
    }

    public String getDistanceFromUser() {
        return distanceFromUser;
    }

    private void setDistanceFromUser(String distanceFromUser) {
        this.distanceFromUser = distanceFromUser;
    }

    public String getTimeFromUser() {
        return timeFromUser;
    }

    private void setTimeFromUser(String timeFromUser) {
        this.timeFromUser = timeFromUser;
    }

    public String getCurrLocation() {
        return currLocation;
    }

    public void setCurrLocation(String currLocation) {
        this.currLocation = currLocation;
    }

    public String getDestLocation() {
        return destLocation;
    }

    public void setDestLocation(String destLocation) {
        this.destLocation = destLocation;
    }

    public void update(){
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                Double.parseDouble(getCurrLocation().split(",")[0]),
                Double.parseDouble(getCurrLocation().split(",")[1]));
        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);

        directions.alternatives(true);
        directions.origin(new com.google.maps.model.LatLng(
                Double.parseDouble(getDestLocation().split(",")[0]),
                Double.parseDouble(getDestLocation().split(",")[1])));
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                setTimeFromUser(result.routes[0].legs[0].duration+"");
                setDistanceFromUser(result.routes[0].legs[0].distance+"");
                updateComplete = true;
            }
            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );
                updateComplete = true;
            }
        });
    }

    public boolean isUpdateComplete(){
        return updateComplete;
    }

}
