package com.houvven.guise.hook.hooker.location

import android.telephony.gsm.GsmCellLocation
import com.highcapable.betterandroid.system.extension.tool.SystemVersion
import com.highcapable.yukihookapi.hook.bean.CurrentClass
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.houvven.guise.hook.hooker.base.BaseHooker
import com.houvven.guise.hook.profile.HookProfiles
import com.houvven.guise.hook.util.CellIdentityGsmClass
import com.houvven.guise.hook.util.CellIdentityLteClass
import com.houvven.guise.hook.util.CellIdentityNrClass
import com.houvven.guise.hook.util.CellIdentityTdscdmaClass
import com.houvven.guise.hook.util.CellIdentityWcdmaClass
import com.houvven.guise.hook.util.TelephonyManagerClass

internal class CellLocationHooker(profiles: HookProfiles) : BaseHooker.Default(profiles) {

    override fun doHook() {
        this.hookCellIdentity()
        this.hookCellLocationGetter()
    }

    private fun hookCellIdentity() {
        val setCellIdentityMccMnc: CurrentClass.() -> Unit = {
            profile.mcc?.let { field { name = "mMccStr"; superClass(true) }.set(it) }
            profile.mnc?.let { field { name = "mMncStr"; superClass(true) }.set(it) }
        }

        listOf(
            CellIdentityGsmClass,
            CellIdentityWcdmaClass,
            CellIdentityTdscdmaClass
        ).forEach { clazz ->
            clazz.constructor().hookAll().after {
                instance.current {
                    setCellIdentityMccMnc()
                    profile.cid?.let { field { name = "mCid" }.set(it) }
                    profile.lac?.let { field { name = "mLac" }.set(it) }
                }
            }
        }

        // Lte
        CellIdentityLteClass.constructor().hookAll().after {
            instance.current {
                setCellIdentityMccMnc()
                profile.cid?.let { field { name = "mCi" }.set(it) }
                profile.tac?.let { field { name = "mTac" }.set(it) }
                profile.pci?.let { field { name = "mPci" }.set(it) }
            }
        }

        // Nr
        SystemVersion.require(SystemVersion.Q) {
            CellIdentityNrClass.constructor().hookAll().after {
                instance.current {
                    setCellIdentityMccMnc()
                    profile.cid?.let { field { name = "mNci" }.set(it) }
                    profile.tac?.let { field { name = "mTac" }.set(it) }
                    profile.pci?.let { field { name = "mPci" }.set(it) }
                }
            }
        }
    }

    private fun hookCellLocationGetter() {
        TelephonyManagerClass.method {
            name = "getCellLocation"
        }.ignored().hook().after {
            when (val cellLocation = result) {
                is GsmCellLocation -> {
                    val cid = profile.cid?.toInt() ?: cellLocation.cid
                    val lac = profile.lac ?: cellLocation.lac
                    cellLocation.setLacAndCid(lac, cid)
                }
            }
        }
    }
}