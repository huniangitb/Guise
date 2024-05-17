package com.houvven.guise.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
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
            }
        }
    }
}