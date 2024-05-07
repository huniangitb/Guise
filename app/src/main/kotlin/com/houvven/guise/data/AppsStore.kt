package com.houvven.guise.data

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dylanc.mmkv.IMMKVOwner
import com.dylanc.mmkv.MMKVOwner
import com.houvven.guise.util.app.App
import com.houvven.guise.util.app.AppScanner
import com.houvven.guise.util.app.AppSortComparator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

class AppsStore(
    private val appScanner: AppScanner
) : IMMKVOwner by MMKVOwner(ID), ViewModel() {

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
        val totalAppSize = appScanner.installedAppsSizeAsUser

        _apps.update { emptyList() }
        _appLoadState.update { AppLoadState.INIT_LOAD_STATE.copy(totalAppSize = totalAppSize) }
        appScanner.scanAppsFlowAsUser(includeSystemApps = true)
            .collect { app ->
                _apps.update { it + app }
                _appLoadState.incrementLoadedAppSize()
                _appLoadState.tryDone {
                    _apps.update { it.sortedWith(AppSortComparator.AppNameLocaleComparator) }
                }
            }

        // on each update, cache the apps
        cacheApps(_apps.value)
    }

    /**
     * Load apps from the cache
     *
     * @return `true` if the cache is not empty, `false` otherwise
     */
    private suspend fun loadAppsFromCache(): Boolean {
        _apps.update { emptyList() }
        _appLoadState.initLoadState()

        val packageNames = kv.decodeStringSet(KEY_APPS, emptySet())
        if (packageNames.isNullOrEmpty()) return false

        _appLoadState.setTotalAppSize(packageNames.size)
        appScanner.getAppsFlowAsUser(packageNames).collect { app ->
            _apps.update { it + app }
            _appLoadState.incrementLoadedAppSize()
            _appLoadState.tryDone {
                _apps.update { it.sortedWith(AppSortComparator.AppNameLocaleComparator) }
            }
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
        val includeSystemApps: Boolean = true
    ) : Parcelable


    data class AppLoadState(
        val isLoading: Boolean = false,
        val totalAppSize: Int = 0,
        val loadedAppSize: Int = 0
    ) {
        @Suppress("unused")
        val progress: Float get() = loadedAppSize / totalAppSize.toFloat()

        val isDoneLoading: Boolean get() = totalAppSize == loadedAppSize

        companion object {
            val INIT_LOAD_STATE = AppLoadState(isLoading = true)
        }
    }

    private fun MutableStateFlow<AppLoadState>.initLoadState() =
        update { AppLoadState.INIT_LOAD_STATE }

    private fun MutableStateFlow<AppLoadState>.setTotalAppSize(size: Int) =
        update { it.copy(totalAppSize = size) }

    private fun MutableStateFlow<AppLoadState>.incrementLoadedAppSize() =
        update { it.copy(loadedAppSize = it.loadedAppSize + 1) }

    private fun MutableStateFlow<AppLoadState>.tryDone(callback: () -> Unit = {}) {
        if (value.isDoneLoading) {
            callback()
            update { it.copy(isLoading = false) }
        }
    }
}