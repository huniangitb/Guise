package com.houvven.guise.ui.screen.profile.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import com.houvven.guise.data.repository.profile.ProfilesSuggestRepo
import com.houvven.guise.ui.style.OutlinedTextFieldTransparentBorderColor
import org.koin.compose.koinInject


@Composable
private fun BasicTextReviseEditor(
    value: String?,
    onDone: (String?) -> Unit,
    label: String,
    placeholder: String,
    suggestRepo: ProfilesSuggestRepo?,
    validator: (String) -> Boolean = { true },
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val focusRequester = remember { FocusRequester() }
    var staging by remember { mutableStateOf(value) }
    val onStagingValueChange: (String) -> Unit = {
        when {
            it.isBlank() -> staging = null
            validator(it) -> staging = it
        }
    }
    val trailingIcon = @Composable {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(visible = !staging.isNullOrBlank()) {
                IconButton(onClick = { staging = null }) {
                    Icon(Icons.Filled.Clear, contentDescription = null)
                }
            }
        }
    }

    ListItem(
        headlineContent = { Text(text = label, style = MaterialTheme.typography.titleLarge) },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        trailingContent = {
            AnimatedVisibility(visible = staging != value) {
                Icon(
                    Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.clickable { onDone.invoke(staging) }
                )
            }
        }
    )

    TextField(
        value = staging ?: "",
        onValueChange = onStagingValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        placeholder = { Text(text = placeholder) },
        trailingIcon = trailingIcon,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = keyboardType
        ),
        keyboardActions = KeyboardActions(
            onDone = { onDone.invoke(staging) },
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

    /**
     * Random Suggestions Repo Content
     */
    @Composable
    fun ProfilesSuggestRepo.Random<*>.Content() {
        val getter = { generate(6).toList() }
        var suggests by remember {
            mutableStateOf(getter.invoke())
        }

        Box {
            LazyColumn(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surface),
            ) {
                items(suggests) {
                    ListItem(
                        headlineContent = {
                            Text(
                                text = it.label,
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        modifier = Modifier.clickable { onStagingValueChange.invoke(it.value.toString()) }
                    )
                }
            }
            FilledIconButton(
                onClick = { suggests = getter.invoke() },
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Icon(Icons.Rounded.Refresh, contentDescription = null)
            }
        }
    }

    when (suggestRepo) {
        is ProfilesSuggestRepo.Random<*> -> suggestRepo.Content()
        else -> Unit
    }
}

@Composable
fun ProfileReviseEditor.Text.EditorContent(
    state: ProfileReviseState
) {
    val profiles by state.profilesState
    BasicTextReviseEditor(
        value = value.invoke(profiles),
        onDone = { state.updateAndDone(onValueChange.invoke(profiles, it)) },
        label = label.invoke(),
        placeholder = placeholder,
        suggestRepo = suggestRepo,
        validator = validator
    )
}


@Composable
fun <T : Number> ProfileReviseEditor.TextNumber<T>.EditorContent(state: ProfileReviseState) {
    val profiles by state.profilesState
    BasicTextReviseEditor(
        value = value.invoke(profiles)?.toString(),
        onDone = { state.updateAndDone(onValueChange.invoke(profiles, stringToNumber(it ?: ""))) },
        label = label.invoke(),
        placeholder = placeholder,
        suggestRepo = null,
        validator = { validator.invoke(stringToNumber(it)) },
        keyboardType = KeyboardType.Number
    )
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