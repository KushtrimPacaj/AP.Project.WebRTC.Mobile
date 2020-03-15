package com.ap.project.webrtcmobile.events;

import org.webrtc.IceCandidate;


public class OnIceCandidateGeneratedEvent {
    private final IceCandidate candidate;
    private final String userId;


    public OnIceCandidateGeneratedEvent(IceCandidate candidate, String userId) {
        this.candidate = candidate;
        this.userId = userId;
    }

    public IceCandidate getCandidate() {
        return candidate;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "OnIceCandidateGeneratedEvent{" +
                "candidate=" + candidate +
                ", userId='" + userId + '\'' +
                '}';
    }
}
