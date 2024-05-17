package com.houvven.guise.data.repository

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.highcapable.betterandroid.system.extension.tool.SystemProperties
import com.houvven.guise.hook.profile.ModuleHookProfiles
import com.houvven.guise.hook.profile.item.AppProfile
import com.houvven.guise.hook.profile.item.PropertiesProfile
import com.houvven.guise.util.app.App
import java.util.Locale

@SuppressLint("ConstantLocale")
object ProfilesPlaceholderRepo {

    private val configuration = Resources.getSystem().configuration

    private var profiles by mutableStateOf(
        ModuleHookProfiles(
            properties = PropertiesProfile(
                brand = Build.BRAND,
                manufacturer = Build.MANUFACTURER,
                model = Build.MODEL,
                product = Build.PRODUCT,
                device = Build.DEVICE,
                displayId = Build.DISPLAY,
                fingerprint = Build.FINGERPRINT,
                characteristics = SystemProperties.get("ro.build.characteristics"),
            ),
            app = AppProfile(
                language = Locale.getDefault().toString(),
                densityDpi = configuration.densityDpi,
                fontScale = configuration.fontScale,
            )
        )
    )

    fun <T> get(function: (ModuleHookProfiles) -> T): T {
        return function(profiles)
    }

    fun update(app: App) {
        profiles = profiles.app.packageInfo.copy(
            versionCode = app.versionCode.toInt(),
            versionName = app.versionName
        ).let {
            profiles.copy(app = profiles.app.copy(packageInfo = it))
        }
    }
}