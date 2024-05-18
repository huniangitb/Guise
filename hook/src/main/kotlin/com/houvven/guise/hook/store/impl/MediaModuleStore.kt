package com.houvven.guise.hook.store.impl

import android.os.Environment
import com.houvven.guise.hook.profile.HookProfiles
import com.houvven.guise.hook.store.ModuleStore
import java.io.File

object MediaModuleStore {

    class Hooker : ModuleStore.Hooker() {

        private val hooked = MediaModuleStore.Hooked()

        override val configuredPackages: Set<String>
            get() = mediaDir.list()?.filter { isEnabled(it) }?.toSet() ?: emptySet()

        override fun set(profiles: HookProfiles) {
            if (profiles.packageName == null) {
                error("profiles 's package name undefined.")
            }
            val file = getStoreFileAsPackage(profiles.packageName, name)
            if (profiles.isAvailable) {
                file.takeUnless { it.exists() }?.createNewFile()
                file.writeText(profiles.toJsonStr())
            } else {
                kotlin.runCatching { file.delete() }
            }
        }

        override fun get(packageName: String) = hooked.get(packageName)

        override fun isEnabled(packageName: String) = hooked.isEnabled(packageName)
    }


    class Hooked : ModuleStore.Hooked() {

        override fun get(packageName: String): HookProfiles {
            val json = getStoreFileAsPackage(packageName, name)
                .takeUnless { it.exists() }?.readText()

            if (json.isNullOrBlank()) {
                return HookProfiles.Empty
            }
            return HookProfiles.fromJsonStr(json)
        }

        override fun isEnabled(packageName: String): Boolean {
            return getStoreFileAsPackage(packageName, name).exists()
        }
    }

    private val mediaDir = Environment.getExternalStorageDirectory().resolve("Android/media")

    private fun getStoreFileAsPackage(packageName: String, filename: String): File {
        return mediaDir.resolve(packageName).resolve(filename)
    }
}