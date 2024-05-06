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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.houvven.guise.R
import com.houvven.guise.data.AppsStore
import com.houvven.guise.ui.compontent.AppListItem
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
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.padding(innerPaddings)
        ) {
            AppLazyColumn(
                modifier = Modifier.fillMaxSize(),
                appsStore = appsStore
            )
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
    modifier: Modifier = Modifier
) {
    val installedApps by appsStore.apps.collectAsStateWithLifecycle(emptyList())
    val loadState by appsStore.appLoadState.collectAsStateWithLifecycle(AppsStore.AppLoadState())
    val coroutineScope = rememberCoroutineScope()

    PullToRefreshBox(
        isRefreshing = loadState.isLoading,
        onRefresh = {
            coroutineScope.launch(Dispatchers.Default) {
                appsStore.loadApps()
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = modifier
        ) {
            items(installedApps) { app ->
                AppListItem(app = app)
            }
        }
    }
}