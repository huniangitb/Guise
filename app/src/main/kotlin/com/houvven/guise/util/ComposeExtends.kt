package com.houvven.guise.util

import android.graphics.drawable.Drawable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmapOrNull

val EmptyImageBitmap: ImageBitmap = ImageBitmap(1, 1) // 1x1 pixel image

fun Drawable.toImageBitmapOrEmpty() = toBitmapOrNull()?.asImageBitmap() ?: EmptyImageBitmap

inline fun <T> MutableState<T>.update(function: (T) -> T) {
    value = function(value)
}