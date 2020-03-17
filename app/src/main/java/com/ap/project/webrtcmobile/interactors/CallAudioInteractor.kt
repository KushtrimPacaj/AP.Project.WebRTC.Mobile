package com.ap.project.webrtcmobile.interactors

import android.content.Context
import android.media.AudioManager
import com.ap.project.webrtcmobile.WebRtcMobileApp

class CallAudioInteractor {

    private val audioManager: AudioManager by lazy {
        WebRtcMobileApp.getInstance().getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    fun onCallStarted() {
        with(audioManager) {
            mode = AudioManager.MODE_IN_COMMUNICATION
            isSpeakerphoneOn = true
        }
    }


    fun onCallEnded() {
        with(audioManager) {
            mode = AudioManager.MODE_NORMAL
            isSpeakerphoneOn = false
        }
    }

}