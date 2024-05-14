package com.houvven.guise.ui.screen.launch.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.houvven.guise.data.AppsStore
import com.houvven.guise.hook.ModuleStatus
import com.houvven.guise.hook.store.ModuleStore
import com.houvven.guise.ui.compontent.AppListItem
import com.houvven.guise.ui.screen.launch.home.components.HomeScreenTopBar
import com.houvven.guise.ui.screen.launch.home.components.ModuleInactiveView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun HomeScreen(
    state: HomeState,
    actions: HomeActions,
    appsStore: AppsStore = koinInject()
) {
    Scaffold(
        topBar = { HomeScreenTopBar(state, actions) }
    ) { innerPaddings ->
        Column(
            modifier = Modifier
                .padding(top = innerPaddings.calculateTopPadding())
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (ModuleStatus.isModuleActive) {
                AppLazyColumn(
                    state = state,
                    actions = actions,
                    modifier = Modifier.fillMaxSize(),
                    appsStore = appsStore
                )
            } else {
                ModuleInactiveView()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppLazyColumn(
    state: HomeState,
    actions: HomeActions,
    appsStore: AppsStore,
    modifier: Modifier = Modifier,
    moduleStore: ModuleStore.Hooker = koinInject()
) {
    val userAppsState by appsStore.userAppState.collectAsStateWithLifecycle(AppsStore.AppState())
    val sysAppsState by appsStore.sysAppState.collectAsStateWithLifecycle(AppsStore.AppState())
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState { AppsStore.Member.entries.size }
    val lazyListState = rememberLazyListState()

    val configuredPackages = moduleStore.configuredPackages

    HorizontalPager(
        state = pagerState,
        pageSize = PageSize.Fill,
        key = { index -> AppsStore.Member.entries[index].name }
    ) { pageIndex ->
        val member = AppsStore.Member.entries[pageIndex]
        val appsState = when (member) {
            AppsStore.Member.USER -> userAppsState
            AppsStore.Member.SYSTEM -> sysAppsState
        }
        PullToRefreshBox(
            isRefreshing = appsState.isLoading,
            onRefresh = {
                coroutineScope.launch(Dispatchers.Default) { appsStore.loadApp(member) }
            },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = modifier,
                state = lazyListState
            ) {
                items(
                    items = appsState
                        .filter(query = state.appQuery)
                        .sortedBy { it.packageName !in configuredPackages },
                    key = { it.packageName },
                    contentType = { it }
                ) { app ->
                    // container color, if the app is configured set to primary container
                    val containerColor = if (configuredPackages.contains(app.packageName)) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        Color.Unspecified
                    }
                    AppListItem(
                        app = app,
                        onClick = actions.onAppClick,
                        modifier = Modifier.animateItem(),
                        colors = ListItemDefaults.colors(containerColor = containerColor)
                    )
                }
            }
        }
    }
}