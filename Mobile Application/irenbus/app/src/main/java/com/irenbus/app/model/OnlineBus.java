package com.irenbus.app.model;

public class OnlineBus {
    String busCode, currLocation, currDriverId;

    public OnlineBus(String busCode, String currLocation, String currDriverId) {
        this.busCode = busCode;
        this.currLocation = currLocation;
        this.currDriverId = currDriverId;
    }

    public OnlineBus() {
    }

    public String getBusCode() {
        return busCode;
    }

    public void setBusCode(String busCode) {
        this.busCode = busCode;
    }

    public String getCurrLocation() {
        return currLocation;
    }

    public void setCurrLocation(String currLocation) {
        this.currLocation = currLocation;
    }

    public String getCurrDriverId() {
        return currDriverId;
    }

    public void setCurrDriverId(String currDriverId) {
        this.currDriverId = currDriverId;
    }
}
