package com.ap.project.webrtcmobile.models

import com.google.gson.annotations.SerializedName
import org.webrtc.IceCandidate


data class IceCandidateModel(

        @SerializedName("ice")
        var candidate: IceCandidate,

        @SerializedName("fromUserId")
        var fromUserId: String,

        @SerializedName("targetUserId")
        var targetUserId: String
)