package com.houvven.guise.ui.screen.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ClearAll
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.houvven.guise.R
import com.houvven.guise.ui.compontent.ArrowBackButton
import com.houvven.guise.ui.compontent.DangerousOperationsDialog
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


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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

    val menu = @Composable {
        var isExpanded by remember { mutableStateOf(false) }
        var isEjected by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier.wrapContentSize(Alignment.TopStart),
        ) {
            IconButton(onClick = { isExpanded = true }) {
                Icon(imageVector = Icons.Outlined.MoreVert, contentDescription = null)
            }
            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
                border = BorderStroke(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = .7f)
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                val itemColors = MenuDefaults.itemColors(
                    textColor = MaterialTheme.colorScheme.primary,
                    leadingIconColor = MaterialTheme.colorScheme.primary,
                    trailingIconColor = MaterialTheme.colorScheme.primary
                )
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = R.string.restart)) },
                    onClick = actions.onRestart,
                    colors = itemColors,
                )
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = R.string.stop)) },
                    onClick = actions.onStop,
                    colors = itemColors,
                )
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = R.string.clear_data)) },
                    onClick = { isEjected = true },
                    colors = itemColors
                )
                // DropdownMenuItem(
                //     text = { Text(text = stringResource(id = R.string.copy_to_clipboard)) },
                //     onClick = actions.onCopyToClipboard,
                //     colors = itemColors
                // )
                // DropdownMenuItem(
                //     text = { Text(text = stringResource(id = R.string.import_from_clipboard)) },
                //     onClick = { /*TODO*/ },
                //     colors = itemColors
                // )
            }
        }

        DangerousOperationsDialog(
            ejected = isEjected,
            onDismissRequest = { isEjected = false },
            onConfirmClick = {
                actions.onClearData.invoke()
                isEjected = false
            }
        )
    }

    MediumTopAppBar(
        title = { Text(text = app.name) },
        navigationIcon = { ArrowBackButton() },
        actions = {
            AnimatedVisibility(visible = profiles.isEffective) { dynamicActions.invoke() }
            IconButton(onClick = actions.onSave) {
                Icon(Icons.Outlined.Save, contentDescription = null)
            }
            menu.invoke()
        },
        scrollBehavior = scrollBehavior
    )
}