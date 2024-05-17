package com.houvven.guise.ui.screen.profile.components

import android.os.Bundle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.BottomSheetDefaults
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
import com.highcapable.betterandroid.system.extension.component.getParcelableCompat
import com.houvven.guise.data.repository.ProfilesPlaceholderRepo
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
    dataList: List<ProfileReviseContract> = ProfileReviseDataList,
    columns: Int = 2,
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
                is ProfileReviseEditor.Editor<*> -> {
                    ProfileReviseEditorApron(state = state, editor = def)
                }

                else -> Unit
            }
        }

        item(
            key = "%76%69%70" /* Hex encode */,
            span = { GridItemSpan(columns) }
        ) {

        }
    }

    ProfileReviseEditorSheet(
        state = state,
        sheetState = sheetState
    )

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
    contentColor: Color = contentColorFor(containerColor)
) {
    val editor = state.editor

    if (editor != ProfileReviseEditor.None) {
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
                    is ProfileReviseEditor.Text -> editor.EditorContent(state = state)
                    is ProfileReviseEditor.TextNumber<*> -> editor.EditorContent(state = state)
                    is ProfileReviseEditor.Enum<*> -> editor.EditorContent(state = state)
                    else -> Unit
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
            val profiles = value.getParcelableCompat<Profiles>("") ?: Profiles.Empty
            return ProfileReviseState(profiles)
        }

        override fun SaverScope.save(value: ProfileReviseState): Bundle {
            val bundle = Bundle()
            // Save the primitive data types from ProfileReviseState
            bundle.putParcelable(KEY_PROFILES, value.profilesState.value)
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