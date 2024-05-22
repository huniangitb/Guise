package com.houvven.guise.ui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.houvven.guise.R
import com.houvven.guise.client.LServiceBridgeClient
import com.houvven.guise.ui.theme.AppTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs


val LocalNavHostController: ProvidableCompositionLocal<NavHostController> =
    staticCompositionLocalOf { error("Not provided") }

val LocalSnackBarHostState: ProvidableCompositionLocal<SnackbarHostState> =
    staticCompositionLocalOf { error("Not provided") }

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GuiseApp(
    navController: NavHostController = rememberNavController(),
    hostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    AppTheme {
        CompositionLocalProvider(
            LocalNavHostController provides navController,
            LocalSnackBarHostState provides hostState
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                snackbarHost = {
                    SnackbarHost(hostState = LocalSnackBarHostState.current) {
                        Snackbar(snackbarData = it)
                    }
                }
            ) {
                DestinationsNavHost(
                    navController = LocalNavHostController.current,
                    navGraph = NavGraphs.root
                )

                LServiceErrorStatusDia()
            }
        }
    }
}


@Composable
private fun LServiceErrorStatusDia() {
    val lServiceBridgeStatus by LServiceBridgeClient.statusFlow.collectAsStateWithLifecycle()
    val error = lServiceBridgeStatus as? LServiceBridgeClient.Status.Error
    var iSee by remember { mutableStateOf(false) }
    val visible = error != null && !iSee
    AnimatedVisibility(visible = visible) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                Button(onClick = {
                    iSee = true
                }) { Text(text = stringResource(id = R.string.ok)) }
            },
            title = { Text(text = stringResource(id = R.string.lservice_error_title)) },
            text = {
                Column {
                    Text(text = stringResource(id = error!!.messageResId))
                    Text(text = stringResource(id = R.string.lservice_description))
                }
            },
            shape = MaterialTheme.shapes.medium,
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    }

    LaunchedEffect(error) {
        if (error != null && iSee) {
            iSee = false
        }
    }
}