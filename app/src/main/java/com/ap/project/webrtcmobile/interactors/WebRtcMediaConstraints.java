package com.ap.project.webrtcmobile.interactors;


import org.webrtc.MediaConstraints;


class WebRtcMediaConstraints {


    static MediaConstraints getSDPConstraints(boolean iceRestart) {
        MediaConstraints sdpMediaConstraints = new MediaConstraints();
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("IceRestart", String.valueOf(iceRestart)));
        return sdpMediaConstraints;
    }

    static MediaConstraints getAudioConstraint() {
        MediaConstraints audioConstraints = new MediaConstraints();
        audioConstraints.optional.add(new MediaConstraints.KeyValuePair("googEchoCancellation", "true"));
        audioConstraints.optional.add(new MediaConstraints.KeyValuePair("googDAEchoCancellation", "true"));
        audioConstraints.optional.add(new MediaConstraints.KeyValuePair("googAutoGainControl", "false"));
        audioConstraints.optional.add(new MediaConstraints.KeyValuePair("googNoiseSuppression", "true"));
        audioConstraints.optional.add(new MediaConstraints.KeyValuePair("googHighpassFilter", "true"));
        return audioConstraints;
    }

}
