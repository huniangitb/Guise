package com.houvven.guise.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.houvven.guise.ui.theme.GuiseTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GuiseApp(
    navController: NavHostController = rememberNavController()
) {
    GuiseTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) {
            DestinationsNavHost(
                navController = navController,
                navGraph = NavGraphs.root
            )
        }
    }
}