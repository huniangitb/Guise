package com.houvven.guise.util

import org.lsposed.lspd.ILSPManagerService
import org.lsposed.lspd.models.Application

/**
 * Creates an Application object with the provided user ID and package name.
 * @param uid The user ID to set in the Application object.
 * @param packageName The package name to set in the Application object.
 * @return The created Application object.
 */
fun createModuleApplication(uid: Int, packageName: String) = Application().apply {
    userId = uid
    this.packageName = packageName
}

/**
 * Creates a list of Application objects from a map of user IDs and package names.
 * @param map The map of user IDs and package names.
 * @return The list of created Application objects.
 */
fun createModuleApplications(map: Map<Int, String>) =
    map.map { (uid, packageName) -> createModuleApplication(uid, packageName) }

/**
 * Creates a list of Application objects from vararg pairs of user IDs and package names.
 * @param pairs The vararg pairs of user IDs and package names.
 * @return The list of created Application objects.
 */
fun createModuleApplications(vararg pairs: Pair<Int, String>) =
    createModuleApplications(pairs.toMap())

/**
 * Creates a list of Application objects with the same user ID and different package names.
 * @param uid The user ID to set in the Application objects.
 * @param packageNames The vararg package names to set in the Application objects.
 * @return The list of created Application objects.
 */
fun createModuleApplications(uid: Int, vararg packageNames: String) =
    packageNames.map { createModuleApplication(uid, it) }


fun ILSPManagerService.putModuleScope(
    moduleName: String,
    applicationsFunc: () -> Set<Application>
): Boolean {
    val moduleScope = getModuleScope(moduleName).toMutableSet()
    moduleScope.addAll(applicationsFunc())
    return setModuleScope(moduleName, moduleScope.toList())
}

fun ILSPManagerService.removeModuleScope(
    moduleName: String,
    applicationsFunc: () -> Set<Application>
): Boolean {
    val moduleScope = getModuleScope(moduleName).toMutableSet()
    moduleScope.removeAll(applicationsFunc())
    return setModuleScope(moduleName, moduleScope.toList())
}