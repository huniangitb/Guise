package com.houvven.guise.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.houvven.guise.util.app.App
import com.houvven.guise.util.app.AppScanner
import com.houvven.guise.util.app.AppSortComparator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppsStore(
    private val appScanner: AppScanner
) : ViewModel() {

    private val _userAppsState = MutableStateFlow(AppState())
    private val _sysAppsState = MutableStateFlow(AppState())
    val userAppState = _userAppsState.asStateFlow()
    val sysAppState = _sysAppsState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.Default) { loadApp(Member.USER) }
        viewModelScope.launch(Dispatchers.Default) { loadApp(Member.SYSTEM) }
    }

    suspend fun loadApp(member: Member) {
        val stateFlow = member.stateFlow
        stateFlow.reLoad()
        appScanner.scanAppsAsUser(member.scanMode)
            .sortedWith(AppSortComparator.AppNameLocaleComparator)
            .let { apps -> stateFlow.update { it.copy(apps = apps) } }
        stateFlow.doneLoading()
    }

    private val Member.stateFlow: MutableStateFlow<AppState>
        get() = when (this) {
            Member.SYSTEM -> _sysAppsState
            Member.USER -> _userAppsState
        }

    data class AppState(
        val apps: List<App> = emptyList(),
        val isLoading: Boolean = false,
    ) {
        fun filter(query: String): List<App> {
            return if (query.isBlank()) apps else apps.filter { app ->
                app.name.contains(query, true)
            }
        }
    }

    enum class Member(val scanMode: AppScanner.ScanMode) {
        USER(
            scanMode = AppScanner.ScanMode.USER
        ),
        SYSTEM(
            scanMode = AppScanner.ScanMode.SYSTEM
        );
    }

    private fun MutableStateFlow<AppState>.reLoad() =
        update { state -> state.copy(isLoading = true, apps = emptyList()) }

    private fun MutableStateFlow<AppState>.doneLoading() =
        update { state -> state.copy(isLoading = false) }
}