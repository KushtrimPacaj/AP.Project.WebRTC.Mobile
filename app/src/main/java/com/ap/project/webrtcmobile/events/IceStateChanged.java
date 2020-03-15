package com.ap.project.webrtcmobile.events;

import org.webrtc.PeerConnection;


public class IceStateChanged {

    private String userId;
    private PeerConnection.IceConnectionState iceConnectionState;

    public IceStateChanged(String userId, PeerConnection.IceConnectionState iceConnectionState) {
        this.userId = userId;
        this.iceConnectionState = iceConnectionState;
    }

    public String getUserId() {
        return userId;
    }

    public PeerConnection.IceConnectionState getIceConnectionState() {
        return iceConnectionState;
    }
}
