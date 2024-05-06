package com.houvven.guise.hook

import com.highcapable.yukihookapi.YukiHookAPI

object ModuleStatus {
    val isModuleActive = YukiHookAPI.Status.isXposedModuleActive
}