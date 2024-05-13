package com.houvven.guise.ui.screen.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.koin.androidx.compose.koinViewModel

/**
 * Screen's coordinator which is responsible for handling actions from the UI layer
 * and one-shot actions based on the new UI state
 */
class AppProfileReviseCoordinator(
    val viewModel: AppProfileReviseViewModel
) {
    val screenStateFlow = viewModel.stateFlow

    fun doStuff() {
        // TODO Handle UI Action
    }
}

@Composable
fun rememberAppProfileReviseCoordinator(
    viewModel: AppProfileReviseViewModel = koinViewModel()
): AppProfileReviseCoordinator {
    return remember(viewModel) {
        AppProfileReviseCoordinator(
            viewModel = viewModel
        )
    }
}