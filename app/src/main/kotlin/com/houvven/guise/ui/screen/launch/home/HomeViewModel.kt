package com.houvven.guise.ui.screen.launch.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.houvven.guise.data.AppsStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// @HiltViewModel
class HomeViewModel(
    private val appsStore: AppsStore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _stateFlow: MutableStateFlow<HomeState> = MutableStateFlow(HomeState())

    val stateFlow: StateFlow<HomeState> = _stateFlow.asStateFlow()
}