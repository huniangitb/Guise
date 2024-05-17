package com.houvven.guise.util.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.topjohnwu.superuser.ShellUtils

object AppUtils {

    fun cmdStopApp(packageName: String): Boolean {
        if (packageName.isBlank()) return false
        return ShellUtils.fastCmdResult("am force-stop $packageName")
    }

    fun cmdClearData(packageName: String): Boolean {
        if (packageName.isBlank()) return false
        return ShellUtils.fastCmdResult("pm clear $packageName")
    }

    fun toDetails(context: Context, packageName: String): Boolean {
        return runCatching {
            val intent = Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }.isSuccess
    }

    fun launchApp(context: Context, packageName: String): Boolean {
        return runCatching {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            context.startActivity(intent)
        }.isSuccess
    }
}