package com.houvven.guise.ui.screen.launch.home.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import com.houvven.guise.R
import com.houvven.guise.ui.screen.launch.home.HomeActions
import com.houvven.guise.ui.screen.launch.home.HomeState
import com.houvven.guise.ui.style.OutlinedTextFieldTransparentBorderColor

@Composable
fun HomeScreenTopBar(
    state: HomeState,
    actions: HomeActions
) {
    var isSearching by remember { mutableStateOf(false) }
    val onSearchChange: (Boolean) -> Unit = { isSearching = it }

    when (isSearching) {
        false -> HomeScreenTopAppBar(onSearchChange)
        true -> HomeScreenSearchAppBar(state, actions, onSearchChange)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenTopAppBar(
    onSearchChange: (Boolean) -> Unit
) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
        actions = {
            IconButton(onClick = { onSearchChange.invoke(true) }) {
                Icon(Icons.Filled.Search, contentDescription = null)
            }
        }
    )
}


@Composable
private fun HomeScreenSearchAppBar(
    state: HomeState,
    actions: HomeActions,
    onSearchChange: (Boolean) -> Unit
) = Column {
    val placeholder = stringResource(id = R.string.search_hint)
    val focusRequester = remember { FocusRequester() }

    OutlinedTextField(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .focusRequester(focusRequester),
        value = state.appQuery,
        onValueChange = actions.onAppQueryChange,
        placeholder = { Text(text = placeholder) },
        colors = OutlinedTextFieldTransparentBorderColor,
        textStyle = MaterialTheme.typography.bodyLarge,
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            TextButton(onClick = { onSearchChange.invoke(false) }) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        keyboardOptions = KeyboardOptions(
            showKeyboardOnFocus = true
        )
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.5f))

    BackHandler {
        onSearchChange.invoke(false)
    }

    DisposableEffect(key1 = "HomeAppSearchBarDisposable") {
        onDispose {
            actions.onAppQueryChange("")
        }
    }
}