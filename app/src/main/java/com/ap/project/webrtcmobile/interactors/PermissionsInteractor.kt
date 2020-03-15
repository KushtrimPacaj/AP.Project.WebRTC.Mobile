package com.ap.project.webrtcmobile.interactors

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.ap.project.webrtcmobile.R
import com.ap.project.webrtcmobile.utils.CustomDialogOnAnyDeniedMultiplePermissionsListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class PermissionsInteractor(private val activity: Activity, val grantedCallback: GrantedCallback) {


    public fun requestPermissionsForApp() {
        val dialogMultiplePermissionsListener = CustomDialogOnAnyDeniedMultiplePermissionsListener.Builder
                .withContext(activity)
                .withTitle("Camera & audio permission")
                .withMessage("Both camera and audio permission are needed to make calls")
                .withButtonText("Go to Settings")
                .withIcon(R.mipmap.ic_launcher)
                .build()
        val compositePermissionsListener: MultiplePermissionsListener = CompositeMultiplePermissionsListener(dialogMultiplePermissionsListener, object : BaseMultiplePermissionsListener() {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report != null && report.areAllPermissionsGranted()) {
                    grantedCallback.onPermissionsGranted()
                }
            }
        })
        Dexter.withActivity(activity)
                .withPermissions(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
                .withListener(compositePermissionsListener)
                .withErrorListener { error: DexterError -> Log.d(activity.localClassName, "Error: $error") }
                .check()
    }

    fun areAllPermissionsGranted(): Boolean {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    interface GrantedCallback {
        fun onPermissionsGranted()
    }

}