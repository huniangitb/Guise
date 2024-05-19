package com.houvven.guise.hook.hooker.location

import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import com.highcapable.betterandroid.system.extension.tool.SystemVersion
import com.highcapable.yukihookapi.hook.factory.allMethods
import com.highcapable.yukihookapi.hook.factory.method
import com.houvven.guise.hook.hooker.base.BaseHooker
import com.houvven.guise.hook.profile.HookProfiles
import com.houvven.guise.hook.util.LocationClass
import com.houvven.guise.hook.util.LocationListenerClass
import com.houvven.guise.hook.util.LocationManagerClass
import java.lang.reflect.Proxy

internal class LocationHooker(profile: HookProfiles) : BaseHooker.Default(profile) {

    override val isEffective: Boolean = profile.isLocationAvailable

    private val locationProvider = SystemVersion.require(
        SystemVersion.S,
        LocationManager.GPS_PROVIDER
    ) { LocationManager.FUSED_PROVIDER }

    override fun doHook() {
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
                    val proxyInstance = Proxy.newProxyInstance(
                        listenerClass.classLoader,
                        arrayOf(LocationListenerClass)
                    ) { _, method, args ->
                        val locationIndex = args.indexOfFirst { it is Location }
                        if (locationIndex != -1) {
                            val location = args[locationIndex] as Location
                            val provideLocation = provideLocation(location)
                            val proxyArgs = args.toMutableList().apply {
                                set(locationIndex, provideLocation)
                            }.toTypedArray()
                            return@newProxyInstance method.invoke(listener, *proxyArgs)
                        }
                        return@newProxyInstance method.invoke(listener, *args)
                    }
                    args[index] = proxyInstance
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
            val index = method.parameterTypes.indexOfFirst { it.isAssignableFrom(LocationClass) }
            if (index != -1) {
                method.hook {
                    before {
                        val origin = args[index] as? Location ?: return@before
                        val provideLocation = provideLocation(origin)
                        args[index] = provideLocation
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