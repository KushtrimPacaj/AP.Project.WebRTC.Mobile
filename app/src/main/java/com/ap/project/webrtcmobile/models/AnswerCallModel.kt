package com.ap.project.webrtcmobile.models

import com.google.gson.annotations.SerializedName

data class AnswerCallModel(
        @SerializedName("fromUserId")
        var fromUserId: String,
        @SerializedName("targetUserId")
        var targetUserId: String
)