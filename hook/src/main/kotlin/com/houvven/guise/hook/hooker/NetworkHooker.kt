package com.houvven.guise.hook.hooker

import android.net.ConnectivityManager
import com.highcapable.yukihookapi.hook.factory.classOf
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.houvven.guise.hook.hooker.base.BaseHooker
import com.houvven.guise.hook.profile.HookProfiles

internal class NetworkHooker(profile: HookProfiles) : BaseHooker.Default(profile) {

    override fun doHook() {
        this.hookActiveNetwork()
    }

    private fun hookActiveNetwork() {
        val networkType = profile.networkType ?: return
        classOf<ConnectivityManager>().run {
            method {
                name = "getActiveNetwork"
            }.hookAll().after {
                result = result?.current {
                    field { name = "netId" }.set(networkType)
                }
            }
        }
    }
}