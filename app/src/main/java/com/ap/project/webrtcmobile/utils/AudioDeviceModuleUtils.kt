package com.ap.project.webrtcmobile.utils


import com.ap.project.webrtcmobile.WebRtcMobileApp
import org.webrtc.audio.AudioDeviceModule
import org.webrtc.audio.JavaAudioDeviceModule
import org.webrtc.voiceengine.WebRtcAudioManager
import org.webrtc.voiceengine.WebRtcAudioUtils

object AudioDeviceModuleUtils {

    @JvmStatic
    fun createLegacyAudioDevice(): AudioDeviceModule {

        // Enable/disable OpenSL ES playback.
        WebRtcAudioManager.setBlacklistDeviceForOpenSLESUsage(true /* enable */)
        WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(true)
        WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(true)
        WebRtcAudioUtils.setDefaultSampleRateHz(16000/*16 KHz*/)

        val builder = JavaAudioDeviceModule.builder(WebRtcMobileApp.getInstance().applicationContext)
        return builder.createAudioDeviceModule()
    }
}
