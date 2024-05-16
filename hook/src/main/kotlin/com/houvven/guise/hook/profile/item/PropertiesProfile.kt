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
    val manufacturer: String? = brand,
    val model: String? = null,
    val product: String? = null,
    val device: String? = null,
    val fingerprint: String? = null,

    val characteristics: String? = null,

    val customProperties: Map<String, String> = emptyMap()
) : HookProfile, Parcelable {

    @IgnoredOnParcel
    @Transient
    override val isEffective: Boolean = this != Empty

    companion object {

        val Empty = PropertiesProfile()
    }
}