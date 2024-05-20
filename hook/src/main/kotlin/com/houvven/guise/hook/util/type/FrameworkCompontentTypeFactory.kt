package com.houvven.guise.hook.util.type

import android.os.Build


val LocationManagerServiceClassName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
    "com.android.server.location.LocationManagerService"
} else {
    "com.android.server.LocationManagerService"
}