package com.ap.project.webrtcmobile.interactors;

import android.util.Log;

import com.ap.project.webrtcmobile.WebRtcMobileApp;
import com.ap.project.webrtcmobile.events.CreateOfferForUserEvent;
import com.ap.project.webrtcmobile.interactors.interfaces.SignalingChannel;
import com.ap.project.webrtcmobile.models.IceCandidateModel;
import com.ap.project.webrtcmobile.models.WebrtcOfferAnswerExchangeModel;
import com.ap.project.webrtcmobile.utils.AudioDeviceModuleUtils;
import com.ap.project.webrtcmobile.utils.LogTag;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.webrtc.AudioTrack;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.Logging;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoderFactory;
import org.webrtc.VideoTrack;
import org.webrtc.audio.AudioDeviceModule;

import java.util.EnumSet;


public class WebRtcInteractor {


    private static final boolean ENABLE_VERBOSE_WEBRTC_LOGGING = false;

    private PeerConnectionInteractor peerConnectionInteractor;
    private IceCandidateInteractor iceCandidateInteractor;
    private PeerConnectionFactory factory;
    private LocalMediaStreamInteractor localMediaStreamInteractor;
    private SignalingChannel signalingChannel;


    public WebRtcInteractor(SignalingChannel signalingChannel) {
        Log.d(LogTag.CALLS, "Created WebRtcInteractor");
        this.signalingChannel = signalingChannel;
        factory = createPeerConnectionFactory();
        localMediaStreamInteractor = new LocalMediaStreamInteractor(factory);
        peerConnectionInteractor = new PeerConnectionInteractor(factory, localMediaStreamInteractor);
        iceCandidateInteractor = new IceCandidateInteractor();
        EventBus.getDefault().register(this);
        enableAdvancedLoggingIfRequested();
    }


    public void onUserJoined(String joinedUserId) {
        Log.d(LogTag.CALLS, "onUserJoined " + joinedUserId);
        peerConnectionInteractor.disposePeerConnectionsForUser(joinedUserId);
        PeerConnectionInteractor.PeerConnectionData peerConnectionData = peerConnectionInteractor.getOrCreatePeerConnectionForUser(joinedUserId);
        peerConnectionData.setCanRenegotiate(false);
        createOfferForUser(joinedUserId, peerConnectionData, false);

    }

    private void createOfferForUser(String joinedUserId, PeerConnectionInteractor.PeerConnectionData peerConnectionData, boolean restartIce) {
        Log.d(LogTag.CALLS, "createOfferForParticipant " + joinedUserId);
        PeerConnection peerConnection = peerConnectionData.getPeerConnection();
        localMediaStreamInteractor.addLocalVideoAndAudioTrack(peerConnection);
        peerConnection.createOffer(new SimpleSdpObserver() {
            SessionDescription sdpThatWasCreated;

            @Override
            public void onCreateSuccess(final SessionDescription sdp) {
                Log.d(LogTag.CALLS, "Offer created successfully for user " + joinedUserId + ". Setting it as local description");
                this.sdpThatWasCreated = sdp;
                peerConnection.setLocalDescription(this, sdp);
            }

            @Override
            public void onSetSuccess() {
                Log.d(LogTag.CALLS, "Offer set successfully as local description. Sending to user: " + joinedUserId);
                signalingChannel.sendOffer(joinedUserId, sdpThatWasCreated);
                peerConnectionData.setCanRenegotiate(true);
            }
        }, WebRtcMediaConstraints.getSDPConstraints(restartIce));
    }


    public void onReceivedOffer(WebrtcOfferAnswerExchangeModel model) {
        final String otherUserId = model.getFromUserId();
        Log.d(LogTag.CALLS, "onReceivedOffer " + otherUserId);
        PeerConnectionInteractor.PeerConnectionData peerConnectionData = peerConnectionInteractor.getOrCreatePeerConnectionForUser(otherUserId);
        PeerConnection peerConnection = peerConnectionData.getPeerConnection();
        if (peerConnection.iceConnectionState() == PeerConnection.IceConnectionState.FAILED ||
                peerConnection.iceConnectionState() == PeerConnection.IceConnectionState.DISCONNECTED) {
            peerConnectionInteractor.disposePeerConnectionsForUser(otherUserId);
            peerConnectionData = peerConnectionInteractor.getOrCreatePeerConnectionForUser(otherUserId);
            peerConnection = peerConnectionData.getPeerConnection();
        }

        SessionDescription offer = model.getSdpModel().toWebRtcType();

        peerConnectionData.setCanRenegotiate(false);
        PeerConnection finalPeerConnection = peerConnection;
        peerConnection.setRemoteDescription(new SimpleSdpObserver() {
            @Override
            public void onSetSuccess() {
                Log.d(LogTag.CALLS, "Offer set as remote description for user" + otherUserId);
                iceCandidateInteractor.onRemoteDescriptionSet(finalPeerConnection, otherUserId);
                localMediaStreamInteractor.addLocalVideoAndAudioTrack(finalPeerConnection);
                createAnswerForUser(otherUserId);
            }
        }, offer);
    }

