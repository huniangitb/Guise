package com.houvven.guise.hook.profile

import com.houvven.guise.hook.profile.item.PropertiesProfile
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
data class ModuleHookProfiles(
    @Transient
    val packageName: String? = null,
    val properties: PropertiesProfile = PropertiesProfile.EMPTY
) : HookProfile {

    override val isEffective: Boolean
        get() = this != Empty.copy(packageName = this.packageName)

    companion object {

        @JvmStatic
        val Empty = ModuleHookProfiles()

        @JvmStatic
        val Debug = ModuleHookProfiles(
            properties = PropertiesProfile(
                brand = "Xiaomi",
                model = "M2105K81C",
                characteristics = "tablet"
            )
        )
    }
}