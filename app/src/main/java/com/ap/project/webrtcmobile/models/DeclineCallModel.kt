package com.ap.project.webrtcmobile.models

import com.google.gson.annotations.SerializedName

data class DeclineCallModel(
        @SerializedName("fromUserId")
        var fromUserId: String,
        @SerializedName("targetUserId")
        var targetUserId: String
)