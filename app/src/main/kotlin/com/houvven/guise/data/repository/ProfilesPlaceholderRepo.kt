package com.houvven.guise.data.repository

import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.highcapable.betterandroid.system.extension.tool.SystemProperties
import com.houvven.guise.hook.profile.ModuleHookProfiles
import com.houvven.guise.hook.profile.item.AppInfoProfile
import com.houvven.guise.hook.profile.item.PropertiesProfile
import com.houvven.guise.util.app.App

object ProfilesPlaceholderRepo {

    private var profiles by mutableStateOf(
        ModuleHookProfiles(
            properties = PropertiesProfile(
                brand = Build.BRAND,
                manufacturer = Build.MANUFACTURER,
                model = Build.MODEL,
                device = Build.DEVICE,
                fingerprint = Build.FINGERPRINT,
                characteristics = SystemProperties.get("ro.build.characteristics"),
            )
        )
    )

    fun <T> get(function: (ModuleHookProfiles) -> T): T {
        return function(profiles)
    }

    fun update(app: App) {
        profiles = profiles.copy(
            appInfo = AppInfoProfile(
                versionName = app.versionName,
                versionCode = app.versionCode.toInt()
            )
        )
    }
}