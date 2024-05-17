package com.houvven.guise.hook.profile.item

import android.os.Parcelable
import com.houvven.guise.hook.profile.HookProfile
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Parcelize
data class AppProfile(
    val packageInfo: PackageInfoProfile = PackageInfoProfile.Empty,
    val language: String? = null,
    val nightMode: Boolean? = null,
    val densityDpi: Int? = null,
    val fontScale: Float? = null
) : HookProfile, Parcelable {

    @Transient
    @IgnoredOnParcel
    override val isEffective: Boolean = this != Empty

    companion object {
        val Empty = AppProfile()
    }

    @Serializable
    @Parcelize
    data class PackageInfoProfile(
        val versionName: String? = null,
        val versionCode: Int? = null
    ) : HookProfile, Parcelable {

        @Transient
        @IgnoredOnParcel
        override val isEffective: Boolean = this != Empty

        companion object {
            val Empty = PackageInfoProfile()
        }
    }
}