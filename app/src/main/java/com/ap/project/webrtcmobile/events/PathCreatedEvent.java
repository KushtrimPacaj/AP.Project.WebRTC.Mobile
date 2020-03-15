package com.ap.project.webrtcmobile.events;

import com.ap.project.webrtcmobile.custom_views.DrawableObjects.SerializablePath;


public class PathCreatedEvent {

    private SerializablePath serializablePath;

    public PathCreatedEvent(SerializablePath serializablePath) {
        this.serializablePath = serializablePath;
    }

    public SerializablePath getSerializablePath() {
        return serializablePath;
    }
}
