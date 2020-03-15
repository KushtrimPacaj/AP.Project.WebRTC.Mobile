package com.ap.project.webrtcmobile.activities

import android.os.Bundle
import com.ap.project.webrtcmobile.custom_views.DrawableObjects.CPath
import com.ap.project.webrtcmobile.databinding.ActivityOutgoingBinding
import com.ap.project.webrtcmobile.events.EndCallEvent
import com.ap.project.webrtcmobile.interactors.initWithDefaultEglContext
import com.ap.project.webrtcmobile.interactors.removeAllTracksFromRenderer
import com.ap.project.webrtcmobile.interactors.replaceTrackInRenderer
import com.ap.project.webrtcmobile.interactors.swapFeedsWith
import com.ap.project.webrtcmobile.presenters.OutgoingPresenter
import com.ap.project.webrtcmobile.view_interfaces.OutgoingView
import com.hannesdorfmann.mosby.mvp.MvpActivity
import org.greenrobot.eventbus.EventBus
import org.webrtc.VideoTrack

class OutgoingActivity : MvpActivity<OutgoingView, OutgoingPresenter>(), OutgoingView {

    private lateinit var binding: ActivityOutgoingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOutgoingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        presenter.init()
    }


    override fun onDestroy() {
        super.onDestroy()
        presenter.destroy()
    }

    override fun createPresenter() = OutgoingPresenter()

    override fun initViews() {
        with(binding.localRenderer) {
            setZOrderMediaOverlay(true)
            setScalingType(org.webrtc.RendererCommon.ScalingType.SCALE_ASPECT_BALANCED)
            setMirror(false)
            requestLayout()
            initWithDefaultEglContext()
        }

        with(binding.remoteRenderer) {
            setScalingType(org.webrtc.RendererCommon.ScalingType.SCALE_ASPECT_BALANCED)
            setMirror(false)
            requestLayout()
            initWithDefaultEglContext()
        }

        binding.endCall.setOnClickListener {
            EventBus.getDefault().post(EndCallEvent())
        }

        binding.localRenderer.setOnClickListener {
            binding.localRenderer.swapFeedsWith(binding.remoteRenderer)
        }
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().post(EndCallEvent())
    }

    override fun destroyViews() {
        with(binding.localRenderer) {
            removeAllTracksFromRenderer()
            release()
        }
        with(binding.remoteRenderer) {
            removeAllTracksFromRenderer()
            release()
        }
    }

    override fun bindLocalVideoTrack(videoTrack: VideoTrack) {
        binding.localRenderer.replaceTrackInRenderer(videoTrack)
    }

    override fun bindRemoteVideoTrack(videoTrack: VideoTrack) {
        binding.remoteRenderer.replaceTrackInRenderer(videoTrack)
    }

    override fun drawPath(path: CPath?) {
        binding.faricView.addCPath(path)
    }

    override fun cleanupOldDrawings() {
        binding.faricView.cleanupOldDrawings()
    }
}
