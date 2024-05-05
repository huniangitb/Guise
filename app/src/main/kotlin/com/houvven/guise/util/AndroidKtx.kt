package com.houvven.guise.util

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo


val PackageInfo.isSystemApp: Boolean get() = applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0