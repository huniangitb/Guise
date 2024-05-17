package com.houvven.guise.hook

import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import com.houvven.guise.hook.hooker.AppHooker
import com.houvven.guise.hook.hooker.PropertiesHooker
import com.houvven.guise.hook.profile.ModuleHookProfiles
import com.houvven.guise.hook.store.impl.SharedPreferenceModuleStore

@InjectYukiHookWithXposed(
    modulePackageName = "com.houvven.guise",
    isUsingXposedModuleStatus = true
)
object HookEntry : IYukiHookXposedInit {

    override fun onInit() = configs {
        isDebug = false
    }

    override fun onHook() = encase {
        // val profiles = ModuleHookProfiles.Debug
        val store = SharedPreferenceModuleStore.Hooked(packageParam = this)
        val profiles = store.get(mainProcessName)

        loadApp(isExcludeSelf = true) {
            doLoadHooker(profiles)
        }
    }

    private fun PackageParam.doLoadHooker(profile: ModuleHookProfiles) {
        with(profile) {
            loadHooker(PropertiesHooker(properties))
            loadHooker(AppHooker(app))
        }
    }

}