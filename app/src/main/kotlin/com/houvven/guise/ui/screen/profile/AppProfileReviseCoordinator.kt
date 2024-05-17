package com.houvven.guise.ui.screen.profile

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.highcapable.betterandroid.system.extension.component.clipboardManager
import com.highcapable.betterandroid.system.extension.component.copy
import com.houvven.guise.R
import com.houvven.guise.hook.profile.ModuleHookProfiles
import com.houvven.guise.hook.store.ModuleStore
import com.houvven.guise.ui.screen.profile.components.ProfileReviseState
import com.houvven.guise.util.app.App
import com.houvven.guise.util.app.AppUtils
import com.houvven.guise.util.showToast
import org.koin.androidx.compose.koinViewModel

/**
 * Screen's coordinator which is responsible for handling actions from the UI layer
 * and one-shot actions based on the new UI state
 */
class AppProfileReviseCoordinator(
    val viewModel: AppProfileReviseViewModel,
    val reviseState: ProfileReviseState,
    val moduleStore: ModuleStore.Hooker,
    val app: App,
    val context: Context
) {
    val screenStateFlow = viewModel.stateFlow

    fun onSave() {
        runCatching {
            moduleStore.set(reviseState.profilesState.value)
        }.onSuccess {
            context.showToast(R.string.success)
        }
    }

    fun onClearAll() {
        reviseState.update(ModuleHookProfiles.Empty.copy(packageName = app.packageName))
    }

    fun onRestart() {
        runCatching {
            require(AppUtils.cmdStopApp(app.packageName))
            AppUtils.launchApp(context, app.packageName)
        }.onFailure {
            context.showToast(R.string.required_permissions_root)
        }
    }

    fun onStop() {
        if (AppUtils.cmdStopApp(app.packageName)) {
            context.showToast(R.string.success)
            return
        }
        AppUtils.toDetails(context, app.packageName)
    }

    fun onClearData() {
        val result = AppUtils.cmdClearData(app.packageName)
        val id = if (result) R.string.success else R.string.required_permissions_root
        context.showToast(id)
    }

    fun onCopyToClipboard() {
        val profiles by reviseState.profilesState
        val clipboardManager = context.clipboardManager
        clipboardManager.copy(profiles.toJsonStr())
        context.showToast(R.string.success)
    }
}

@Composable
fun rememberAppProfileReviseCoordinator(
    app: App,
    reviseState: ProfileReviseState,
    moduleStore: ModuleStore.Hooker,
    context: Context = LocalContext.current,
    viewModel: AppProfileReviseViewModel = koinViewModel()
): AppProfileReviseCoordinator {
    return remember(viewModel) {
        AppProfileReviseCoordinator(
            viewModel = viewModel,
            reviseState = reviseState,
            moduleStore = moduleStore,
            app = app,
            context = context
        )
    }
}