package com.ap.project.webrtcmobile.interactors

import android.util.Log
import androidx.collection.SimpleArrayMap
import com.ap.project.webrtcmobile.utils.IceServersUtils
import com.ap.project.webrtcmobile.utils.LogTag
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory


class PeerConnectionInteractor(private var factory: PeerConnectionFactory?, private val localMediaStreamInteractor: LocalMediaStreamInteractor) {


    private val peerConnectionMap = SimpleArrayMap<String, PeerConnectionData>()


    fun getOrCreatePeerConnectionForUser(userId: String): PeerConnectionData {
        Log.d(LogTag.CALLS, "Requested a peer connection for participant with id:  $userId")

        var peerConnectionData = peerConnectionMap.get(userId)
        if (peerConnectionData == null) {
            Log.d(LogTag.CALLS, "Peer connection for user $userId doesn't exist. Creating one...")
            val peerConnection = createAPeerConnection(userId)
            peerConnectionData = PeerConnectionData(peerConnection, false)
            peerConnectionMap.put(userId, peerConnectionData)
        }
        return peerConnectionData
    }


    private fun createAPeerConnection(userId: String): PeerConnection {
        val rtcConfig = PeerConnection.RTCConfiguration(IceServersUtils.getList())
        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
        return factory!!.createPeerConnection(
                rtcConfig,
                PeerConnectionObserver(userId)
        )!!
    }


    fun disposeAllPeerConnections() {

        val size = peerConnectionMap.size()
        Log.d(LogTag.CALLS, "Disposing peer connections. Current peer connection number is $size")

        for (i in 0 until size) {
            Log.d(LogTag.CALLS, "Disposing peer connections for user " + peerConnectionMap.keyAt(0))
            val peerConnectionData = peerConnectionMap.valueAt(0)
            peerConnectionMap.removeAt(0)
            peerConnectionData.peerConnection.dispose()

        }
    }

    fun disposePeerConnectionsForUser(userId: String) {
        val peerConnectionData = peerConnectionMap.get(userId)
        if (peerConnectionData != null) {
            peerConnectionData.peerConnection.dispose()
            peerConnectionMap.remove(userId)
        }
    }

    fun setFactory(factory: PeerConnectionFactory) {
        this.factory = factory
    }


    fun getPeerConnectionForUser(userId: String): PeerConnectionData? {
        return peerConnectionMap.get(userId)
    }


    data class PeerConnectionData(val peerConnection: PeerConnection, var canRenegotiate: Boolean)
}
