package com.proximosolutions.nvoycourier.MainLogic;

import java.io.Serializable;

/**
 * Created by Isuru Tharanga on 3/25/2017.
 */

public class NvoyUser implements Serializable{
    private String userID;
    private String firstName;
    private String lastName;
    private String contactNumber;
    private String nic;
    private boolean active;



    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }



    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

}
