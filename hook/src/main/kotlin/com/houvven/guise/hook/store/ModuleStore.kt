package com.houvven.guise.hook.store

import com.houvven.guise.hook.profile.HookProfiles

abstract class ModuleStore {

    protected open val name: String = "guise.hook.profiles"

    abstract fun get(packageName: String): HookProfiles
    abstract fun isEnabled(packageName: String): Boolean


    abstract class Hooked : ModuleStore()


    abstract class Hooker : ModuleStore() {

        abstract val configuredPackages: Set<String>
        abstract fun set(profiles: HookProfiles)
    }
}