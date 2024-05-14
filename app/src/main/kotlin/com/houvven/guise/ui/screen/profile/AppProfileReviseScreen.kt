package com.houvven.guise.ui.screen.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ClearAll
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.houvven.guise.ui.compontent.ArrowBackButton
import com.houvven.guise.ui.screen.profile.components.ProfileRevise
import com.houvven.guise.ui.screen.profile.components.ProfileReviseState
import com.houvven.guise.util.app.App

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppProfileReviseScreen(
    state: AppProfileReviseState,
    actions: AppProfileReviseActions,
    app: App,
    reviseState: ProfileReviseState
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppProfileReviseTopBar(
                app = app,
                reviseState = reviseState,
                actions = actions,
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        ProfileRevise(
            app = app,
            state = reviseState,
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppProfileReviseTopBar(
    app: App,
    actions: AppProfileReviseActions,
    reviseState: ProfileReviseState,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    val profiles by reviseState.profilesState

    val dynamicActions = @Composable {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = actions.onClearAll) {
                Icon(Icons.Outlined.ClearAll, contentDescription = null)
            }
        }
    }


    MediumTopAppBar(
        title = { Text(text = app.name) },
        navigationIcon = { ArrowBackButton() },
        actions = {
            AnimatedVisibility(visible = profiles.isEffective) { dynamicActions.invoke() }
            IconButton(onClick = actions.onSave) {
                Icon(Icons.Outlined.Save, contentDescription = null)
            }
        },
        scrollBehavior = scrollBehavior
    )
}