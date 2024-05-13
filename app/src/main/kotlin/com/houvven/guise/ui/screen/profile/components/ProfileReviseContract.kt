package com.houvven.guise.ui.screen.profile.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.houvven.guise.R
import com.houvven.guise.data.repository.ProfilesPlaceholderRepository
import com.houvven.guise.hook.profile.ModuleHookProfiles
import com.houvven.guise.hook.profile.item.AppInfoProfile
import com.houvven.guise.hook.profile.item.PropertiesProfile

typealias Profiles = ModuleHookProfiles

sealed class ProfileReviseContract {
    open val span = ProfileReviseColumSpan.DEFAULT
}

enum class ProfileReviseColumSpan {
    FULL,
    DEFAULT
}

class ProfileReviseHeader(
    val title: @Composable () -> String
) : ProfileReviseContract() {
    override val span = ProfileReviseColumSpan.FULL
}

sealed class ProfileReviseEditor : ProfileReviseContract() {

    data object None : ProfileReviseEditor()

    sealed class Editor<T> : ProfileReviseEditor() {
        abstract val label: @Composable () -> String
        abstract val value: Profiles.() -> T?
        abstract val onValueChange: Profiles.(T?) -> Profiles

        open val display: (T?) -> String = { it.toString() }
        open val onValueClear: Profiles.() -> Profiles = { onValueChange(null) }
        open val validator: (T?) -> Boolean = { true }

        val placeholder get() = ProfilesPlaceholderRepository.get(value).run(display)

        fun isEdited(profiles: Profiles) = value.invoke(profiles) != null
    }

    class Text(
        override val label: @Composable () -> String,
        override val value: Profiles.() -> String?,
        override val onValueChange: Profiles.(String?) -> Profiles
    ) : Editor<String>()

    class TextNumber<T : Number>(
        override val label: @Composable () -> String,
        override val value: Profiles.() -> T?,
        override val onValueChange: Profiles.(T?) -> Profiles,
        val stringToNumber: (String) -> T?,
    ) : Editor<T>()
}


val ProfileReviseDataList = setOf(
    ProfileReviseHeader { stringResource(id = R.string.system_properties) },
    // Brand
    ProfileReviseEditor.Text(
        label = { stringResource(id = R.string.brand) },
        value = { properties.brand },
        onValueChange = { properties { copy(brand = it) } }
    ),
    // Model
    ProfileReviseEditor.Text(
        label = { stringResource(id = R.string.model) },
        value = { properties.model },
        onValueChange = { properties { copy(model = it) } },
    ),
    // Device Code
    ProfileReviseEditor.Text(
        label = { stringResource(id = R.string.device) },
        value = { properties.device },
        onValueChange = { properties { copy(device = it) } },
    ),
    // Characteristic
    ProfileReviseEditor.Text(
        label = { stringResource(id = R.string.characteristic) },
        value = { properties.characteristics },
        onValueChange = { properties { copy(characteristics = it) } },
    ),

    // App Info
    ProfileReviseHeader { stringResource(id = R.string.app_info) },
    // App Version Name
    ProfileReviseEditor.Text(
        label = { stringResource(id = R.string.version_name) },
        value = { appInfo.versionName },
        onValueChange = { appInfo { copy(versionName = it) } },
    ),
    // App Version Code
    ProfileReviseEditor.TextNumber(
        label = { stringResource(id = R.string.version_code) },
        value = { appInfo.versionCode },
        onValueChange = { appInfo { copy(versionCode = it) } },
        stringToNumber = { it.toIntOrNull() }
    )
).toList()


private fun Profiles.properties(function: PropertiesProfile.() -> PropertiesProfile) =
    copy(properties = properties.function())

private fun Profiles.appInfo(function: AppInfoProfile.() -> AppInfoProfile) =
    copy(appInfo = appInfo.function())