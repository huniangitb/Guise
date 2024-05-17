package com.houvven.guise.data.domain

import android.content.Context

class ProfileSuggest<T>(
    val label: String = "",
    val value: T
) {

    companion object {
        fun <T> create(vararg profiles: Pair<String, T>): List<ProfileSuggest<T>> {
            return profiles.map {
                ProfileSuggest(it.first, it.second)
            }
        }

        fun <T> create(context: Context, vararg profiles: Pair<Int, T>): List<ProfileSuggest<T>> {
            return profiles.map {
                ProfileSuggest(context.getString(it.first), it.second)
            }
        }

        fun <T> create(profiles: Map<String, T>): List<ProfileSuggest<T>> {
            return profiles.map {
                ProfileSuggest(it.key, it.value)
            }
        }

        fun <T> create(context: Context, profiles: Map<Int, T>): List<ProfileSuggest<T>> {
            return profiles.map {
                ProfileSuggest(context.getString(it.key), it.value)
            }
        }
    }
}