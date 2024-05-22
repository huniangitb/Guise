package com.houvven.guise.ui.screen.profile.components

import android.os.Bundle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.houvven.guise.data.repository.profile.ProfilesPlaceholderRepo
import com.houvven.guise.ui.screen.profile.components.ProfileReviseEditor.Editor
import com.houvven.guise.util.app.App


/**
 * Edit the hook configuration
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileRevise(
    app: App,
    state: ProfileReviseState,
    modifier: Modifier = Modifier,
    dataList: List<ProfileReviseContract> = ProfilesReviseItemsDef,
    columns: Int = 2
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(columns),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = dataList,
            key = { def -> def.hashCode() },
            span = { def ->
                when (def.span) {
                    ProfileReviseColumSpan.FULL -> GridItemSpan(columns)
                    ProfileReviseColumSpan.DEFAULT -> GridItemSpan(1)
                }
            }
        ) { def ->
            when (def) {
                is ProfileReviseHeader -> ProfileReviseHeader(header = def)
                is Editor<*> -> ProfileReviseEditorApron(state = state, editor = def)

                else -> Unit
            }
        }
    }

    ProfileReviseEditorSheet(
        state = state,
        sheetState = sheetState
    )

    ProfileReviseEditorDialog(state = state)

    SideEffect {
        ProfilesPlaceholderRepo.update(app)
    }
}


@Composable
private fun ProfileReviseHeader(header: ProfileReviseHeader) {
    with(header) {
        Text(
            text = title(),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(start = 8.dp, top = 10.dp)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileReviseEditorSheet(
    state: ProfileReviseState,
    sheetState: SheetState = rememberModalBottomSheetState(),
    containerColor: Color = BottomSheetDefaults.ContainerColor,
    contentColor: Color = contentColorFor(containerColor),
) {
    val editor = state.editor
    val supported = listOf(
        ProfileReviseEditor.Enum::class.java,
        ProfileReviseEditor.BooleanEnum::class.java
    )

    if (editor::class.java in supported) {
        ModalBottomSheet(
            onDismissRequest = { state.edit(ProfileReviseEditor.None) },
            sheetState = sheetState,
            containerColor = containerColor,
            contentColor = contentColor,
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 15.dp)
            ) {
                when (editor) {
                    is ProfileReviseEditor.Enum<*> -> editor.EditorContent(state)
                    else -> Unit
                }
            }
        }
    }
}

@Composable
fun ProfileReviseEditorDialog(
    state: ProfileReviseState,
) {
    val editor = state.editor
    val supported = listOf(
        ProfileReviseEditor.Text::class.java,
        ProfileReviseEditor.TextNumber::class.java
    )
    if (editor::class.java in supported) {
        Dialog(
            onDismissRequest = { state.editNone() },
            properties = DialogProperties(dismissOnClickOutside = true)
        ) {
            Card(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.safeContent)
                    .fillMaxHeight(0.8f)
                    .wrapContentHeight(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.padding(16.dp)
                ) {
                    when (editor) {
                        is ProfileReviseEditor.Text -> editor.EditorContent(state)
                        is ProfileReviseEditor.TextNumber<*> -> editor.EditorContent(state)
                        else -> Unit
                    }
                }
            }
        }
    }
}


@Composable
fun rememberProfileReviseState(profiles: Profiles) =
    rememberSaveable(saver = ProfileReviseDefaults.Saver) { ProfileReviseState(profiles) }

@Stable
class ProfileReviseState(
    profiles: Profiles
) {
    val profilesState = mutableStateOf(profiles)

    var editor: ProfileReviseEditor by mutableStateOf(ProfileReviseEditor.None)
        private set

    fun edit(editor: ProfileReviseEditor) {
        this.editor = editor
    }

    fun editNone() {
        edit(ProfileReviseEditor.None)
    }

    fun update(profiles: Profiles) {
        profilesState.value = profiles
    }

    fun updateAndDone(profiles: Profiles) {
        update(profiles)
        edit(ProfileReviseEditor.None)
    }
}


object ProfileReviseDefaults {

    @Composable
    fun defaultProfileReviseItemColors(): ProfileReviseItemColors {
        return ProfileReviseItemColors(
            enabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
            enabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            disabledContentColor = MaterialTheme.colorScheme.onSurface
        )
    }

    val defaultBadgeContainerColor
        @Composable
        get() = MaterialTheme.colorScheme.primaryContainer

    val Saver = object : Saver<ProfileReviseState, Bundle> {
        val KEY_PROFILES = "profiles"

        override fun restore(value: Bundle): ProfileReviseState {
            val profiles = value.getString(KEY_PROFILES, "").let { Profiles.fromJsonStr(it) }
            return ProfileReviseState(profiles)
        }

        override fun SaverScope.save(value: ProfileReviseState): Bundle {
            val bundle = Bundle()
            // Save the primitive data types from ProfileReviseState
            val profiles by value.profilesState
            bundle.putString(KEY_PROFILES, profiles.toJsonStr())
            return bundle
        }
    }
}

@Immutable
data class ProfileReviseItemColors(
    private val enabledContentColor: Color,
    private val enabledContainerColor: Color,
    private val disabledContentColor: Color,
    private val disabledContainerColor: Color,
    val titleColor: Color = Color.Unspecified,
    val enabledTextColor: Color = Color.Unspecified,
    val placeholderColor: Color = enabledTextColor.copy(alpha = .4f)
) {

    fun contentColor(enabled: Boolean) =
        if (enabled) enabledContentColor else disabledContentColor

    fun containerColor(enabled: Boolean) =
        if (enabled) enabledContainerColor else disabledContainerColor
}