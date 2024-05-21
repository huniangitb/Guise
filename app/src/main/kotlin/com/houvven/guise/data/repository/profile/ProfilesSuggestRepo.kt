package com.houvven.guise.data.repository.profile

import com.houvven.guise.data.domain.ProfileSuggest

sealed interface ProfilesSuggestRepo {

    interface Local : ProfilesSuggestRepo

    interface Remote : ProfilesSuggestRepo

    interface Random<out T> : ProfilesSuggestRepo {

        fun generate(size: Int): Set<ProfileSuggest<out T>> {
            val profiles = mutableSetOf<ProfileSuggest<out T>>()
            for (i in 0 until size) {
                profiles.add(generate())
            }
            return profiles
        }

        fun generate(): ProfileSuggest<out T>
    }
}