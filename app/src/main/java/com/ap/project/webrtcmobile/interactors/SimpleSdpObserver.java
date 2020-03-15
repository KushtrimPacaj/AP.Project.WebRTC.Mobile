package com.ap.project.webrtcmobile.interactors;

import android.util.Log;

import com.ap.project.webrtcmobile.utils.LogTag;

import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;


public class SimpleSdpObserver implements SdpObserver {

    @Override
    public void onCreateSuccess(SessionDescription sdp) {
    }

    @Override
    public void onSetSuccess() {

    }

    @Override
    public void onCreateFailure(String error) {
        Log.d(LogTag.CALLS, "Sdp onCreateFailure " + error);
    }

    @Override
    public void onSetFailure(String error) {
        Log.d(LogTag.CALLS, "Sdp onSetFailure " + error);
    }
}