    private void createAnswerForUser(final String otherUserId) {
        Log.d(LogTag.CALLS, "createAnswerForUser " + otherUserId);

        PeerConnectionInteractor.PeerConnectionData peerConnectionData = peerConnectionInteractor.getOrCreatePeerConnectionForUser(otherUserId);
        PeerConnection peerConnection = peerConnectionData.getPeerConnection();
        peerConnection.createAnswer(new SimpleSdpObserver() {
            SessionDescription sdpThatWasCreated;

            @Override
            public void onCreateSuccess(final SessionDescription sdp) {
                Log.d(LogTag.CALLS, "Answer created successfully for user " + otherUserId + ". Setting it as local description");
                this.sdpThatWasCreated = sdp;
                peerConnection.setLocalDescription(this, sdp);
            }

            @Override
            public void onSetSuccess() {
                Log.d(LogTag.CALLS, "Answer set successfully as local description. Sending to user: " + otherUserId);
                signalingChannel.sendAnswer(otherUserId, sdpThatWasCreated);
                peerConnectionData.setCanRenegotiate(true);
            }
        }, WebRtcMediaConstraints.getSDPConstraints(false));
    }

    public void onReceivedAnswer(WebrtcOfferAnswerExchangeModel model) {
        final String otherUserId = model.getFromUserId();
        Log.d(LogTag.CALLS, "onReceivedAnswer " + otherUserId);

        PeerConnectionInteractor.PeerConnectionData peerConnectionData = peerConnectionInteractor.getOrCreatePeerConnectionForUser(otherUserId);
        PeerConnection peerConnection = peerConnectionData.getPeerConnection();
        SessionDescription answer = model.getSdpModel().toWebRtcType();
        peerConnection.setRemoteDescription(new SimpleSdpObserver() {
            @Override
            public void onSetSuccess() {
                Log.d(LogTag.CALLS, "Answer from " + otherUserId + " set as remote description");
                iceCandidateInteractor.onRemoteDescriptionSet(peerConnection, otherUserId);
            }
        }, answer);
    }


    public void onIceCandidateReceived(IceCandidateModel model) {
        String otherUserId = model.getFromUserId();
        Log.d(LogTag.CALLS, "onIceCandidateReceived " + otherUserId);

        PeerConnectionInteractor.PeerConnectionData peerConnection = peerConnectionInteractor.getOrCreatePeerConnectionForUser(otherUserId);
        iceCandidateInteractor.onIceCandidateReceived(peerConnection.getPeerConnection(), model);
    }

    public AudioTrack getOrCreateAudioTrack() {
        return localMediaStreamInteractor.getOrCreateAudioTrack();
    }

    public VideoTrack getOrCreateVideoTrack() {
        return localMediaStreamInteractor.getOrCreateVideoTrack();
    }

    public void disposePeerConnectionsAndLocalStream() {
        peerConnectionInteractor.disposeAllPeerConnections();
        localMediaStreamInteractor.dispose();
    }

    @Subscribe
    public void onEvent(CreateOfferForUserEvent event) {
        String joinedUserId = event.getUserId();
        Log.d(LogTag.CALLS, "onEvent(CreateOfferForUserEvent event)  " + joinedUserId);
        PeerConnectionInteractor.PeerConnectionData peerConnectionData = peerConnectionInteractor.getPeerConnectionForUser(joinedUserId);
        if (peerConnectionData != null && peerConnectionData.getCanRenegotiate()) {
            PeerConnection.IceConnectionState iceConnectionState = peerConnectionData.getPeerConnection().iceConnectionState();
            boolean restartIce = iceConnectionState == PeerConnection.IceConnectionState.DISCONNECTED;
            createOfferForUser(joinedUserId, peerConnectionData, restartIce);
        }
    }

    public void destroy() {
        factory.dispose();
    }

    public void recreateFactory() {
        factory.dispose();
        factory = createPeerConnectionFactory();
        localMediaStreamInteractor.setFactory(factory);
        peerConnectionInteractor.setFactory(factory);
    }

    private PeerConnectionFactory createPeerConnectionFactory() {
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        final AudioDeviceModule adm = AudioDeviceModuleUtils.createLegacyAudioDevice();
        VideoEncoderFactory encoderFactory = new DefaultVideoEncoderFactory(
                WebRtcMobileApp.getInstance().getEglBase().getEglBaseContext(), true /* enableIntelVp8Encoder */, true);
        VideoDecoderFactory decoderFactory = new DefaultVideoDecoderFactory(WebRtcMobileApp.getInstance().getEglBase().getEglBaseContext());
        return PeerConnectionFactory.builder()
                .setOptions(options)
                .setAudioDeviceModule(adm)
                .setVideoEncoderFactory(encoderFactory)
                .setVideoDecoderFactory(decoderFactory)
                .createPeerConnectionFactory();
    }

    private void enableAdvancedLoggingIfRequested() {
        if (ENABLE_VERBOSE_WEBRTC_LOGGING) {
            Logging.enableTracing("logcat:", EnumSet.of(Logging.TraceLevel.TRACE_DEFAULT));
            Logging.enableLogToDebugOutput(Logging.Severity.LS_INFO);
        } else {
            Logging.enableLogToDebugOutput(Logging.Severity.LS_NONE);
        }
    }


}
