package com.app.bollyhood.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionUtils {

    companion object{
    fun isRecording(context: Context):Boolean{
        return ContextCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED
    }

        fun isCamera(context: Context):Boolean{
            return ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED
        }

    fun requestRecordingPermission(activity: Activity) {
        ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.RECORD_AUDIO), 100)
    }

        fun requestCameraPermission(activity: Activity) {
            ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.CAMERA), 100)
        }
    }
}