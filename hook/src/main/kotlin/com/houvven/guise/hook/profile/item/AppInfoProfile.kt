package com.houvven.guise.hook.profile.item

import android.os.Parcelable
import com.houvven.guise.hook.profile.HookProfile
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Parcelize
data class AppInfoProfile constructor(
    val versionName: String? = null,
    val versionCode: Int? = null,
) : HookProfile, Parcelable {

    @Transient
    @IgnoredOnParcel
    override val isEffective: Boolean = this != Empty

    companion object {
        val Empty = AppInfoProfile()
    }
}