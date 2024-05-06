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
    private val _appLoadState = MutableStateFlow(AppLoadState())
    val apps = _apps.asStateFlow()
    val appLoadState = _appLoadState.asStateFlow()

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
        _appLoadState.update { AppLoadState.INIT_LOAD_STATE.copy(appSize = appScanner.installedAppsSize) }
        appScanner
            .scanAppsFlow(includeSystemApps = settings.includeSystemApps)
            .collectLatest { app ->
                _apps.update { it + app }
                _appLoadState.update { it.incrementLoadedAppSize() }
            }

        // on each update, cache the apps
        _appLoadState.update { it.doneLoading() }
        cacheApps(_apps.value)
    }

    /**
     * Load apps from the cache
     *
     * @return `true` if the cache is not empty, `false` otherwise
     */
    private suspend fun loadAppsFromCache(): Boolean {
        _appLoadState.initLoadState()

        val packageNames = kv.decodeStringSet(KEY_APPS, emptySet())
        if (packageNames.isNullOrEmpty()) return false

        _appLoadState.setAppSize(packageNames.size)
        appScanner.getAppsFlow(packageNames).collectLatest { app ->
            _apps.update { it + app }
            _appLoadState.incrementLoadedAppSize()
        }
        _appLoadState.doneLoading()
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


    data class AppLoadState(
        val isLoading: Boolean = false,
        val appSize: Int = 0,
        val loadedAppSize: Int = 0
    ) {
        fun setAppSize(size: Int) = copy(appSize = size)

        fun incrementLoadedAppSize() = copy(loadedAppSize = loadedAppSize + 1)

        fun doneLoading() = copy(isLoading = false)

        companion object {
            val INIT_LOAD_STATE = AppLoadState(isLoading = true)
        }
    }

    private fun MutableStateFlow<AppLoadState>.initLoadState() =
        update { AppLoadState.INIT_LOAD_STATE }

    private fun MutableStateFlow<AppLoadState>.setAppSize(size: Int) =
        update { it.setAppSize(size) }

    private fun MutableStateFlow<AppLoadState>.incrementLoadedAppSize() =
        update { it.incrementLoadedAppSize() }

    private fun MutableStateFlow<AppLoadState>.doneLoading() =
        update { it.doneLoading() }
}