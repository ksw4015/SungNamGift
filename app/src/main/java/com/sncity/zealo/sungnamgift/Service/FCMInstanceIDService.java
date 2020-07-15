package com.sncity.zealo.sungnamgift.Service;

import android.content.Intent;

import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by zealo on 2017-12-27.
 */

public class FCMInstanceIDService extends FirebaseInstanceIdService{

    private static String refreshToken = null;

    public FCMInstanceIDService() {

    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
    }
}
