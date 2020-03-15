package com.ap.project.webrtcmobile.interactors

import android.util.Log
import androidx.collection.SimpleArrayMap
import com.ap.project.webrtcmobile.utils.IceServersUtils
import com.ap.project.webrtcmobile.utils.LogTag
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory

/**
 * Used to manage PeerConnections
 */
class PeerConnectionInteractor(private var factory: PeerConnectionFactory?) {


    private val peerConnectionMap = SimpleArrayMap<String, PeerConnectionData>()


    /**
     * Returns a peer connection for this user ( if a peer connection already exists, it returns that, otherwise it creates a new one
     */
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


    /**
     * Uses WebRTC API ( PeerConnectionFactory) in order to create a new PeerConnection.
     * Sets an observer (PeerConnectionObserver) in order to get callback for it.
     */
    private fun createAPeerConnection(userId: String): PeerConnection {
        val rtcConfig = PeerConnection.RTCConfiguration(IceServersUtils.getList())
        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
        return factory!!.createPeerConnection(
                rtcConfig,
                PeerConnectionObserver(userId)
        )!!
    }


    /**
     * Disposes all peer connections
     */
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

    /**
     * Disposes only the peer connection for user with id passed as argument ( if it exists )
     */
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


    /**
     * Returns a peer connection for user if it exists, or null if it doesn't
     */
    fun getPeerConnectionForUser(userId: String): PeerConnectionData? {
        return peerConnectionMap.get(userId)
    }


    /**
     * A wrapper model for PeerConnection, containing the boolean canRenegotiate.
     * This is for the case when PeerConnectionObserver triggers onRenegotiationNeeded..
     * We first check if canRenegotiate state is set to true to carry that out.
     *
     * If it's set to false, it means we're already mid offer-answer exchange, so we shouldn't start reconnect again
     */
    data class PeerConnectionData(val peerConnection: PeerConnection, var canRenegotiate: Boolean)
}
