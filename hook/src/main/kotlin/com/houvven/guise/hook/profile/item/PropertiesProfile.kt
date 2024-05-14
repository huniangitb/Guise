package com.houvven.guise.hook.profile.item

import android.os.Parcelable
import com.houvven.guise.hook.profile.HookProfile
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Parcelize
data class PropertiesProfile(
    val brand: String? = null,
    val model: String? = null,
    val product: String? = null,
    val device: String? = null,

    val characteristics: String? = null,

    val customProperties: Map<String, String> = emptyMap()
) : HookProfile, Parcelable {

    @IgnoredOnParcel
    @Transient
    val manufacturer = brand

    @IgnoredOnParcel
    @Transient
    override val isEffective: Boolean = this != Empty

    companion object {

        val Empty =  PropertiesProfile()

        const val UNKNOWN = "unknown"

        const val KEY_BRAND = "ro.product.brand"
        const val KEY_MODEL = "ro.product.model"
        const val KEY_MANUFACTURER = "ro.product.manufacturer"
        const val KEY_PRODUCT = "ro.product.name"
        const val KEY_DEVICE = "ro.product.device"
    }
}