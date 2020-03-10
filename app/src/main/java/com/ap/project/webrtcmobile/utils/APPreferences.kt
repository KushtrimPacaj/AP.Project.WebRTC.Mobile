package com.ap.project.webrtcmobile.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.ap.project.webrtcmobile.WebRtcMobileApp


@SuppressLint("CommitPrefEdits")
object APPreferences {

    private val sharedPreferences: SharedPreferences
        get() = WebRtcMobileApp.getInstance().getSharedPreferences("WebRtcMobileApp", Context.MODE_PRIVATE)

    @JvmStatic
    public var userName: String?
        get() = sharedPreferences.getString("username", null)
        set(value) {
            sharedPreferences.edit { putString("username", value) }
        }


    @JvmStatic
    public var userId: String?
        get() = sharedPreferences.getString("userId", null)
        set(value) {
            sharedPreferences.edit { putString("userId", value) }
        }

}
