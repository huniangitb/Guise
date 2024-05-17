package com.houvven.guise.ui.compontent

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.houvven.guise.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun DangerousOperationsDialog(
    ejected: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
    duration: Int = 3
) {
    if (ejected) {
        var countdown by remember { mutableIntStateOf(duration) }
        val enabled = countdown == 0

        AlertDialog(
            modifier = modifier,
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(
                    onClick = onConfirmClick,
                    enabled = enabled
                ) {
                    Text(text = if (enabled) stringResource(id = R.string.confirm) else "${countdown}s")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
            icon = { Icon(imageVector = Icons.Outlined.Warning, contentDescription = null) },
            title = { Text(text = stringResource(id = R.string.dangerous_operations)) },
            text = { Text(text = stringResource(id = R.string.dangerous_operations_hint)) },
            containerColor = MaterialTheme.colorScheme.errorContainer
        )

        LaunchedEffect(Unit) {
            launch {
                repeat(duration) {
                    delay(1000)
                    countdown--
                }
            }
        }
    }
}