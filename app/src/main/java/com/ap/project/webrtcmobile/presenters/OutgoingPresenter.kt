package com.ap.project.webrtcmobile.presenters

import com.ap.project.webrtcmobile.events.DestroyOutgoingView
import com.ap.project.webrtcmobile.events.OnAddVideoTrackEvent
import com.ap.project.webrtcmobile.events.OnLocalStreamChangeEvent
import com.ap.project.webrtcmobile.events.OutgoingViewCreatedEvent
import com.ap.project.webrtcmobile.view_interfaces.OutgoingView
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.webrtc.VideoTrack

class OutgoingPresenter : MvpBasePresenter<OutgoingView>() {


    fun init() {
        view?.initViews()

        EventBus.getDefault().register(this)
        EventBus.getDefault().post(OutgoingViewCreatedEvent())
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: OnLocalStreamChangeEvent) {
        view?.bindLocalVideoTrack(event.videoTrack)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: OnAddVideoTrackEvent) {
        view?.bindRemoteVideoTrack(event.rtpReceiver.track() as VideoTrack)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: DestroyOutgoingView?) {
        view?.destroyViews()
        EventBus.getDefault().unregister(this)
        view?.finish()
    }


    fun destroy() {
        EventBus.getDefault().unregister(this)
    }
}