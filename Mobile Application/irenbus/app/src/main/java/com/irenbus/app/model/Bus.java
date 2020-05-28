package com.irenbus.app.model;

public class Bus {

    String busCode, number, route, plate;

    public Bus(String busCode, String number, String route, String plate) {
        this.busCode = busCode;
        this.number = number;
        this.route = route;
        this.plate = plate;
    }

    public Bus() {

    }

    public String getBusCode() {
        return busCode;
    }

    public void setBusCode(String busCode) {
        this.busCode = busCode;
    }

    public String getBusNumber() {
        return number;
    }

    public void setBusNumber(String number) {
        this.number = number;
    }

    public String getBusRoute() {
        return route;
    }

    public void setBusRoute(String route) {
        this.route = route;
    }

    public String getNumberPlate() {
        return plate;
    }

    public void setNumberPlate(String plate) {
        this.plate = plate;
    }
}
