package com.houvven.guise.util.app

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import com.houvven.guise.util.isSystemApp
import com.houvven.guise.util.toImageBitmapOrEmpty
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
class AppScanner(
    private val packageManager: PackageManager,
    private val excludedPackages: Set<String> = emptySet()
) {

    /**
     * The list of installed apps.
     */
    private val installedApplicationsAsUser
        get() = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

    private val installedPackages
        get() = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)


    /**
     * The number of installed apps as all users.
     */
    val installedAppsSize: Int
        get() = (installedPackages.size - excludedPackages.size).also {
            Log.d(TAG, "installed app size as all users: $it")
        }

    val installedAppsSizeAsUser: Int
        get() = (installedApplicationsAsUser.size - excludedPackages.size).also {
            Log.d(TAG, "installed app size as user: $it")
        }


    /**
     * Scans the installed apps and returns a flow of [App]s.
     *
     * @param includeSystemApps Whether to include system apps.
     * @return A [Flow] of [App]s.
     */
    fun scanAppsFlow(includeSystemApps: Boolean = false): Flow<App> {
        return flow {
            installedPackages.map { it.applicationInfo }
                .filter { (!it.isSystemApp || includeSystemApps) && !excludedPackages.contains(it.packageName) }
                .forEach { emit(createApp(it)) }
        }
    }

    /**
     * Scans the installed apps for the user and returns a flow of [App]s.
     *
     * This method filters out apps that are not for the current user.
     * @param includeSystemApps Whether to include system apps.
     * @return A [Flow] of [App]s.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun scanAppsFlowAsUser(includeSystemApps: Boolean = false): Flow<App> {
        return flow {
            installedApplicationsAsUser
                .filter { (!it.isSystemApp || includeSystemApps) && !excludedPackages.contains(it.packageName) }
                .forEach { emit(createApp(it)) }
        }
    }


    /**
     * Gets the app with the specified package name.
     * @param packageName The package name of the app.
     * @return The app with the specified package name, or `null` if the app is not installed.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun getAppAsUser(
        packageName: String,
        applications: List<ApplicationInfo> = this.installedApplicationsAsUser
    ): App? {
        return applications.find { it.packageName == packageName }?.let { packageInfo ->
            createApp(packageInfo)
        }
    }

    suspend fun getAppsAsUser(packageNames: Set<String>): List<App> {
        return withContext(Dispatchers.Default) {
            installedApplicationsAsUser
                .filter { packageNames.contains(it.packageName) }
                .map { async { createApp(it) } }.awaitAll()
        }
    }

    /**
     * Gets the apps with the specified package names.
     * @param packageNames The package names of the apps.
     * @return A flow of apps with the specified package names.
     */
    fun getAppsFlowAsUser(packageNames: Set<String>): Flow<App> {
        return flow {
            installedApplicationsAsUser
                .filter { packageNames.contains(it.packageName) }
                .forEach { emit(createApp(it)) }
        }
    }


    /**
     * Creates an [App] from the specified [PackageInfo].
     */
    private fun createApp(applicationInfo: ApplicationInfo) = applicationInfo.run {
        App(
            name = loadLabel(packageManager).toString(),
            packageName = packageName,
            icon = loadIcon(packageManager).toImageBitmapOrEmpty(),
            isSystemApp = isSystemApp
        )
    }


    companion object {
        const val TAG = "AppScanner"
    }
}