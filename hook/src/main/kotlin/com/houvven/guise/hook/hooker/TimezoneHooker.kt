package com.houvven.guise.hook.hooker

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.TimeZoneClass
import com.houvven.guise.hook.profile.HookProfiles
import java.util.TimeZone

internal class TimezoneHooker(private val profile: HookProfiles) : YukiBaseHooker() {


    override fun onHook() {
        profile.timezone?.let {
            TimeZoneClass.method {
                name = "getDefaultRef"
            }.hook().replaceTo(TimeZone.getTimeZone(profile.timezone))
        }
    }
}