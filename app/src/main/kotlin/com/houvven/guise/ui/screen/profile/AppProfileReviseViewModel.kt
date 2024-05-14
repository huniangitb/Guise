package com.houvven.guise.ui.screen.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppProfileReviseViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _stateFlow: MutableStateFlow<AppProfileReviseState> =
        MutableStateFlow(AppProfileReviseState())

    val stateFlow: StateFlow<AppProfileReviseState> = _stateFlow.asStateFlow()
}