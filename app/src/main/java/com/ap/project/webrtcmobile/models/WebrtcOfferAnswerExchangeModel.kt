package com.ap.project.webrtcmobile.models

import com.google.gson.annotations.SerializedName

data class WebrtcOfferAnswerExchangeModel
(
        @SerializedName("fromUserId")
        val fromUserId: String,

        @SerializedName("targetUserId")
        val targetUserId: String,

        @SerializedName("sdp")
        val sdpModel: SdpModel
)