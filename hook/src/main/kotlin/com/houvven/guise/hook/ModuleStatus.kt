package com.houvven.guise.hook

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.core.api.compat.type.ExecutorType

object ModuleStatus {
    val isModuleActive = YukiHookAPI.Status.isModuleActive
    val executorType = YukiHookAPI.Status.Executor.type

    val isLSPosedExecutor = executorType == ExecutorType.LSPOSED_LSPATCH
}