package com.houvven.guise.hook.hooker

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.houvven.guise.hook.profile.HookProfiles
import com.houvven.guise.hook.util.type.ConnectivityManagerClass
import com.houvven.guise.hook.util.type.TelephonyManagerClass

internal class NetworkHooker(private val profile: HookProfiles) : YukiBaseHooker() {

    override fun onHook() {
        this.hookActiveNetworkType()
        this.hookMobileNetworkType()
    }

    private fun hookActiveNetworkType() {
        profile.networkType?.let { networkType ->
            ConnectivityManagerClass.method {
                name = "getActiveNetwork"
            }.hookAll().after {
                result = result?.current {
                    field { name = "netId" }.set(networkType)
                }
            }

            ConnectivityManagerClass.method {
                name = "getActiveNetworkInfo"
            }.hookAll().after {
                result = result?.current(ignored = true) {
                    field { name = "mNetworkType" }.set(networkType)
                }
            }
        }
    }

    private fun hookMobileNetworkType() {
        profile.mobileNetType?.let { type ->
            listOf("getNetworkType", "getDataNetworkType").forEach { methodName ->
                TelephonyManagerClass.method {
                    name = methodName
                    param(IntType)
                }.hook().replaceTo(type)
            }
        }
    }
}