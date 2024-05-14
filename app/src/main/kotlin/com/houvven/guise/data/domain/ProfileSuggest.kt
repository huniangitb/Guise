package com.houvven.guise.data.domain

class ProfileSuggest<T>(
    val label: String,
    val value: T
) {

    companion object {
        fun <T> create(vararg profiles: Pair<String, T>): List<ProfileSuggest<T>> {
            return profiles.map {
                ProfileSuggest(it.first, it.second)
            }
        }
    }
}