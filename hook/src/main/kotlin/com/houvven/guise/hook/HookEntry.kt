package com.houvven.guise.hook

import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit

@InjectYukiHookWithXposed(modulePackageName = "com.houvven.guise", isUsingXposedModuleStatus = true)
object HookEntry : IYukiHookXposedInit {

    override fun onInit() = configs {

    }

    override fun onHook() = encase {

    }

    private fun onDebugHook() = encase {

    }
}