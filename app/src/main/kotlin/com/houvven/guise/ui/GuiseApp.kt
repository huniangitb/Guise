package com.houvven.guise.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.houvven.guise.ui.theme.GuiseTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.utils.currentDestinationAsState

@Composable
fun GuiseApp(
    navController: NavHostController = rememberNavController()
) {
    GuiseTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = { GuiseAppBottomBar(navController) }
        ) { innerPadding ->

            // if currentDestination in BottomBarDestination, set bottom innerPadding
            val modifier = Modifier
            navController.currentDestinationAsState().value.let {
                if (it == null || BottomBarDestination.contains(it)) {
                    modifier.padding(bottom = innerPadding.calculateBottomPadding())
                }
            }

            Column(
                modifier = modifier
            ) {
                DestinationsNavHost(
                    navController = navController,
                    navGraph = NavGraphs.root
                )
            }
        }
    }
}