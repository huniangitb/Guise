package com.houvven.guise.data.repository

sealed interface ProfilesSuggestRepo {

    interface Local : ProfilesSuggestRepo

    interface Remote : ProfilesSuggestRepo

    interface Random : ProfilesSuggestRepo
}