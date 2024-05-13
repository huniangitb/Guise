package com.houvven.guise.ui.screen.profile

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.houvven.guise.hook.store.ModuleStore
import com.houvven.guise.ui.compontent.ArrowBackButton
import com.houvven.guise.ui.screen.profile.components.ProfileRevise
import com.houvven.guise.ui.screen.profile.components.rememberProfileReviseState
import com.houvven.guise.util.app.App
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppProfileReviseScreen(
    state: AppProfileReviseState,
    actions: AppProfileReviseActions,
    app: App,
    moduleStore: ModuleStore.Hooker = koinInject()
) {

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val profiles = moduleStore.get(app.packageName)
    val profileReviseState = rememberProfileReviseState(profiles = profiles)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { AppProfileReviseTopBar(app = app) }
    ) { innerPadding ->
        ProfileRevise(
            app = app,
            state = profileReviseState,
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppProfileReviseTopBar(
    app: App,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    MediumTopAppBar(
        title = { Text(text = app.name) },
        navigationIcon = { ArrowBackButton() },
        actions = {

        },
        scrollBehavior = scrollBehavior
    )
}