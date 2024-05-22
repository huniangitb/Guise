package com.houvven.guise.hook.hooker.location

import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import com.highcapable.betterandroid.system.extension.tool.SystemVersion
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.allMethods
import com.highcapable.yukihookapi.hook.factory.method
import com.houvven.guise.hook.profile.HookProfiles
import com.houvven.guise.hook.util.type.LocationClass
import com.houvven.guise.hook.util.type.LocationListenerClass
import com.houvven.guise.hook.util.type.LocationManagerClass

internal class LocationHooker(private  val profile: HookProfiles) : YukiBaseHooker() {


    private val locationProvider = SystemVersion.require(
        SystemVersion.S,
        LocationManager.GPS_PROVIDER
    ) { LocationManager.FUSED_PROVIDER }

    override fun onHook() {
        loadHooker(GnssStatusHooker())
        this.hookLocationRequestUpdates()
        this.hookProviderStateGetter()
        this.hookLastKnownLocationGetter()
    }

    private fun hookLocationRequestUpdates() {
        val targetMethods = listOf("requestSingleUpdate", "requestLocationUpdates")
        LocationManagerClass.allMethods { _, m ->
            val index = m.parameterTypes.indexOfFirst { it.isAssignableFrom(LocationListenerClass) }
            val condition1 = targetMethods.contains(m.name)
            val condition2 = index != -1
            if (!condition1 || !condition2) {
                return@allMethods
            }

            m.hook {
                before {
                    val listener = args[index] as LocationListener
                    val listenerClass = listener::class.java
                    setLocationListener(listenerClass)
                }
            }
        }
    }

    private fun hookLastKnownLocationGetter() = LocationManagerClass.run {
        method { name = "getLastKnownLocation" }.hookAll().replaceTo(provideLocation())
        method { name = "getLastLocation" }.ignored().hookAll().replaceTo(provideLocation())
    }

    private fun hookProviderStateGetter() {
        LocationManagerClass.run {
            method { name = "isLocationEnabledForUser" }.hookAll().replaceToTrue()
            method { name = "isLocationEnabled" }.hookAll().replaceToTrue()
            method { name = "isProviderEnabledForUser" }.hookAll().replaceToTrue()
            method { name = "getBestProvider" }.hookAll().replaceTo(LocationManager.GPS_PROVIDER)
            method { name = "getProviders" }.hookAll().after {
                val providers = result as List<*>? ?: return@after
                result = providers.toMutableSet().add(LocationManager.GPS_PROVIDER)
            }
        }
    }

    private fun setLocationListener(clazz: Class<out LocationListener>) {
        clazz.allMethods { _, method ->
            val locationIndex =
                method.parameterTypes.indexOfFirst { it.isAssignableFrom(LocationClass) }
            if (locationIndex != -1) {
                method.hook {
                    before {
                        val origin = args[locationIndex] as? Location ?: return@before
                        val provideLocation = provideLocation(origin)
                        args[locationIndex] = provideLocation
                    }
                }
            }
        }
    }

    private fun provideLocation(location: Location = Location(locationProvider)): Location {
        return location.apply {
            longitude = profile.longitude ?: longitude
            latitude = profile.latitude ?: latitude
            time = System.currentTimeMillis()
        }
    }
}