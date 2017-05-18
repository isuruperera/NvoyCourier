package com.proximosolutions.nvoycourier.MainLogic;

import java.io.Serializable;

/**
 * Created by Isuru Tharanga on 3/25/2017.
 */

public class Parcel implements Serializable {
    private String parcelID;
    private String itemDescription;
    private String senderID;
    private String receiverID;
    private String carrierID;
    private double deliveryFair;
    private Location currentLocation;
    private int status;
    private String lastUpdated;

    public static final int NEW = 1;
    public static final int CANCELLED = 2;
    public static final int ACCEPTED = 3;
    public static final int IN_TRANSIT = 4;
    public static final int DELIVERED = 5;
    public static final int TIME_OUT = 6;
    public static final int PICKUP = 7;
    public static final int MARKED_DELIVERED = 8;
    public static final int CUST_MARKED_NOT_DELIVERED = 9;
    public static final int CUST_MARKED_NOT_COLLECTED = 10;

    public String getParcelID() {
        return parcelID;
    }

    public void setParcelID(String parcelID) {
        this.parcelID = parcelID;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getCarrierID() {
        return carrierID;
    }

    public void setCarrierID(String carrierID) {
        this.carrierID = carrierID;
    }

    public double getDeliveryFair() {
        return deliveryFair;
    }

    public void setDeliveryFair(double deliveryFair) {
        this.deliveryFair = deliveryFair;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
