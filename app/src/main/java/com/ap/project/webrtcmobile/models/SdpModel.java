package com.ap.project.webrtcmobile.models;

import org.webrtc.SessionDescription;

public class SdpModel {

    private String sdp;
    private String type;

    public SdpModel(SessionDescription sessionDescription) {
        sdp = sessionDescription.description;
        type = sessionDescription.type.canonicalForm();
    }

    public SessionDescription toWebRtcType() {
        return new SessionDescription(SessionDescription.Type.fromCanonicalForm(type), sdp);
    }

    @Override
    public String toString() {
        return "SdpModel{" +
                "sdp='" + sdp + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}

