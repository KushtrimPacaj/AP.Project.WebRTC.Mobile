package com.ap.project.webrtcmobile.events;

import com.ap.project.webrtcmobile.custom_views.DrawableObjects.SerializablePath;


public class PathReceivedEvent {

    private SerializablePath serializablePath;

    public PathReceivedEvent(SerializablePath serializablePath) {

        this.serializablePath = serializablePath;
    }

    public SerializablePath getSerializablePath() {
        return serializablePath;
    }
}
