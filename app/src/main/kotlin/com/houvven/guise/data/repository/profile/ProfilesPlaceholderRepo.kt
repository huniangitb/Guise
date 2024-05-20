package com.houvven.guise.data.repository.profile

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.highcapable.betterandroid.system.extension.tool.SystemProperties
import com.houvven.guise.hook.profile.HookProfiles
import com.houvven.guise.hook.profile.item.PropertiesProfile
import com.houvven.guise.util.app.App
import java.util.Locale

@SuppressLint("ConstantLocale")
object ProfilesPlaceholderRepo {

    private val configuration = Resources.getSystem().configuration

    private var profiles by mutableStateOf(
        HookProfiles(
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
            language = Locale.getDefault().toString(),
            densityDpi = configuration.densityDpi,
            fontScale = configuration.fontScale
        )
    )

    fun <T> get(function: (HookProfiles) -> T): T {
        return function(profiles)
    }

    fun update(app: App) {
        profiles = profiles.copy(
            versionCode = app.versionCode.toInt(),
            versionName = app.versionName
        )
    }
}