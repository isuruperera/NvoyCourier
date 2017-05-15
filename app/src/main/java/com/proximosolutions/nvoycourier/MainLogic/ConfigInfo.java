package com.proximosolutions.nvoycourier.MainLogic;

/**
 * Created by Isuru Tharanga on 5/13/2017.
 */

public class ConfigInfo {
    private int normalDeliveryChargePerKM;
    private int normalDeliveryFixedCharge;
    private int expressDeliveryChargePerKM;
    private int expressDeliveryFixedCharge;


    public int getNormalDeliveryChargePerKM() {
        return normalDeliveryChargePerKM;
    }

    public void setNormalDeliveryChargePerKM(int normalDeliveryChargePerKM) {
        this.normalDeliveryChargePerKM = normalDeliveryChargePerKM;
    }

    public int getNormalDeliveryFixedCharge() {
        return normalDeliveryFixedCharge;
    }

    public void setNormalDeliveryFixedCharge(int normalDeliveryFixedCharge) {
        this.normalDeliveryFixedCharge = normalDeliveryFixedCharge;
    }

    public int getExpressDeliveryChargePerKM() {
        return expressDeliveryChargePerKM;
    }

    public void setExpressDeliveryChargePerKM(int expressDeliveryChargePerKM) {
        this.expressDeliveryChargePerKM = expressDeliveryChargePerKM;
    }

    public int getExpressDeliveryFixedCharge() {
        return expressDeliveryFixedCharge;
    }

    public void setExpressDeliveryFixedCharge(int expressDeliveryFixedCharge) {
        this.expressDeliveryFixedCharge = expressDeliveryFixedCharge;
    }
}
