package com.houvven.guise.ui.style

import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


val OutlinedTextFieldTransparentBorderColor
    @Composable
    get() = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color.Transparent,
        disabledBorderColor = Color.Transparent,
        unfocusedBorderColor = Color.Transparent,
        errorBorderColor = Color.Transparent
    )