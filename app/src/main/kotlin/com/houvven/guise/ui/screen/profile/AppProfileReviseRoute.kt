package com.houvven.guise.ui.screen.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.houvven.guise.util.app.AppScanner
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import org.koin.compose.koinInject

@Destination<RootGraph>
@Composable
fun AppProfileReviseRoute(
    packageName: String,
    coordinator: AppProfileReviseCoordinator = rememberAppProfileReviseCoordinator(),
    appScanner: AppScanner = koinInject()
) {
    // State observing and declarations
    val uiState by coordinator.screenStateFlow.collectAsStateWithLifecycle(AppProfileReviseState())

    // UI Actions
    val actions = rememberAppProfileReviseActions(coordinator)

    val app = appScanner.getAppAsUser(packageName)

    // UI Rendering
    if (app != null) {
        AppProfileReviseScreen(uiState, actions, app)
    }
}


@Composable
fun rememberAppProfileReviseActions(coordinator: AppProfileReviseCoordinator): AppProfileReviseActions {
    return remember(coordinator) {
        AppProfileReviseActions(
            onClick = coordinator::doStuff
        )
    }
}