package com.proximosolutions.nvoycourier.MainLogic;

/**
 * Created by Isuru Tharanga on 3/25/2017.
 */

public class Courier extends NvoyUser {


    private Location location;
    private boolean expressCourier;


    public void calculateFair(){}
    public void notifyDelivery(){}


    public boolean isExpressCourier() {
        return expressCourier;
    }

    public void setExpressCourier(boolean expressCourier) {
        this.expressCourier = expressCourier;
    }


    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
