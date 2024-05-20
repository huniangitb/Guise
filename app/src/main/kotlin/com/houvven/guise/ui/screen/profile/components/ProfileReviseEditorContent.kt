package com.houvven.guise.ui.screen.profile.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.houvven.guise.R
import com.houvven.guise.data.domain.ProfileSuggest
import com.houvven.guise.ui.style.OutlinedTextFieldTransparentBorderColor
import org.koin.compose.koinInject

@Composable
fun ProfileReviseEditor.Text.EditorContent(state: ProfileReviseState) {
    val profiles by state.profilesState
    val originValue = value.invoke(profiles) ?: ""
    var stagingVar by remember { mutableStateOf(originValue) }
    val isEdited = stagingVar.isNotBlank()
    val focusRequester = remember { FocusRequester() }

    val onStagingValueChange: (String) -> Unit = {
        if (validator(it)) stagingVar = it
    }

    val onDone = {
        state.update(profiles.onValueChange(stagingVar.takeUnless { it.isBlank() }))
        state.edit(ProfileReviseEditor.None)
    }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnimatedVisibility(
            visible = stagingVar != originValue,
            modifier = Modifier.align(Alignment.End)
        ) {
            TextButton(onClick = onDone) {
                Text(text = stringResource(id = androidx.appcompat.R.string.abc_action_mode_done))
            }
        }
        TextField(
            value = stagingVar,
            onValueChange = onStagingValueChange,
            placeholder = { Text(text = placeholder) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            trailingIcon = {
                AnimatedVisibility(visible = isEdited) {
                    IconButton(onClick = { stagingVar = "" }) {
                        Icon(Icons.Filled.Clear, contentDescription = null)
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                showKeyboardOnFocus = true,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onDone() }
            ),
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                focusedSupportingTextColor = Color.Transparent
            )
        )

        SideEffect {
            focusRequester.requestFocus()
        }
    }
}


@Composable
fun <T : Number> ProfileReviseEditor.TextNumber<T>.EditorContent(state: ProfileReviseState) {
    val profiles by state.profilesState
    val originValue = value.invoke(profiles)?.toString() ?: ""
    var stagingVar by remember { mutableStateOf(originValue) }
    val isEdited = stagingVar.isNotBlank()
    val focusRequester = remember { FocusRequester() }

    // string to number
    val number = stringToNumber(stagingVar)
    val onStagingValueChange: (String) -> Unit = {
        if (it.isBlank() || validator(number)) {
            stagingVar = it
        }
    }
    val onDone = {
        state.update(profiles.onValueChange(number))
        state.edit(ProfileReviseEditor.None)
    }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = originValue != stagingVar,
            modifier = Modifier.align(Alignment.End)
        ) {
            TextButton(onClick = onDone) {
                Text(text = stringResource(id = androidx.appcompat.R.string.abc_action_mode_done))
            }
        }
        TextField(
            value = stagingVar,
            onValueChange = onStagingValueChange,
            placeholder = { Text(text = placeholder) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            trailingIcon = {
                AnimatedVisibility(visible = isEdited) {
                    IconButton(onClick = { stagingVar = "" }) {
                        Icon(Icons.Filled.Clear, contentDescription = null)
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                showKeyboardOnFocus = true,
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onDone() }
            ),
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                focusedSupportingTextColor = Color.Transparent
            )
        )
    }
}


@Composable
fun <T> ProfileReviseEditor.Enum<T>.EditorContent(
    state: ProfileReviseState,
) {
    val profiles by state.profilesState
    val options = options.invoke(koinInject())
    val value = value.invoke(profiles)
    val onClick: (ProfileSuggest<T>) -> Unit = {
        state.updateAndDone(profiles.onSelectedChange(it))
    }
    var query by remember { mutableStateOf("") }

    if (options.size > 10) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldTransparentBorderColor,
            placeholder = { Text(text = stringResource(id = R.string.search_hint)) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            }
        )
        HorizontalDivider()
    }

    LazyColumn(
        contentPadding = PaddingValues(vertical = 5.dp)
    ) {
        items(
            items = options.filter { it.label.contains(query.trim()) },
            contentType = { it.value },
            key = { it.value.toString() }
        ) {
            val selected = value == it.value
            val containerColor =
                if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent

            ListItem(
                headlineContent = { Text(text = it.label) },
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { onClick(it) }
                    .animateItem(),
                trailingContent = {
                    if (selected) Icon(
                        Icons.Default.Check,
                        contentDescription = null
                    )
                },
                colors = ListItemDefaults.colors(containerColor = containerColor),
            )
        }
    }
}