package com.ap.project.webrtcmobile.interactors;

import android.util.Log;

import com.ap.project.webrtcmobile.WebRtcMobileApp;
import com.ap.project.webrtcmobile.events.OnLocalStreamChangeEvent;
import com.ap.project.webrtcmobile.utils.LogTag;

import org.greenrobot.eventbus.EventBus;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * WebRTC stream management
 * This class is used to create audio and video tracks using WebRTC api.
 */
public class LocalMediaStreamInteractor {
    private PeerConnectionFactory factory;
    private VideoSource videoSource;
    private AudioSource audioSource;
    private CameraVideoCapturer videoCapturer;

    private AudioTrack localAudioTrack;
    private VideoTrack localVideoTrack;

    private SurfaceTextureHelper surfaceTextureHelper;


    public LocalMediaStreamInteractor(PeerConnectionFactory factory) {
        this.factory = factory;
        surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread",
                WebRtcMobileApp.getInstance().getEglBase().getEglBaseContext()
        );

    }


    public void addLocalVideoAndAudioTrack(PeerConnection peerConnection) {
        List<String> mediaStreamLabels = Collections.singletonList("ARDAMS");

        try {
            AudioTrack audioTrack = getOrCreateAudioTrack();
            peerConnection.addTrack(audioTrack, mediaStreamLabels);

            VideoTrack videoTrack = getOrCreateVideoTrack();
            peerConnection.addTrack(videoTrack, mediaStreamLabels);

            EventBus.getDefault().post(new OnLocalStreamChangeEvent(audioTrack, videoTrack));
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }
    }

    public AudioTrack getOrCreateAudioTrack() {
        if (localAudioTrack != null) {
            return localAudioTrack;
        } else {
            return createAudioTrack();
        }
    }

    public VideoTrack getOrCreateVideoTrack() {
        if (localVideoTrack != null) {
            return localVideoTrack;
        } else {
            return createVideoTrack();
        }
    }



    public void dispose() {
        Log.d(LogTag.CALLS, "dispose in media stream");
        if (audioSource != null) {
            audioSource.dispose();
            audioSource = null;
        }
        Log.d(LogTag.CALLS, "Stopping capture.");
        if (videoCapturer != null) {
            try {
                videoCapturer.stopCapture();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            videoCapturer.dispose();
            videoCapturer = null;
        }
        Log.d(LogTag.CALLS, "Closing video source.");
        if (videoSource != null) {
            videoSource.dispose();
            videoSource = null;
        }

        if (localVideoTrack != null) {
            localVideoTrack.dispose();
            localVideoTrack = null;
        }

        if (localAudioTrack != null) {
            localAudioTrack.dispose();
            localAudioTrack = null;
        }

    }



    private AudioTrack createAudioTrack() {
        Log.d(LogTag.CALLS, "createAudioTrack");
        AudioSource audioSource = createLocalAudioSource();
        this.audioSource = audioSource;
        String AUDIO_TRACK_ID = UUID.randomUUID().toString();
        localAudioTrack = factory.createAudioTrack(AUDIO_TRACK_ID, audioSource);
        localAudioTrack.setEnabled(true);
        return localAudioTrack;
    }


    private AudioSource createLocalAudioSource() {
        Log.d(LogTag.CALLS, "createLocalAudioSource");
        return factory.createAudioSource(WebRtcMediaConstraints.getAudioConstraint());
    }


    private VideoTrack createVideoTrack() {
        Log.d(LogTag.CALLS, "createVideoTrack");
        VideoSource videoSource = createLocalVideoSource();
        String VIDEO_TRACK_ID = UUID.randomUUID().toString();
        localVideoTrack = factory.createVideoTrack(VIDEO_TRACK_ID, videoSource);
        localVideoTrack.setEnabled(true);
        return localVideoTrack;
    }


    private VideoSource createLocalVideoSource() {

        Log.d(LogTag.CALLS, "createLocalVideoSource");
        if (videoSource != null) {
            videoSource.dispose();
        }

        videoCapturer = getVideoCapturer();
        videoSource = factory.createVideoSource(videoCapturer.isScreencast());
        videoCapturer.initialize(surfaceTextureHelper,
                WebRtcMobileApp.getInstance().getApplicationContext(),
                videoSource.getCapturerObserver());
        videoCapturer.startCapture(1280, 720, 30);

        return videoSource;
    }

    private CameraVideoCapturer getVideoCapturer() {
        Log.d(LogTag.CALLS, "getVideoCapturer");
        CameraEnumerator enumerator = new Camera1Enumerator(true);
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find front facing camera
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                CameraVideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                CameraVideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        throw new RuntimeException("Could not start camera. It might be in use by another process.");
    }


    void setFactory(PeerConnectionFactory factory) {
        this.factory = factory;
    }

    public void switchCamera() {
        videoCapturer.switchCamera(null);
    }
}
