package com.ap.project.webrtcmobile.view_interfaces

import com.hannesdorfmann.mosby.mvp.MvpView
import org.webrtc.VideoTrack

interface OutgoingView : MvpView {

    fun initViews()

    fun destroyViews()

    fun bindLocalVideoTrack(videoTrack: VideoTrack)

    fun bindRemoteVideoTrack(videoTrack: VideoTrack)

    fun finish()

}

