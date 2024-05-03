package com.houvven.guise.hook.hooker.base

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.houvven.guise.hook.profile.HookProfile

internal abstract class GuiseBaseHooker<T : HookProfile>
internal constructor(protected val profile: T) : YukiBaseHooker() {

    protected val isEffective = profile.isEffective

    override fun onHook() {
        if (isEffective) {
            doHook()
        }
    }

    abstract fun doHook()
}