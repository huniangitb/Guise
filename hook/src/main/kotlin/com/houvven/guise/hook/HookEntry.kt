package com.houvven.guise.hook

import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import com.houvven.guise.hook.hooker.PropertiesHooker
import com.houvven.guise.hook.profile.ModuleHookProfiles

@InjectYukiHookWithXposed(isUsingXposedModuleStatus = true)
object HookEntry : IYukiHookXposedInit {

    override fun onInit() = configs {
    }

    override fun onHook() = encase {
        val profiles = ModuleHookProfiles.Debug

        loadApp {
            loadHooker(profiles)
        }
    }

    private fun PackageParam.loadHooker(profile: ModuleHookProfiles) {
        with(profile) {
            loadHooker(PropertiesHooker(properties))
        }
    }

}