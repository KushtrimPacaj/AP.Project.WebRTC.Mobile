package com.ap.project.webrtcmobile.models

import com.google.gson.annotations.SerializedName

data class EndCallModel(

        @SerializedName("targetUserId")
        var targetUserId: String
)