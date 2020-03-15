package com.ap.project.webrtcmobile.events;

import org.webrtc.PeerConnection;


public class IceGatheringStateChanged {

    private String userId;
    private PeerConnection.IceGatheringState iceGatheringState;


    public IceGatheringStateChanged(String userId, PeerConnection.IceGatheringState iceGatheringState) {
        this.userId = userId;
        this.iceGatheringState = iceGatheringState;
    }

    public String getUserId() {
        return userId;
    }

    public PeerConnection.IceGatheringState getIceGatheringState() {
        return iceGatheringState;
    }
}


