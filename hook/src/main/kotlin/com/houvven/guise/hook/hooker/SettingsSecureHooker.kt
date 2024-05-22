package com.houvven.guise.hook.hooker

import android.provider.Settings.Secure
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContentResolverClass
import com.highcapable.yukihookapi.hook.type.android.Settings_SecureClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.houvven.guise.hook.hooker.base.BaseHooker
import com.houvven.guise.hook.profile.HookProfiles

internal class SettingsSecureHooker(profiles: HookProfiles) : BaseHooker.Default(profiles) {

    override fun doHook() {
        Settings_SecureClass.method {
            name = "getStringForUser"
            param(ContentResolverClass, StringClass, IntType)
        }.hook().after {
            val name = args[1] as String

            @Suppress("DEPRECATION")
            val r =
                if (name == Secure.LOCATION_MODE && profile.isLocationAvailable) Secure.LOCATION_MODE_HIGH_ACCURACY
                else if (name == Secure.ANDROID_ID && !profile.ssaid.isNullOrBlank()) profile.ssaid
                else result

            result = r.toString()
        }
    }
}