package com.houvven.guise.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.houvven.guise.ui.theme.AppTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs


val LocalNavHostController: ProvidableCompositionLocal<NavHostController> =
    staticCompositionLocalOf { error("Not provided") }


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GuiseApp(
    navController: NavHostController = rememberNavController()
) {
    AppTheme {
        CompositionLocalProvider(
            LocalNavHostController provides navController
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize()
            ) {
                DestinationsNavHost(
                    navController = LocalNavHostController.current,
                    navGraph = NavGraphs.root
                )
            }
        }
    }
}