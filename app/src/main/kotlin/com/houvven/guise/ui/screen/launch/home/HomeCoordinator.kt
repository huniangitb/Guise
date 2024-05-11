package com.houvven.guise.ui.screen.launch.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import com.houvven.guise.ui.LocalNavHostController
import com.houvven.guise.util.app.App
import com.houvven.guise.util.navigateDirection
import com.ramcosta.composedestinations.generated.destinations.AppProfileReviseRouteDestination
import org.koin.androidx.compose.koinViewModel

/**
 * Screen's coordinator which is responsible for handling actions from the UI layer
 * and one-shot actions based on the new UI state
 */
class HomeCoordinator(
    val viewModel: HomeViewModel,
    val navController: NavHostController
) {
    val screenStateFlow = viewModel.stateFlow

    fun onAppLick(app: App) {
        navController.navigateDirection(AppProfileReviseRouteDestination(app.packageName))
    }
}

@Composable
fun rememberHomeCoordinator(
    viewModel: HomeViewModel = koinViewModel(),
    navHostController: NavHostController = LocalNavHostController.current
): HomeCoordinator {
    return remember(viewModel) {
        HomeCoordinator(
            viewModel = viewModel,
            navController = navHostController
        )
    }
}