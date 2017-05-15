package com.proximosolutions.nvoycourier.Controller;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Isuru Tharanga on 5/7/2017.
 */

public class NvoyFirebaseInstantIDService extends FirebaseInstanceIdService {
    private  static final String REG_TOKEN = "REG_TOKEN";
    @Override
    public void onTokenRefresh() {
        String recent_token = FirebaseInstanceId.getInstance().getToken();
    }
}
