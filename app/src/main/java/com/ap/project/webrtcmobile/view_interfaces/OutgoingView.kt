package com.ap.project.webrtcmobile.view_interfaces

import com.ap.project.webrtcmobile.custom_views.DrawableObjects.CPath
import com.hannesdorfmann.mosby.mvp.MvpView
import org.webrtc.VideoTrack

interface OutgoingView : MvpView {

    fun initViews()

    fun drawPath(path: CPath?)

    fun cleanupOldDrawings()

    fun destroyViews()

    fun bindLocalVideoTrack(videoTrack: VideoTrack)

    fun bindRemoteVideoTrack(videoTrack: VideoTrack)

    fun finish()

}

