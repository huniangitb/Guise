package com.houvven.guise.ui.screen.profile.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.houvven.guise.data.domain.ProfileSuggest

@Composable
fun ProfileReviseEditor.Text.EditorContent(state: ProfileReviseState) {
    val profiles by state.profilesState
    var stagingVar by remember { mutableStateOf(value.invoke(profiles) ?: "") }
    val isEdited = stagingVar.isNotBlank()

    val onStagingValueChange: (String) -> Unit = {
        if (validator(it))
            stagingVar = it
    }

    val onDone = {
        state.update(profiles.onValueChange(stagingVar.takeUnless { it.isBlank() }))
        state.edit(ProfileReviseEditor.None)
    }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = stagingVar,
            onValueChange = onStagingValueChange,
            placeholder = { Text(text = placeholder) },
            modifier = Modifier.padding(bottom = 20.dp),
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
    }
}


@Composable
fun <T : Number> ProfileReviseEditor.TextNumber<T>.EditorContent(state: ProfileReviseState) {
    val profiles by state.profilesState
    var stagingVar by remember { mutableStateOf(value.invoke(profiles)?.toString() ?: "") }
    val isEdited = stagingVar.isNotBlank()

    // string to number
    val number = stringToNumber(stagingVar)
    val onStagingValueChange: (String) -> Unit = {
        if (it.isNotBlank() && validator(number)) {
            stagingVar = it
        }
    }
    val onDone = {
        state.update(profiles.onValueChange(number))
        state.edit(ProfileReviseEditor.None)
    }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = stagingVar,
            onValueChange = onStagingValueChange,
            placeholder = { Text(text = placeholder) },
            modifier = Modifier.padding(bottom = 20.dp),
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
fun <T> ProfileReviseEditor.Enum<T>.EditorContent(state: ProfileReviseState) {
    val profiles by state.profilesState
    val value = value.invoke(profiles)
    val onClick: (ProfileSuggest<T>) -> Unit = {
        state.updateAndDone(profiles.onSelectedChange(it))
    }

    suggests.forEach {
        val selected = value == it.value
        val containerColor =
            if (selected) MaterialTheme.colorScheme.primaryContainer
            else Color.Transparent
        Card(
            onClick = { onClick(it) },
            colors = CardDefaults.cardColors(containerColor = containerColor),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                RadioButton(selected = selected, onClick = { onClick(it) })
                Text(text = it.label)
            }
        }
    }
}