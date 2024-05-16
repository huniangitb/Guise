package com.houvven.guise.ui.screen.profile.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Clear
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun <T> ProfileReviseEditorApron(
    state: ProfileReviseState,
    editor: ProfileReviseEditor.Editor<T>,
    colors: ProfileReviseItemColors = ProfileReviseDefaults.defaultProfileReviseItemColors(),
) {
    val profiles by state.profilesState
    val isEdited = editor.isEdited(profiles)
    val coroutineScope = rememberCoroutineScope()

    Box {
        val cardColors = colors.run {
            CardDefaults.cardColors(
                contentColor = contentColor(isEdited),
                containerColor = containerColor(isEdited)
            )
        }
        ElevatedCard(
            onClick = {
                coroutineScope.launch { state.edit(editor) }
            },
            colors = cardColors,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Text(
                    text = editor.label(),
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.titleColor
                )
                val (value, color) = when (isEdited) {
                    true -> editor.run { display(value(profiles)) } to colors.enabledTextColor
                    else -> editor.run { placeholder } to colors.placeholderColor
                }
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    color = color,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        EditorApronClearBadge(visible = isEdited) { state.update(editor.onValueClear(profiles)) }
    }
}


@Composable
private fun BoxScope.EditorApronClearBadge(visible: Boolean, onClear: () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(2.dp)
    ) {
        FilledIconButton(
            onClick = onClear,
            modifier = Modifier
                .border(2.dp, Color.White, CircleShape)
                .size(26.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = ProfileReviseDefaults.defaultBadgeContainerColor
            )
        ) {
            Icon(
                Icons.TwoTone.Clear,
                contentDescription = null,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}