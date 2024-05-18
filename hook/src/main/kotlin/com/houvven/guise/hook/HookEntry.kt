package com.houvven.guise.hook

import com.elvishew.xlog.BuildConfig
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import com.houvven.guise.hook.hooker.PackageHooker
import com.houvven.guise.hook.hooker.PropertiesHooker
import com.houvven.guise.hook.hooker.ResourceConfigurationHooker
import com.houvven.guise.hook.hooker.location.CellLocationHooker
import com.houvven.guise.hook.hooker.location.LocationHooker
import com.houvven.guise.hook.profile.HookProfiles
import com.houvven.guise.hook.store.impl.SharedPreferenceModuleStore

@InjectYukiHookWithXposed(
    modulePackageName = "com.houvven.guise",
    isUsingXposedModuleStatus = true
)
object HookEntry : IYukiHookXposedInit {

    override fun onInit() = configs {
        isDebug = BuildConfig.DEBUG
        debugLog {
            tag = "GuiseHook"
        }
    }

    override fun onHook() = encase {
        // val profiles = HookProfiles.Debug
        val store = SharedPreferenceModuleStore.Hooked(packageParam = this)
        val profiles = store.get(mainProcessName)
        if (profiles.isAvailable) {
            loadApp(isExcludeSelf = true) {
                doLoadHooker(profiles)
            }
        }
    }

    private fun PackageParam.doLoadHooker(profile: HookProfiles) = profile.run {
        listOf(
            ::PackageHooker,
            ::ResourceConfigurationHooker,
            ::LocationHooker,
            ::CellLocationHooker
        ).forEach { loadHooker(it.invoke(this)) }
        loadHooker(PropertiesHooker(profile.properties))
    }

}