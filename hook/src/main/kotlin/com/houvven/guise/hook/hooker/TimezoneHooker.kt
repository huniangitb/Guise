package com.houvven.guise.hook.hooker

import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.TimeZoneClass
import com.houvven.guise.hook.hooker.base.BaseHooker
import com.houvven.guise.hook.profile.HookProfiles
import java.util.TimeZone

internal class TimezoneHooker(profiles: HookProfiles) : BaseHooker.Default(profiles) {

    override val isEffective: Boolean get() = !profile.timezone.isNullOrBlank()

    override fun doHook() {
        TimeZoneClass.method {
            name = "getDefaultRef"
        }.hook().replaceTo(TimeZone.getTimeZone(profile.timezone))
    }
}