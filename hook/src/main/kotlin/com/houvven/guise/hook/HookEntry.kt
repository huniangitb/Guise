package com.houvven.guise.hook

import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import com.houvven.guise.hook.hooker.PackageHooker
import com.houvven.guise.hook.hooker.PropertiesHooker
import com.houvven.guise.hook.hooker.ResourceConfigurationHooker
import com.houvven.guise.hook.hooker.location.CellHooker
import com.houvven.guise.hook.hooker.location.LocationHooker
import com.houvven.guise.hook.store.impl.SharedPreferenceModuleStore

@InjectYukiHookWithXposed(
    modulePackageName = "com.houvven.guise",
    isUsingXposedModuleStatus = true
)
object HookEntry : IYukiHookXposedInit {

    override fun onInit() = configs {
        isDebug = false
        debugLog {
            tag = "GuiseHook"
        }
    }

    override fun onHook() = encase {
        loadAppHooker()
        loadSysHooker()
    }

    private fun PackageParam.loadAppHooker() {
        val store = SharedPreferenceModuleStore.Hooked(packageParam = this)
        val profiles = store.get(mainProcessName)
        if (!profiles.isAvailable) {
            YLog.info("No profiles for $packageName")
        }
        loadApp(isExcludeSelf = true) {
            profiles.run {
                listOf(
                    ::PackageHooker,
                    ::ResourceConfigurationHooker,
                    ::LocationHooker,
                    ::CellHooker
                ).forEach {
                    loadHooker(it.invoke(this))
                }
                loadHooker(PropertiesHooker(properties))
            }
        }
    }

    private fun PackageParam.loadSysHooker() {
    }
}