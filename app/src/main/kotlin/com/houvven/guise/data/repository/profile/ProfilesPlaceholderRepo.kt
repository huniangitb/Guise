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

/**
 * This object represents a repository for storing placeholder profiles.
 * It uses system properties and configuration to generate a HookProfiles object.
 */
@SuppressLint("ConstantLocale")
object ProfilesPlaceholderRepo {

    // System configuration used to get density DPI and font scale
    private val configuration = Resources.getSystem().configuration

    // Mutable state of HookProfiles, initialized with system properties and configuration
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

    /**
     * This function takes a function as a parameter and applies it to the profiles.
     * @param function A function that takes a HookProfiles object and returns a value of type T.
     * @return The result of applying the function to the profiles.
     */
    fun <T> get(function: (HookProfiles) -> T): T {
        return function(profiles)
    }

    /**
     * This function updates the profiles with the version code and version name from the app.
     * @param app An App object from which the version code and version name are taken.
     */
    fun update(app: App) {
        profiles = profiles.copy(
            versionCode = app.versionCode.toInt(),
            versionName = app.versionName
        )
    }
}