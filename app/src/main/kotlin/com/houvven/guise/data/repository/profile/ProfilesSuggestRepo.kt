package com.houvven.guise.data.repository.profile

import com.houvven.guise.data.domain.ProfileSuggest

sealed interface ProfilesSuggestRepo {

    interface Local<T> : ProfilesSuggestRepo {
        fun get(): List<ProfileSuggest<out T>>
    }

    interface Remote : ProfilesSuggestRepo

    class Random<out T>(private val generate: () -> ProfileSuggest<out T>) : ProfilesSuggestRepo {
        fun generate(size: Int) = (0 until size).map { generate.invoke() }.toSet()
    }
}