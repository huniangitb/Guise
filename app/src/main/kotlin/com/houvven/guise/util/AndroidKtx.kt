package com.houvven.guise.util

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.os.UserHandle
import com.highcapable.yukireflection.factory.classOf
import com.highcapable.yukireflection.factory.method

private const val TAG = "AndroidKtx"

/**
 * Check if the package is a system app
 * @return true if the package is a system app
 */
val PackageInfo.isSystemApp: Boolean get() = applicationInfo.isSystemApp

/**
 * Check if the application is a system app
 * @return true if the application is a system app
 */
val ApplicationInfo.isSystemApp: Boolean get() = flags and ApplicationInfo.FLAG_SYSTEM != 0

/**
 * Current app run user 's id
 */
val MY_USER_ID = runCatching {
    classOf<UserHandle>().method { name("myUserId") }.get(null).int()
}.getOrDefault(-1)


