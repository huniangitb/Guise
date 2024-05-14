package com.houvven.guise.ui.screen.profile

/**
 * UI State that represents AppProfileReviseScreen
 **/
class AppProfileReviseState

/**
 * AppProfileRevise Actions emitted from the UI Layer
 * passed to the coordinator to handle
 **/
data class AppProfileReviseActions(
    val onSave: () -> Unit = {},
    val onClearAll: () -> Unit = {}
)