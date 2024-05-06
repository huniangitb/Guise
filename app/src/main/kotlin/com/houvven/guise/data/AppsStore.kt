package com.houvven.guise.data

import android.content.pm.PackageManager
import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dylanc.mmkv.IMMKVOwner
import com.dylanc.mmkv.MMKVOwner
import com.houvven.guise.util.app.App
import com.houvven.guise.util.app.AppScanner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

class AppsStore(packageManager: PackageManager) : IMMKVOwner by MMKVOwner(ID), ViewModel() {

    private val appScanner = AppScanner(packageManager)

    private val _apps = MutableStateFlow(emptyList<App>())

    /**
     * The list of apps
     */
    val apps = _apps.asStateFlow()

    private val _appsSettings = MutableStateFlow(
        kv.decodeParcelable(
            KEY_APPS_SETTINGS,
            AppsSettings::class.java,
            AppsSettings()
        )!!
    )

    /**
     * The apps settings, such as whether to include system apps
     */
    val appsSettings = _appsSettings.asStateFlow()

    init {
        initLoadApps()
    }


    /**
     * On class initialization, load the apps from the cache,
     * if the cache is empty, load the apps from the device
     */
    private fun initLoadApps() {
        viewModelScope.launch(Dispatchers.Default) {
            if (!loadAppsFromCache()) loadApps()
        }
    }

    /**
     * Load the apps from the device, when it's done, cache the apps
     */
    @Suppress("MemberVisibilityCanBePrivate")
    suspend fun loadApps() {
        val settings = _appsSettings.value
        appScanner
            .scanAppsFlow(includeSystemApps = settings.includeSystemApps)
            .collectLatest { app -> _apps.update { it + app } }

        // on each update, cache the apps
        cacheApps(_apps.value)
    }

    /**
     * Load apps from the cache
     *
     * @return `true` if the cache is not empty, `false` otherwise
     */
    private suspend fun loadAppsFromCache(): Boolean {
        val packageNames = kv.decodeStringSet(KEY_APPS, emptySet())
        if (packageNames.isNullOrEmpty()) return false

        appScanner.getAppsFlow(packageNames).collectLatest { app ->
            _apps.update { it + app }
        }

        return _apps.value.isNotEmpty()
    }

    /**
     * Cache the apps
     *
     * @param apps The apps to cache
     */
    private fun cacheApps(apps: List<App>) {
        apps.map { it.packageName }.toSet().let { kv.encode(KEY_APPS, it) }
    }


    companion object {
        const val ID = "installed_apps"
        const val KEY_APPS = "apps"
        const val KEY_APPS_SETTINGS = "apps_settings"
    }

    @Parcelize
    @Serializable
    data class AppsSettings(
        val includeSystemApps: Boolean = false
    ) : Parcelable
}