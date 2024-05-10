package com.houvven.guise.ui.screen.profile.components

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.twotone.Clear
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.highcapable.betterandroid.system.extension.tool.SystemProperties
import com.houvven.guise.R
import com.houvven.guise.hook.profile.ModuleHookProfiles
import com.houvven.guise.hook.profile.item.PropertiesProfile
import com.houvven.guise.util.update
import kotlinx.parcelize.Parcelize


/**
 * Edit the hook configuration
 */
@Composable
fun ProfileRevise(
    state: ProfileReviseState,
    modifier: Modifier = Modifier,
    dataList: List<ProfileReviseDataDef> = ProfileReviseDataList,
    columns: Int = 2,
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(columns),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = dataList,
            key = { data -> data.hashCode() },
            span = { reviseDataDef ->
                when (reviseDataDef) {
                    is ProfileReviseDataDef.Header -> columns
                    is ProfileReviseDataDef.Item<*>,
                    ProfileReviseDataDef.PropertiesCustom -> 1
                }.let {
                    GridItemSpan(it)
                }
            }
        ) { data ->
            when (data) {
                is ProfileReviseDataDef.Header -> ProfileReviseHeader(data)
                is ProfileReviseDataDef.Item<*> -> ProfileReviseItem(state, data)
                ProfileReviseDataDef.PropertiesCustom -> {}
            }
        }
    }
}


@Composable
private fun ProfileReviseHeader(data: ProfileReviseDataDef.Header) {
    with(data) {
        Text(
            text = title(),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(start = 8.dp, top = 10.dp)
        )
    }
}


