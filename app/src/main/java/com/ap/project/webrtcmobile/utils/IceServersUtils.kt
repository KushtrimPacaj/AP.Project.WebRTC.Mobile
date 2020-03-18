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


//
//  ME 10.1.3.4            10.1.3.4:6700  (NAT)  80.90.17.3:3800                                   STUN
//


//
//  ME 10.1.3.4            10.1.3.4:6700  (SYMMETRIC NAT)  80.90.17.3:3800                         STUN1
//  ME 10.1.3.4            10.1.3.4:6700  (SYMMETRIC NAT)  80.90.17.3:5600                         STUN2 or PEER


//                                          TURN
//
//  ME 10.1.3.4                                                                               OTHER  10.1.3.6
//

}
