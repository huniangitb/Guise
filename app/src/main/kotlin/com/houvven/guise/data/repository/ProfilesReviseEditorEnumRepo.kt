package com.houvven.guise.data.repository

import android.icu.text.Collator
import com.houvven.guise.data.domain.ProfileSuggest
import java.util.Locale


object ProfilesReviseEditorEnumRepo {


    val characteristics = ProfileSuggest.create(
        "tablet" to "tablet",
        "nosdcard" to "nosdcard",
        "default" to "default"
    )

    val language = Locale.getAvailableLocales().map {
        ProfileSuggest(it.displayName, it.toString())
    }.sortedWith { o1, o2 ->
        Collator.getInstance().compare(o1.label, o2.label)
    }

    val boolean: List<ProfileSuggest<Boolean>> = ProfileSuggest.create(
        "true" to true,
        "false" to false
    )
}