@Composable
private fun <T> ProfileReviseItem(
    state: ProfileReviseState,
    data: ProfileReviseDataDef.Item<T>,
    colors: ProfileReviseItemColors = ProfileReviseDefaults.defaultProfileReviseItemColors(),
) {

    val profilesState by state.profiles
    val edited = data.isEdited(profilesState)
    val badge: @Composable BoxScope.() -> Unit = {
        // Clear Badge
        // Show badge if this profile item is not default value
        AnimatedVisibility(
            visible = edited,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            FilledIconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .border(2.dp, Color.White, CircleShape)
                    .size(26.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
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

    BadgedBox(badge = badge) {
        val cardColors = colors.run {
            CardDefaults.cardColors(
                contentColor = contentColor(edited),
                containerColor = containerColor(edited)
            )
        }
        ElevatedCard(
            onClick = {

            },
            colors = cardColors,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Text(
                    text = data.label(),
                    style = MaterialTheme.typography.titleMedium.run {
                        copy(color = color.copy(alpha = .9f))
                    }
                )
                val value: String
                val color: Color
                data.value(profilesState)?.toString().let {
                    if (it.isNullOrBlank()) {
                        value = data.placeholder.toString()
                        color = Color.Unspecified.copy(alpha = 0.4f)
                    } else {
                        value = it
                        color = Color.Unspecified
                    }
                }

                Text(text = value, style = MaterialTheme.typography.bodyLarge, color = color)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> ProfileReviseInputSheet(
    data: ProfileReviseDataDef.Item<T>,
    state: ProfileReviseState,
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(),
    containerColor: Color = BottomSheetDefaults.ContainerColor,
    contentColor: Color = contentColorFor(containerColor)
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = containerColor,
        contentColor = contentColor,
    ) {

        val profilesState by state.profiles
        val currentModificationType by state.currentModificationType
    }
}


@Composable
fun ProfileReviseType.TEXT.InputSheetContent(
    state: ProfileReviseState,
    data: ProfileReviseDataDef.Item<String>
) {
    val profilesState by state.profiles
    var stagingValue by remember { mutableStateOf(data.value(profilesState) ?: "") }

    val onStagingValueChange: (String?) -> Unit = {
        stagingValue = when {
            it.isNullOrBlank() -> ""
            data.validator(it) -> it
            else -> it
        }
    }
    val onClear: () -> Unit = { stagingValue = "" }

    Column {
        OutlinedTextField(
            value = stagingValue,
            onValueChange = onStagingValueChange,
            trailingIcon = {
                IconButton(onClick = onClear) {
                    Icon(Icons.Filled.Clear, contentDescription = null)
                }
            }
        )
    }
}

sealed interface ProfileReviseType : Parcelable {
    @Parcelize
    data object TEXT : ProfileReviseType

    @Parcelize
    data object NUMBER : ProfileReviseType

    @Parcelize
    data object BOOLEAN : ProfileReviseType

    @Parcelize
    data object ENUM : ProfileReviseType
}


@Composable
fun rememberProfileReviseState(profiles: ModuleHookProfiles) =
    rememberSaveable(saver = ProfileReviseDefaults.Saver) { ProfileReviseState(profiles) }

@Stable
class ProfileReviseState {

    val profiles: MutableState<ModuleHookProfiles>
    val currentModificationType: MutableState<ProfileReviseType?>

    constructor(profiles: ModuleHookProfiles, currentModificationType: ProfileReviseType? = null) {
        this.profiles = mutableStateOf(profiles)
        this.currentModificationType = mutableStateOf(currentModificationType)
    }

    constructor(
        profiles: MutableState<ModuleHookProfiles>,
        currentModificationType: MutableState<ProfileReviseType?>
    ) {
        this.profiles = profiles
        this.currentModificationType = currentModificationType
    }

    fun updateProfiles(function: (ModuleHookProfiles) -> ModuleHookProfiles) {
        profiles.update(function)
    }

    fun updateProfilesProperties(function: (PropertiesProfile) -> PropertiesProfile) {
        updateProfiles { it.copy(properties = function(it.properties)) }
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

    val Saver = object : Saver<ProfileReviseState, Bundle> {
        val KEY_PROFILES = "profiles"
        val KEY_CURRENT_MODIFICATION_TYPE = "currentModificationType"

        override fun restore(value: Bundle): ProfileReviseState {
            val profiles: ModuleHookProfiles =
                value.getParcelable(KEY_PROFILES) ?: ModuleHookProfiles.Empty
            val currentModificationType: ProfileReviseType? =
                value.getParcelable(KEY_CURRENT_MODIFICATION_TYPE)
            return ProfileReviseState(profiles, currentModificationType)
        }

        override fun SaverScope.save(value: ProfileReviseState): Bundle {
            val bundle = Bundle()
            // Save the primitive data types from ProfileReviseState
            bundle.putParcelable(KEY_PROFILES, value.profiles.value)
            bundle.putParcelable(KEY_CURRENT_MODIFICATION_TYPE, value.currentModificationType.value)
            return bundle
        }
    }
}

@Immutable
data class ProfileReviseItemColors(
    private val enabledContentColor: Color,
    private val enabledContainerColor: Color,
    private val disabledContentColor: Color,
    private val disabledContainerColor: Color
) {

    fun contentColor(enabled: Boolean) =
        if (enabled) enabledContentColor else disabledContentColor

    fun containerColor(enabled: Boolean) =
        if (enabled) enabledContainerColor else disabledContainerColor
}


sealed interface ProfileReviseDataDef {

    data class Header(
        val title: @Composable () -> String
    ) : ProfileReviseDataDef


    data class Item<T>(
        val label: @Composable () -> String,
        val value: ModuleHookProfiles.() -> T?,
        val onValueChange: ProfileReviseState.(T?) -> Unit,
        val type: ProfileReviseType = ProfileReviseType.TEXT,
        val placeholder: T? = null,
        val onClear: ProfileReviseState.() -> Unit = { onValueChange(null) },
        val isEdited: ModuleHookProfiles.() -> Boolean = { value() != null },
        val validator: (T?) -> Boolean = { true }
    ) : ProfileReviseDataDef

    data object PropertiesCustom : ProfileReviseDataDef
}

val ProfileReviseDataList = setOf(
    ProfileReviseDataDef.Header { stringResource(id = R.string.system_properties) },
    // Brand
    ProfileReviseDataDef.Item(
        label = { stringResource(id = R.string.brand) },
        value = { properties.brand },
        onValueChange = { value -> updateProfilesProperties { it.copy(brand = value) } },
        placeholder = Build.BRAND
    ),
    // Model
    ProfileReviseDataDef.Item(
        label = { stringResource(id = R.string.model) },
        value = { properties.brand },
        onValueChange = { value -> updateProfilesProperties { it.copy(model = value) } },
        placeholder = Build.MODEL
    ),
    // Device Code
    ProfileReviseDataDef.Item(
        label = { stringResource(id = R.string.device) },
        value = { properties.device },
        onValueChange = { value -> updateProfilesProperties { it.copy(device = value) } },
        placeholder = Build.DEVICE
    ),
    // Characteristic
    ProfileReviseDataDef.Item(
        label = { stringResource(id = R.string.characteristic) },
        value = { properties.characteristics },
        onValueChange = { value -> updateProfilesProperties { it.copy(characteristics = value) } },
        placeholder = SystemProperties.get("ro.build.characteristics")
    ),
    ProfileReviseDataDef.PropertiesCustom,

    ProfileReviseDataDef.Header { stringResource(id = R.string.app_info) },
    ProfileReviseDataDef.Item(
        label = { stringResource(id = R.string.version_name) },
        value = { "" },
        onValueChange = { },
    ),
).toList()