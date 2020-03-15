package com.ap.project.webrtcmobile.interactors;

import android.util.Log;

import com.ap.project.webrtcmobile.events.CreateOfferForUserEvent;
import com.ap.project.webrtcmobile.events.IceGatheringStateChanged;
import com.ap.project.webrtcmobile.events.IceStateChanged;
import com.ap.project.webrtcmobile.events.OnAddAudioTrackEvent;
import com.ap.project.webrtcmobile.events.OnAddVideoTrackEvent;
import com.ap.project.webrtcmobile.events.OnIceCandidateGeneratedEvent;
import com.ap.project.webrtcmobile.events.OnRemoteStreamRemovedEvent;
import com.ap.project.webrtcmobile.utils.LogTag;

import org.greenrobot.eventbus.EventBus;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.MediaStreamTrack;
import org.webrtc.PeerConnection;
import org.webrtc.RtpReceiver;

import java.util.Arrays;


public class PeerConnectionObserver implements PeerConnection.Observer {


    protected String userId;

    public PeerConnectionObserver(String userId) {
        this.userId = userId;
    }

    @Override
    public void onIceConnectionChange(PeerConnection.IceConnectionState newState) {
        Log.d(LogTag.CALLS, "ice connection for user " + userId + " changed to " + newState);
        EventBus.getDefault().post(new IceStateChanged(userId, newState));
    }

    @Override
    public void onIceCandidate(IceCandidate candidate) {
        EventBus.getDefault().post(new OnIceCandidateGeneratedEvent(candidate, userId));
    }

    @Override
    public void onAddStream(MediaStream stream) {
        Log.d(LogTag.CALLS, "Stream added for user with userId " + userId);
    }

    @Override
    public void onRemoveStream(MediaStream stream) {
        Log.d(LogTag.CALLS, "stream removed for user with id " + userId);
        EventBus.getDefault().post(new OnRemoteStreamRemovedEvent(stream, userId));
    }

    @Override
    public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
        Log.d(LogTag.CALLS, "onAddTrack for user with userId " + userId);
        MediaStreamTrack mediaStreamTrack = rtpReceiver.track();
        if (mediaStreamTrack != null && mediaStreamTrack.kind().equals(MediaStreamTrack.VIDEO_TRACK_KIND)) {
            EventBus.getDefault().post(new OnAddVideoTrackEvent(userId, rtpReceiver));
        } else {
            EventBus.getDefault().post(new OnAddAudioTrackEvent(userId, rtpReceiver));
        }
    }

    @Override
    public void onIceGatheringChange(PeerConnection.IceGatheringState newState) {
        Log.d(LogTag.CALLS, "ice gathering for user " + userId + " changed to " + newState);
        EventBus.getDefault().post(new IceGatheringStateChanged(userId, newState));
    }


    @Override
    public void onRenegotiationNeeded() {
        Log.d(LogTag.CALLS, "onRenegotiationNeeded for user " + userId);
        EventBus.getDefault().post(new CreateOfferForUserEvent(userId));
    }

    @Override
    public void onSignalingChange(PeerConnection.SignalingState newState) {
        Log.d(LogTag.CALLS, "onSignalingChange to " + newState + ", user " + userId);
    }

    @Override
    public void onIceConnectionReceivingChange(boolean receiving) {
        Log.d(LogTag.CALLS, "onIceConnectionReceivingChange  " + receiving + ", user " + userId);
    }


    @Override
    public void onIceCandidatesRemoved(IceCandidate[] candidates) {
        Log.d(LogTag.CALLS, "onIceGatheringChange  " + Arrays.toString(candidates) + ", user " + userId);
    }

    @Override
    public void onDataChannel(DataChannel dataChannel) {
        Log.d(LogTag.CALLS, "onIceGatheringChange  " + dataChannel + ", user " + userId);
    }


}