package com.ap.project.webrtcmobile.events;

import org.webrtc.AudioTrack;
import org.webrtc.VideoTrack;

public class OnLocalStreamChangeEvent {

    private AudioTrack audioTrack;
    private VideoTrack videoTrack;

    public OnLocalStreamChangeEvent(AudioTrack audioTrack, VideoTrack videoTrack) {
        this.audioTrack = audioTrack;
        this.videoTrack = videoTrack;
    }

    public AudioTrack getAudioTrack() {
        return audioTrack;
    }

    public VideoTrack getVideoTrack() {
        return videoTrack;
    }
}
