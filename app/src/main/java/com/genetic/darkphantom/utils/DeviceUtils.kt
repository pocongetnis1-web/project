package com.genetic.darkphantom.utils

import android.content.Context
import android.os.Build
import android.provider.Settings

object DeviceUtils {
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun getDeviceInfo(context: Context): Map<String, Any> {
        return mapOf(
            "model" to Build.MODEL,
            "brand" to Build.BRAND,
            "sdk" to Build.VERSION.SDK_INT,
            "android_version" to Build.VERSION.RELEASE,
            "device_id" to getDeviceId(context)
        )
    }
}
