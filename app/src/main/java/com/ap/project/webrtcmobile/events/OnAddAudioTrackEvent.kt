package com.ap.project.webrtcmobile.events

import org.webrtc.RtpReceiver

class OnAddAudioTrackEvent(val userId: String, val rtpReceiver: RtpReceiver)