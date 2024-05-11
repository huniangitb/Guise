package com.houvven.guise.ui.screen.launch.home

import com.houvven.guise.util.app.App

/**
 * UI State that represents HomeScreen
 **/
data class HomeState(
    val appQuery: String = ""
)

/**
 * Home Actions emitted from the UI Layer
 * passed to the coordinator to handle
 **/
data class HomeActions(
    val onAppQueryChange: (String) -> Unit = {},
    val onAppClick: (App) -> Unit = {}
)