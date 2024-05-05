package com.houvven.guise.util.app

import android.os.Parcelable
import androidx.compose.ui.graphics.ImageBitmap
import com.houvven.guise.util.EmptyImageBitmap
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize


@Parcelize
data class App(
    val name: String,
    val packageName: String,
    @IgnoredOnParcel
    val icon: ImageBitmap = EmptyImageBitmap,
    val firstInstallTime: Long,
    val lastUpdateTime: Long,
    val isSystemApp: Boolean
) : Parcelable