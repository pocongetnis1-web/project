package com.genetic.darkphantom.utils

import android.content.Context
import android.content.pm.PackageManager
import com.genetic.darkphantom.activities.LoginActivity

object IconUtils {
    fun hideIcon(context: Context) {
        val pm = context.packageManager
        val component = android.content.ComponentName(context, LoginActivity::class.java)
        pm.setComponentEnabledSetting(
            component,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }
}
