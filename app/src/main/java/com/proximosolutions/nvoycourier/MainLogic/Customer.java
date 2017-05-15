package com.proximosolutions.nvoycourier.MainLogic;

import java.io.Serializable;

/**
 * Created by Isuru Tharanga on 3/28/2017.
 */

public class Customer extends NvoyUser implements Serializable {
    private Location location;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
