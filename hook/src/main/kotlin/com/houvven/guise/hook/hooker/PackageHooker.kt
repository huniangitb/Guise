package com.houvven.guise.hook.hooker

import android.content.pm.PackageInfo
import com.highcapable.betterandroid.system.extension.tool.SystemVersion
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ApplicationPackageManagerClass
import com.houvven.guise.hook.hooker.base.BaseHooker
import com.houvven.guise.hook.profile.HookProfiles

internal class PackageHooker(profile: HookProfiles) : BaseHooker.Default(profile) {

    override fun doHook() {
        hookPackageInfoGetter()
        hookBuildConfig()
    }

    private fun hookPackageInfoGetter() {
        ApplicationPackageManagerClass.method { name = "getPackageInfo" }.hookAll().after {
            val info = result as PackageInfo?
            if (info != null && info.packageName == packageName) {
                profile.versionName?.let { info.versionName = it }
                profile.versionCode?.let {
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
            profile.versionName?.let {
                field {
                    name = "VERSION_NAME"
                }.ignored().give()?.set(null, it)
            }
            profile.versionCode?.let {
                field {
                    name = "VERSION_CODE"
                }.ignored().give()?.set(null, it)
            }
        }
    }
}