package com.houvven.guise.hook

import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import com.houvven.guise.hook.hooker.PropertiesHooker
import com.houvven.guise.hook.profile.ModuleHookProfiles

@InjectYukiHookWithXposed(modulePackageName = "com.houvven.guise", isUsingXposedModuleStatus = true)
object HookEntry : IYukiHookXposedInit {

    override fun onInit() = configs {
    }

    override fun onHook() = encase {
        loadApp {
            loadHooker(ModuleHookProfiles.Debug)
        }
    }

    private fun PackageParam.loadHooker(profile: ModuleHookProfiles) {
        loadHooker(PropertiesHooker(profile.properties))
    }

}