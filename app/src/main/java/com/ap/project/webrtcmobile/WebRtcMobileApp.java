package com.ap.project.webrtcmobile;

import android.app.Application;


public class WebRtcMobileApp extends Application {

    private static WebRtcMobileApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static WebRtcMobileApp getInstance() {
        return instance;
    }


}
