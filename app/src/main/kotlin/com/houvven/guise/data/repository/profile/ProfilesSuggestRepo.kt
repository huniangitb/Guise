package com.houvven.guise.data.repository.profile

import com.houvven.guise.data.domain.ProfileSuggest

sealed interface ProfilesSuggestRepo {

    interface Local : ProfilesSuggestRepo

    interface Remote : ProfilesSuggestRepo

    interface Random<out T> : ProfilesSuggestRepo {

        fun generate(size: Int = 10): List<ProfileSuggest<out T>> {
            if (size <= 0) return emptyList()
            val list = mutableListOf<ProfileSuggest<out T>>()
            repeat(size) { list.add(generate()) }
            return list
        }

        fun generate(): ProfileSuggest<out T>
    }
}