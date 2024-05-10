package com.houvven.guise.ui.screen.launch.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.houvven.guise.R
import com.houvven.guise.data.AppsStore
import com.houvven.guise.hook.ModuleStatus
import com.houvven.guise.ui.compontent.AppListItem
import com.houvven.guise.ui.screen.launch.home.components.ModuleInactiveView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun HomeScreen(
    state: HomeState,
    actions: HomeActions,
    appsStore: AppsStore = koinInject(),
    viewModel: HomeViewModel = koinViewModel()
) {
    Scaffold(
        topBar = { HomeTopBar() }
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
private fun HomeTopBar(modifier: Modifier = Modifier) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppLazyColumn(
    appsStore: AppsStore,
    modifier: Modifier = Modifier,
    navController: NavHostController = LocalNavHostController.current
) {
    val userAppsState by appsStore.userAppState.collectAsStateWithLifecycle(AppsStore.AppState())
    val sysAppsState by appsStore.sysAppState.collectAsStateWithLifecycle(AppsStore.AppState())
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState { AppsStore.Member.entries.size }

    HorizontalPager(
        state = pagerState,
        pageSize = PageSize.Fill,
        key = { index -> AppsStore.Member.entries[index].name }
    ) {
        val appsState = when (AppsStore.Member.entries[it]) {
            AppsStore.Member.USER -> userAppsState
            AppsStore.Member.SYSTEM -> sysAppsState
        }
        PullToRefreshBox(
            isRefreshing = appsState.isLoading,
            onRefresh = {
                coroutineScope.launch(Dispatchers.Default) {
                    appsStore.loadApp(AppsStore.Member.entries[it])
                }
            },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = modifier
            ) {
                items(appsState.apps) { app ->
                    AppListItem(
                        app = app,
                        onClick = {
                            navController.navigateDirection(AppProfileReviseRouteDestination(app.packageName))
                        }
                    )
                }
            }
        }
    }
}