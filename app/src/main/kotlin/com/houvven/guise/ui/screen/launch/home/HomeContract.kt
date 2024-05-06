package com.houvven.guise.ui.screen.launch.home

/**
 * UI State that represents HomeScreen
 **/
class HomeState

/**
 * Home Actions emitted from the UI Layer
 * passed to the coordinator to handle
 **/
data class HomeActions(
    val onClick: () -> Unit = {}
)