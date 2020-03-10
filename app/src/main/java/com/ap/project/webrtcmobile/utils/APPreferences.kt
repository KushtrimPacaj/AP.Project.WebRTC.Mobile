package com.ap.project.webrtcmobile.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.ap.project.webrtcmobile.WebRtcMobileApp




@SuppressLint("CommitPrefEdits")
object APPreferences {

    private val sharedPreferences: SharedPreferences
        get() = WebRtcMobileApp.getInstance().getSharedPreferences("WebRtcMobileApp", Context.MODE_PRIVATE)


}
