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
    val versionCode: Long,
    val versionName: String,
    val dataDir: String,
    val isSystemApp: Boolean
) : Parcelable