package com.ap.project.webrtcmobile.models

import com.ap.project.webrtcmobile.custom_views.DrawableObjects.SerializablePath
import com.google.gson.annotations.SerializedName

class FabricPathModel(
        @SerializedName("targetUserId")
        val targetUserId: String,

        @SerializedName("serializablePath")
        val serializablePath: SerializablePath
)