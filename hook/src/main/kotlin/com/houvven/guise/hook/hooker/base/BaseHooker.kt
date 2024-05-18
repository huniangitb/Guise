package com.houvven.guise.hook.hooker.base

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.houvven.guise.hook.profile.HookProfiles
import com.houvven.guise.hook.profile.Profile

internal abstract class BaseHooker<T : Profile>
internal constructor(protected val profile: T) : YukiBaseHooker() {

    protected open val isEffective = profile.isAvailable

    override fun onHook() {
        if (isEffective) {
            doHook()
        }
    }

    abstract fun doHook()

    abstract class Default(profile: HookProfiles) : BaseHooker<HookProfiles>(profile)
}