package com.houvven.guise.data.repository.profile

import android.content.Context
import android.icu.text.Collator
import com.houvven.guise.R
import com.houvven.guise.data.domain.ProfileSuggest
import java.util.Locale

/**
 * This class represents a repository for storing and retrieving options for revising profiles.
 * It uses the provided context to generate lists of ProfileSuggest objects for various options.
 * @param context The context to use for generating the options.
 */
class ProfilesSuggestRepo_Enum(context: Context) {

    /**
     * A list of ProfileSuggest objects representing device characteristics options.
     */
    val characteristics = ProfileSuggest.create(
        "tablet" to "tablet",
        "nosdcard" to "nosdcard",
        "default" to "default"
    )

    /**
     * A list of ProfileSuggest objects representing language options.
     * The options are sorted by their display name.
     */
    val language = Locale.getAvailableLocales().map {
        ProfileSuggest(it.displayName, it.toString())
    }.sortedWith { o1, o2 ->
        Collator.getInstance().compare(o1.label, o2.label)
    }

    /**
     * A list of ProfileSuggest objects representing boolean options.
     * The options are generated from the context's resources.
     */
    val boolean: List<ProfileSuggest<Boolean>> = ProfileSuggest.create(
        context,
        R.string.turn_on to true,
        R.string.turn_off to false
    )
}