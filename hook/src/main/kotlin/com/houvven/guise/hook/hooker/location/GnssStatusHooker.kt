package com.houvven.guise.hook.hooker.location

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.houvven.guise.hook.util.GnssStatusClass
import com.houvven.guise.hook.util.LocationManagerClass

internal class GnssStatusHooker : YukiBaseHooker() {

    override fun onHook() {
        this.hookGnssStatus()
        listOf(
            "addNmeaListener",
            "addGpsStatusListener",
            "registerGnssStatusCallback",
            "registerGnssMeasurementsCallback",
            "registerGnssNavigationMessageCallback",
            "registerAntennaInfoListener"
        ).forEach {
            LocationManagerClass.method { name(it) }.hookAll().replaceToFalse()
        }
    }

    private fun hookGnssStatus() {
        /*
        private final int mSvCount;
        private final int[] mSvidWithFlags;
        private final float[] mCn0DbHzs;
        private final float[] mElevations;
        private final float[] mAzimuths;
        private final float[] mCarrierFrequencies;
        private final float[] mBasebandCn0DbHzs;
        */
        GnssStatusClass.constructor().hookAll().after {
            val intArr = intArrayOf(0)
            val floatArr = floatArrayOf(0.0f)
            instance.current(ignored = true) {
                field { name = "mSvCount" }.set(0)
                field { name = "mSvidWithFlags" }.set(intArr)
                field { name = "mCn0DbHzs" }.set(floatArr)
                field { name = "mElevations" }.set(floatArr)
                field { name = "mAzimuths" }.set(floatArr)
                field { name = "mCarrierFrequencies" }.set(floatArr)
                field { name = "mBasebandCn0DbHzs" }.set(floatArr)
            }
        }
    }
}