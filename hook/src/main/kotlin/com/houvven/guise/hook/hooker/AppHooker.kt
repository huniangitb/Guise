package com.houvven.guise.hook.hooker

import android.content.pm.PackageInfo
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.LocaleList
import com.highcapable.betterandroid.system.extension.tool.SystemVersion
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ApplicationPackageManagerClass
import com.highcapable.yukihookapi.hook.type.android.ConfigurationClass
import com.highcapable.yukihookapi.hook.type.android.ResourcesClass
import com.houvven.guise.hook.hooker.base.GuiseBaseHooker
import com.houvven.guise.hook.profile.item.AppProfile
import java.util.Locale

internal class AppHooker(profile: AppProfile) :
    GuiseBaseHooker<AppProfile>(profile) {

    override fun doHook() {
        hookPackageInfo()
        hookBuildConfig()
        hookConfiguration()
    }

    private fun hookPackageInfo() {
        ApplicationPackageManagerClass
            .method { name = "getPackageInfo" }
            .hookAll()
            .after {
                val info = result as PackageInfo?
                if (info != null && info.packageName == packageName) {
                    profile.packageInfo.versionName?.let { info.versionName = it }
                    profile.packageInfo.versionCode?.let {
                        info.versionCode = it
                        SystemVersion.require(SystemVersion.P) {
                            info.longVersionCode = it.toLong()
                        }
                    }
                }
            }
    }

    private fun hookBuildConfig() {
        "$packageName.BuildConfig".toClassOrNull()?.run {
            profile.packageInfo.versionName?.let {
                field {
                    name = "VERSION_NAME"
                }.ignored().give()?.set(null, it)
            }
            profile.packageInfo.versionCode?.let {
                field {
                    name = "VERSION_CODE"
                }.ignored().give()?.set(null, it)
            }
        }
    }

    private fun hookConfiguration() {
        val locale = profile.language?.split("_")?.run {
            Locale(getOrElse(0) { "" }, getOrElse(1) { "" }, getOrElse(2) { "" })
        }
        val uiMode = profile.nightMode?.let { if (it) UI_MODE_NIGHT_YES else UI_MODE_NIGHT_NO }
        val dpi = profile.densityDpi
        val fontScale = profile.fontScale

        ResourcesClass.method {
            name = "getConfiguration"
        }.hookAll().after {
            ConfigurationClass.run {
                if (locale != null) {
                    field { name = "mLocaleList" }.ignored().give()?.set(result, LocaleList(locale))
                    field { name = "locale" }.ignored().give()?.set(result, locale)
                }
                if (uiMode != null) {
                    field { name = "uiMode" }.ignored().give()?.set(result, uiMode)
                }
                if (dpi != null) {
                    field { name = "densityDpi" }.ignored().give()?.set(result, dpi)
                }
                if (fontScale != null) {
                    field { name = "fontScale" }.ignored().give()?.set(result, fontScale)
                }
            }
        }
    }

}