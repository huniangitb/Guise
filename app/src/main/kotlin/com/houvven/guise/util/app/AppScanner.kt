package com.houvven.guise.util.app

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmapOrNull
import com.houvven.guise.util.EmptyImageBitmap
import com.houvven.guise.util.isSystemApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

/**
 * A scanner for installed apps.
 *
 * @property packageManager The [PackageManager] to use for scanning.
 */
class AppScanner(private val packageManager: PackageManager) {


    /**
     * The list of installed apps.
     */
    private val installedPackages: List<PackageInfo> get() = packageManager.getInstalledPackages(0)

    /**
     * The number of installed apps.
     */
    val installedAppsSize: Int get() = installedPackages.size

    /**
     * Scans the installed apps and returns a flow of [App]s.
     *
     * @param includeSystemApps Whether to include system apps.
     * @return A [Flow] of [App]s.
     */
    fun scanAppsFlow(includeSystemApps: Boolean = false): Flow<App> {
        return flow {
            installedPackages
                .filter { !it.isSystemApp || includeSystemApps }
                .forEach { emit(createApp(it)) }
        }
    }

    /**
     * Gets the app with the specified package name.
     * @param packageName The package name of the app.
     * @return The app with the specified package name, or `null` if the app is not installed.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun getApp(packageName: String): App? {
        return installedPackages.find { it.packageName == packageName }?.let { packageInfo ->
            createApp(packageInfo)
        }
    }

    /**
     * Gets the apps with the specified package names.
     * @param packageNames The package names of the apps.
     * @return A flow of apps with the specified package names.
     */
    fun getAppsFlow(packageNames: Set<String>): Flow<App> {
        return flow {
            installedPackages
                .filter { it.packageName in packageNames }
                .forEach { emit(createApp(it)) }
        }
    }

    suspend fun getApps(packageNames: Set<String>): List<App> {
        return withContext(Dispatchers.Default) {
            packageNames.map { async { getApp(it) } }.awaitAll().filterNotNull()
        }
    }


    /**
     * Creates an [App] from the specified [PackageInfo].
     */
    private fun createApp(packageInfo: PackageInfo) = packageInfo.run {
        val icon = applicationInfo.loadIcon(packageManager).toBitmapOrNull()?.asImageBitmap()
        App(
            name = applicationInfo.loadLabel(packageManager).toString(),
            packageName = packageName,
            icon = icon ?: EmptyImageBitmap,
            firstInstallTime = firstInstallTime,
            lastUpdateTime = lastUpdateTime,
            isSystemApp = isSystemApp
        )
    }

    companion object {
        const val TAG = "AppScanner"
    }
}