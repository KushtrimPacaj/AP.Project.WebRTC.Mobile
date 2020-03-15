package com.ap.project.webrtcmobile.interactors

object SocketIOMethods {

    const val SAVE_CLIENT_INFO = "saveUserInfo"


    const val CALL_USER = "callUser"
    const val ON_INCOMING_CALL = "onIncomingCall"

    const val ANSWER_CALL = "answerCall"
    const val ON_CALL_ANSWERED = "onCallAnswered"

    const val DECLINE_CALL = "declineCall"
    const val ON_CALL_DECLINED = "onCallDeclined"

    const val END_CALL = "endCall"
    const val ON_CALL_ENDED = "onCallEnded"

    const val SEND_OFFER = "sendOffer"
    const val ON_RECEIVED_OFFER = "onReceivedOffer"

    const val SEND_ANSWER = "sendAnswer"
    const val ON_RECEIVED_ANSWER = "onReceivedAnswer"

    const val SEND_ICE_CANDIDATE = "sendIceCandidate"
    const val ON_ICE_CANDIDATE_RECEIVED = "onReceivedIceCandidate"


    const val ON_USER_ONLINE = "onUserOnline"
    const val ON_USER_DISCONNECTED = "onUserDisconnected"



    const val SEND_FABRIC_PATH = "sendFabricPath"
    const val ON_RECEIVED_FABRIC_PATH = "onReceivedFabricPath"

}