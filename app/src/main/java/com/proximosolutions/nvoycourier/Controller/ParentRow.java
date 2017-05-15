package com.proximosolutions.nvoycourier.Controller;

/**
 * Created by Isuru Tharanga on 3/26/2017.
 */

public class ParentRow {
    private String name;
    private ChildRow child;

    public ParentRow(String name, ChildRow childList) {
        this.name = name;
        this.setChild(childList);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public ChildRow getChild() {
        return child;
    }

    public void setChild(ChildRow child) {
        this.child = child;
    }
}
