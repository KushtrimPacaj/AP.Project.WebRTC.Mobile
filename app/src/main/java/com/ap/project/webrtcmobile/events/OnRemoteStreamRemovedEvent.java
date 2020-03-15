package com.ap.project.webrtcmobile.events;

import org.webrtc.MediaStream;


public class OnRemoteStreamRemovedEvent {
    private final MediaStream stream;
    private final String userId;

    public OnRemoteStreamRemovedEvent(MediaStream stream, String userId) {
        this.stream = stream;
        this.userId = userId;
    }

    public MediaStream getStream() {
        return stream;
    }

    public String getUserId() {
        return userId;
    }
}
