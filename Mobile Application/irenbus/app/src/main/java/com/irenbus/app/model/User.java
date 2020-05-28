package com.irenbus.app.model;

public class User {

    private String id, fullName, imageURL, userType, busCode, currLocation;

    public User(String id, String fullName, String imageURL, String userType) {
        this.id = id;
        this.fullName = fullName;
        this.imageURL = imageURL;
        this.userType = userType;
        this.currLocation = null;
        this.busCode = null;
    }

    public User(String id, String fullName, String imageUrl, String userType, String busCode) {
        this.id = id;
        this.fullName = fullName;
        this.imageURL = imageUrl;
        this.userType = userType;
        this.currLocation = null;
        this.busCode = busCode;
    }

    public User(){ }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getImageUrl() {
        return imageURL;
    }

    public void setImageUrl(String imageUrl) {
        this.imageURL = imageUrl;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getCurrLocation() { return currLocation; }

    public void setCurrLocation(String currLocation) { this.currLocation = currLocation; }

    public String getBusCode() {
        return busCode;
    }

    public void setBusCode(String busCode) {
        this.busCode = busCode;
    }
}
