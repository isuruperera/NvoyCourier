package com.proximosolutions.nvoycourier.Controller;

/**
 * Created by Isuru Tharanga on 3/26/2017.
 */

public class ChildRow {
    private int icon;
    private String text;
    private String status;
    public ChildRow(String text,String status, int icon) {
        this.text = text;
        this.icon = icon;
        this.status = status;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
