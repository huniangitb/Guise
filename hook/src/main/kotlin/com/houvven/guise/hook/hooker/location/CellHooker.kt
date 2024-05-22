package com.houvven.guise.hook.hooker.location

import android.telephony.gsm.GsmCellLocation
import com.highcapable.betterandroid.system.extension.tool.SystemVersion
import com.highcapable.yukihookapi.hook.bean.CurrentClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.houvven.guise.hook.profile.HookProfiles
import com.houvven.guise.hook.util.type.CellIdentityGsmClass
import com.houvven.guise.hook.util.type.CellIdentityLteClass
import com.houvven.guise.hook.util.type.CellIdentityNrClass
import com.houvven.guise.hook.util.type.CellIdentityTdscdmaClass
import com.houvven.guise.hook.util.type.CellIdentityWcdmaClass
import com.houvven.guise.hook.util.type.TelephonyManagerClass

internal class CellHooker(private val profile: HookProfiles) : YukiBaseHooker() {

    private val cid = if (profile.disableCellLocation) 0 else profile.cid
    private val lac = if (profile.disableCellLocation) 0 else profile.lac
    private val tac = if (profile.disableCellLocation) 0 else profile.tac
    private val pci = if (profile.disableCellLocation) 0 else profile.pci

    override fun onHook() {
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
                    cid?.let { field { name = "mCid" }.set(it.toInt()) }
                    lac?.let { field { name = "mLac" }.set(it) }
                }
            }
        }

        // Lte
        CellIdentityLteClass.constructor().hookAll().after {
            instance.current {
                setCellIdentityMccMnc()
                cid?.let { field { name = "mCi" }.set(it) }
                tac?.let { field { name = "mTac" }.set(it) }
                pci?.let { field { name = "mPci" }.set(it) }
            }
        }

        // Nr
        SystemVersion.require(SystemVersion.Q) {
            CellIdentityNrClass.constructor().hookAll().after {
                instance.current {
                    setCellIdentityMccMnc()
                    cid?.let { field { name = "mNci" }.set(it) }
                    tac?.let { field { name = "mTac" }.set(it) }
                    pci?.let { field { name = "mPci" }.set(it) }
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
                    val cid = cid?.toInt() ?: cellLocation.cid
                    val lac = lac ?: cellLocation.lac
                    cellLocation.setLacAndCid(lac, cid)
                }
            }
        }
    }
}