package com.ap.project.webrtcmobile.presenters

import com.ap.project.webrtcmobile.events.*
import com.ap.project.webrtcmobile.view_interfaces.OutgoingView
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.webrtc.VideoTrack
import rx.Observable
import rx.Subscription
import java.util.concurrent.TimeUnit

class OutgoingPresenter : MvpBasePresenter<OutgoingView>() {


    private var subscription: Subscription? = null

    fun init() {
        view?.initViews()

        EventBus.getDefault().register(this)
        EventBus.getDefault().post(OutgoingViewCreatedEvent())
        subscription = Observable.interval(1, TimeUnit.SECONDS).subscribe { view?.cleanupOldDrawings() }
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

    @Subscribe
    fun onEvent(event: PathReceivedEvent) {
        val serializablePath = event.serializablePath
        val path = serializablePath.toCPath()
        view?.drawPath(path)
    }


    fun destroy() {
        EventBus.getDefault().unregister(this)
        subscription?.unsubscribe()
    }
}