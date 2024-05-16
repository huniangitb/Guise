package com.houvven.guise.ui.screen.profile.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.houvven.guise.R
import com.houvven.guise.data.domain.ProfileSuggest
import com.houvven.guise.data.repository.ProfilesPlaceholderRepo
import com.houvven.guise.data.repository.ProfilesReviseEditorEnumRepo
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
        abstract val onValueClear: Profiles.() -> Profiles

        open val display: (T?) -> String = { it.toString() }
        open val validator: (T?) -> Boolean = { true }

        val placeholder get() = ProfilesPlaceholderRepo.get(value).run(display)

        fun isEdited(profiles: Profiles) = value.invoke(profiles) != null
    }

    class Text(
        override val label: @Composable () -> String,
        override val value: Profiles.() -> String?,
        val onValueChange: Profiles.(String?) -> Profiles,
        override val onValueClear: Profiles.() -> Profiles = { onValueChange(null) }
    ) : Editor<String>()

    class TextNumber<T : Number>(
        override val label: @Composable () -> String,
        override val value: Profiles.() -> T?,
        val onValueChange: Profiles.(T?) -> Profiles,
        override val onValueClear: Profiles.() -> Profiles = { onValueChange(null) },
        val stringToNumber: (String) -> T?,
    ) : Editor<T>()

    class Enum<T>(
        override val label: @Composable () -> String,
        override val value: Profiles.() -> T?,
        override val onValueClear: Profiles.() -> Profiles,
        val suggests: List<ProfileSuggest<T>>,
        val onSelectedChange: Profiles.(ProfileSuggest<T>) -> Profiles,
    ) : Editor<T>()
}


val ProfileReviseDataList = listOf(
    ProfileReviseHeader { stringResource(id = R.string.system_properties) },
    // Brand
    ProfileReviseEditor.Text(
        label = { stringResource(id = R.string.brand) },
        value = { properties.brand },
        onValueChange = { properties { copy(brand = it) } }
    ),
    ProfileReviseEditor.Text(
        label = { stringResource(id = R.string.manufacturer) },
        value = { properties.manufacturer },
        onValueChange = { properties { copy(manufacturer = it) } },
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
    ProfileReviseEditor.Text(
        label = { stringResource(id = R.string.fingerprint) },
        value = { properties.fingerprint },
        onValueChange = { properties { copy(fingerprint = it) } }
    ),
    // Characteristic
    ProfileReviseEditor.Enum(
        label = { stringResource(id = R.string.characteristic) },
        value = { properties.characteristics },
        suggests = ProfilesReviseEditorEnumRepo.characteristics,
        onValueClear = { properties { copy(characteristics = null) } },
        onSelectedChange = { properties { copy(characteristics = it.value) } },
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
)


private fun Profiles.properties(function: PropertiesProfile.() -> PropertiesProfile) =
    copy(properties = properties.function())

private fun Profiles.appInfo(function: AppInfoProfile.() -> AppInfoProfile) =
    copy(appInfo = appInfo.function())