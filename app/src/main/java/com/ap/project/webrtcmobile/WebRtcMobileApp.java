package com.ap.project.webrtcmobile;

import android.app.Application;

import org.greenrobot.eventbus.EventBus;
import org.webrtc.EglBase;
import org.webrtc.Logging;
import org.webrtc.PeerConnectionFactory;


public class WebRtcMobileApp extends Application {

    private static WebRtcMobileApp instance;
    private EglBase eglBase;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initWebRTC();
        EventBus.builder().throwSubscriberException(true).installDefaultEventBus();
    }


    private void initWebRTC() {
        PeerConnectionFactory.initialize(PeerConnectionFactory.InitializationOptions.builder(this)
                .setFieldTrials("")
                .setEnableInternalTracer(true)
                .createInitializationOptions());
    }

    public static WebRtcMobileApp getInstance() {
        return instance;
    }


    public EglBase getEglBase() {
        return eglBase;
    }

    public void setEglBase(EglBase eglBase) {
        this.eglBase = eglBase;
    }
}
