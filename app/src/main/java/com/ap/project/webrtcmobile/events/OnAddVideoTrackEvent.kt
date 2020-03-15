package com.ap.project.webrtcmobile.events

import org.webrtc.RtpReceiver

class OnAddVideoTrackEvent(val userId: String, val rtpReceiver: RtpReceiver)
