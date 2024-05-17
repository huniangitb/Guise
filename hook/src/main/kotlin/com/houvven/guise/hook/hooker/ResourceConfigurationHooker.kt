package com.houvven.guise.hook.hooker

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.LocaleList
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ResourcesClass
import com.houvven.guise.hook.hooker.base.BaseHooker
import com.houvven.guise.hook.profile.HookProfiles
import java.util.Locale

internal class ResourceConfigurationHooker(profiles: HookProfiles) : BaseHooker.Default(profiles) {

    override fun doHook() {
        this.hookResourceGetter()
    }

    private fun hookResourceGetter() {
        val locale = profile.language?.let { stringToLocale(it) }
        val uiMode = profile.nightMode?.let { isNightModeToUiMode(it) }
        val dpi = profile.densityDpi
        val fontScale = profile.fontScale

        ResourcesClass.method {
            name = "getConfiguration"
        }.hookAll().after {
            result?.current(ignored = true) {
                locale?.let {
                    field { name = "mLocaleList" }.set(LocaleList(it))
                    field { name = "locale" }.set(it)
                }
                uiMode?.let { field { name = "uiMode" }.set(it) }
                dpi?.let { field { name = "densityDpi" }.set(dpi) }
                fontScale?.let { field { name = "fontScale" }.set(fontScale) }
            }
        }
    }

    private fun stringToLocale(string: String): Locale {
        val parts = string.split("_")
        return when (parts.size) {
            1 -> Locale(parts[0])
            2 -> Locale(parts[0], parts[1])
            3 -> Locale(parts[0], parts[1], parts[2])
            else -> throw IllegalArgumentException("Invalid locale string: $string")
        }
    }

    private fun isNightModeToUiMode(isNightMode: Boolean): Int {
        return if (isNightMode) UI_MODE_NIGHT_YES else UI_MODE_NIGHT_NO
    }
}