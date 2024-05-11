package com.houvven.guise.ui.screen.launch.home.components

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.houvven.guise.R
import com.houvven.guise.ui.compontent.KeyValueCol


@Composable
fun ModuleInactiveView() {
    Column(
        modifier = Modifier.padding(40.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
    ) {
        Icon(
            Icons.Rounded.Error,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(36.dp)
        )
        // Title
        Text(
            text = stringResource(id = R.string.module_inactive),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error.copy(alpha = .8f)
        )
        Text(
            text = stringResource(id = R.string.module_inactive_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Unspecified.copy(alpha = .7f)
        )
        Spacer(modifier = Modifier.padding(2.dp))
        // Android Version
        KeyValueCol(
            key = stringResource(id = R.string.android_version),
            value = "${Build.VERSION.RELEASE}(${Build.VERSION.SDK_INT})"
        )
        // Device Model
        KeyValueCol(
            key = stringResource(id = R.string.device_model),
            value = "${Build.BRAND} ${Build.MODEL}"
        )
        // OS Version
        KeyValueCol(
            key = stringResource(id = R.string.os_version),
            value = Build.DISPLAY
        )
        // Support ABIs
        KeyValueCol(
            key = stringResource(id = R.string.support_abi),
            value = Build.SUPPORTED_ABIS.joinToString()
        )
    }
}