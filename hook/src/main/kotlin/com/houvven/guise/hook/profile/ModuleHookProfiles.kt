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
        get() = this != ModuleHookProfiles(packageName = this.packageName)

    companion object {

        @JvmStatic
        val Empty = ModuleHookProfiles()

        @JvmStatic
        val Debug = ModuleHookProfiles(
            properties = PropertiesProfile(
                characteristics = "tablet", // QQ使用这个特征判断是否是平板
                customProperties = mapOf(
                    "ro.product.brand" to "HUAWEI",
                    "ro.product.model" to "HUAWEI P30 Pro",
                    "ro.product.manufacturer" to "HUAWEI",
                    "ro.product.name" to "VOG-AL00",
                    "ro.product.device" to "VOG-AL00"
                )
            )
        )
    }
}