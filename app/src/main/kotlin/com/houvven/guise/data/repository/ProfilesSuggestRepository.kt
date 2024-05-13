package com.houvven.guise.data.repository

sealed interface ProfilesSuggestRepository {

    interface Local : ProfilesSuggestRepository

    interface Remote : ProfilesSuggestRepository

    interface Random : ProfilesSuggestRepository
}