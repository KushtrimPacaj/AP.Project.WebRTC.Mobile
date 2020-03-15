package com.ap.project.webrtcmobile.interactors

import android.view.View
import com.ap.project.webrtcmobile.R
import com.ap.project.webrtcmobile.WebRtcMobileApp
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoTrack


fun SurfaceViewRenderer.initWithDefaultEglContext() {
    val eglBaseContext = WebRtcMobileApp.getInstance().eglBase.eglBaseContext
    init(eglBaseContext, null)

}


@Synchronized
fun SurfaceViewRenderer.removeAllTracksFromRenderer() {
    val videoTrack = this.getTag(R.id.videoTrack) as VideoTrack?
    videoTrack?.removeSink(this)
    setTag(R.id.videoTrack, null)
    visibility = View.GONE
}


@Synchronized
fun SurfaceViewRenderer.replaceTrackInRenderer(newVideoTrack: VideoTrack) {

    val oldVideoTrack = this.getTag(R.id.videoTrack) as VideoTrack?

    if (oldVideoTrack != newVideoTrack) {
        oldVideoTrack?.removeSink(this)
        newVideoTrack.addSink(this)
        setTag(R.id.videoTrack, newVideoTrack)
    }
    visibility = View.VISIBLE

}

@Synchronized
fun SurfaceViewRenderer.swapFeedsWith(otherRenderer: SurfaceViewRenderer) {
    val myVideoTrack = this.getTag(R.id.videoTrack) as VideoTrack?
    val otherVideoTrack = otherRenderer.getTag(R.id.videoTrack) as VideoTrack?


    otherVideoTrack?.let { replaceTrackInRenderer(it) }
    myVideoTrack?.let { otherRenderer.replaceTrackInRenderer(it) }

}