package com.ap.project.webrtcmobile.utils

import org.webrtc.PeerConnection.IceServer
import java.util.*

object IceServersUtils {
    @JvmStatic
    fun getList(): List<IceServer>? {
        val iceServers: MutableList<IceServer> = ArrayList()
        iceServers.add(IceServer("stun:stun.l.google.com:19302"))
        iceServers.add(IceServer("stun:stun1.l.google.com:19302"))
        iceServers.add(IceServer("stun:stun2.l.google.com:19302"))
        iceServers.add(IceServer("stun:stun3.l.google.com:19302"))
        iceServers.add(IceServer("stun:stun4.l.google.com:19302"))
        return iceServers
    }

}
