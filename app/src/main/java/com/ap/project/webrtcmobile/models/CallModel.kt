package com.ap.project.webrtcmobile.models

import com.google.gson.annotations.SerializedName

data class CallModel(

        @SerializedName("fromUserId")
        var fromUserId: String,

        @SerializedName("callerName")
        var callerName: String,

        @SerializedName("targetUserId")
        var targetUserId: String
)