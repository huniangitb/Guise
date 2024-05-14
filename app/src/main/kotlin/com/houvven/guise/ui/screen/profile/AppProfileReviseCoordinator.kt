package com.houvven.guise.ui.screen.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.houvven.guise.hook.profile.ModuleHookProfiles
import com.houvven.guise.hook.store.ModuleStore
import com.houvven.guise.ui.screen.profile.components.ProfileReviseState
import com.houvven.guise.util.app.App
import org.koin.androidx.compose.koinViewModel

/**
 * Screen's coordinator which is responsible for handling actions from the UI layer
 * and one-shot actions based on the new UI state
 */
class AppProfileReviseCoordinator(
    val viewModel: AppProfileReviseViewModel,
    val reviseState: ProfileReviseState,
    val moduleStore: ModuleStore.Hooker,
    val app: App
) {
    val screenStateFlow = viewModel.stateFlow

    fun onSave() {
        moduleStore.set(reviseState.profilesState.value)
    }

    fun onClearAll() {
        reviseState.update(ModuleHookProfiles.Empty.copy(packageName = app.packageName))
    }
}

@Composable
fun rememberAppProfileReviseCoordinator(
    reviseState: ProfileReviseState,
    moduleStore: ModuleStore.Hooker,
    app: App,
    viewModel: AppProfileReviseViewModel = koinViewModel()
): AppProfileReviseCoordinator {
    return remember(viewModel) {
        AppProfileReviseCoordinator(
            viewModel = viewModel,
            reviseState = reviseState,
            moduleStore = moduleStore,
            app = app
        )
    }
}