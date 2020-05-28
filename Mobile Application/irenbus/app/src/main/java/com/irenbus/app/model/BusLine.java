package com.irenbus.app.model;

public class BusLine implements Comparable<BusLine>{

    public BusLine(String id, String busLine) {
        this.id = id;
        this.busLine = busLine;
    }

    public BusLine(){

    }

    private String id, busLine;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBusLine() {
        return busLine;
    }

    public void setBusLine(String busLine) {
        this.busLine = busLine;
    }

    @Override
    public int compareTo(BusLine b){
        return this.busLine.compareTo(b.busLine);
    }
}
