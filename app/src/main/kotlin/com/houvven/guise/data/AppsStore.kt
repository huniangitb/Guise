package com.houvven.guise.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dylanc.mmkv.IMMKVOwner
import com.dylanc.mmkv.MMKVOwner
import com.houvven.guise.util.app.App
import com.houvven.guise.util.app.AppScanner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppsStore(
    private val appScanner: AppScanner
) : IMMKVOwner by MMKVOwner(ID), ViewModel() {

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
        appScanner.scanAppsFlowAsUser(member.scanMode).collect { app ->
            stateFlow.update { state ->
                state.copy(apps = state.apps + app)
            }
        }
        stateFlow.doneLoading()
    }

    private val Member.stateFlow: MutableStateFlow<AppState>
        get() = when (this) {
            Member.SYSTEM -> _sysAppsState
            Member.USER -> _userAppsState
        }

    companion object {
        const val ID = "apps_store"
    }

    data class AppState(
        val apps: List<App> = emptyList(),
        val isLoading: Boolean = false
    )

    enum class Member(val storeKey: String, val scanMode: AppScanner.ScanMode) {
        USER(
            storeKey = "user_apps",
            scanMode = AppScanner.ScanMode.USER
        ),
        SYSTEM(
            storeKey = "sys_apps",
            scanMode = AppScanner.ScanMode.SYSTEM
        );
    }

    private fun MutableStateFlow<AppState>.reLoad() =
        update { state -> state.copy(isLoading = true, apps = emptyList()) }

    private fun MutableStateFlow<AppState>.doneLoading() =
        update { state -> state.copy(isLoading = false) }
}