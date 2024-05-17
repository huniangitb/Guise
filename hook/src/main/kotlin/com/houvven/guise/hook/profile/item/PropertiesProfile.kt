package com.houvven.guise.hook.profile.item

import com.houvven.guise.hook.profile.Profile
import kotlinx.serialization.Serializable

@Serializable
data class PropertiesProfile(
    val brand: String? = null,
    val manufacturer: String? = brand,
    val model: String? = null,
    val product: String? = null,
    val device: String? = null,
    val displayId: String? = null,
    val fingerprint: String? = null,

    val characteristics: String? = null,

    val customProperties: Map<String, String> = emptyMap()
) : Profile {

    override val isAvailable: Boolean = this != Empty

    companion object {

        val Empty = PropertiesProfile()
    }
}