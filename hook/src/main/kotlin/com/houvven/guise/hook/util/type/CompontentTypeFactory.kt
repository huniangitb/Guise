package com.houvven.guise.hook.util.type

import android.location.GnssStatus
import android.location.GpsStatus
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationRequest
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.CellIdentity
import android.telephony.CellIdentityCdma
import android.telephony.CellIdentityGsm
import android.telephony.CellIdentityLte
import android.telephony.CellIdentityNr
import android.telephony.CellIdentityTdscdma
import android.telephony.CellIdentityWcdma
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import com.highcapable.yukihookapi.hook.factory.classOf
import com.highcapable.yukihookapi.hook.factory.toClass
import java.util.concurrent.Executor


val SystemPropertiesClass = "android.os.SystemProperties".toClass()

val LocationClass = classOf<Location>()

val LocationManagerClass = classOf<LocationManager>()

val LocationRequestClass = classOf<LocationRequest>()

val LocationListenerClass = classOf<LocationListener>()

val ExecutorClass = classOf<Executor>()

val TelephonyManagerClass = classOf<TelephonyManager>()

val CellIdentityClass = classOf<CellIdentity>()

@RequiresApi(Build.VERSION_CODES.Q)
val CellIdentityNrClass = classOf<CellIdentityNr>()

val CellIdentityLteClass = classOf<CellIdentityLte>()

val CellIdentityGsmClass = classOf<CellIdentityGsm>()

val CellIdentityCdma = classOf<CellIdentityCdma>()

val CellIdentityWcdmaClass = classOf<CellIdentityWcdma>()

val CellIdentityTdscdmaClass = classOf<CellIdentityTdscdma>()

val GnssStatusClass = classOf<GnssStatus>()

val GpsStatusClass = classOf<GpsStatus>()

val ConnectivityManagerClass = classOf<ConnectivityManager>()

val WifiManagerClass  = classOf<WifiManager>()