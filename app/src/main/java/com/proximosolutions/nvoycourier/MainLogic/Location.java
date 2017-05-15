package com.proximosolutions.nvoycourier.MainLogic;

import java.io.Serializable;

/**
 * Created by Isuru Tharanga on 3/25/2017.
 */

public class Location implements Serializable {
    private String longitude;
    private String latitude;

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
