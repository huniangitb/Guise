package com.houvven.guise.ui.compontent

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily


@Composable
fun KeyValueCol(
    key: String, value: String,
    textStyles: KeyValueTextStyles = KeyValueDefaults.defaultKeyValueTextStyles()
) {
    Column {
        CardDefaults.outlinedCardBorder()
        Text(
            text = key,
            style = textStyles.keyStyle,
        )
        Text(
            text = value,
            style = textStyles.valueStyle
        )
    }
}

object KeyValueDefaults {

    @Composable
    fun defaultKeyValueTextStyles(): KeyValueTextStyles {
        return KeyValueTextStyles(
            keyStyle = MaterialTheme.typography.titleSmall.copy(color = Color.Unspecified.copy(alpha = .6f)),
            valueStyle = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.SansSerif)
        )
    }
}

@Immutable
data class KeyValueTextStyles(
    val keyStyle: TextStyle,
    val valueStyle: TextStyle
)