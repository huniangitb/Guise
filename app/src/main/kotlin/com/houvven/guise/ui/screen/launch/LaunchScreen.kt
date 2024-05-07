package com.houvven.guise.ui.screen.launch

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.houvven.guise.R
import com.houvven.guise.hook.ModuleStatus
import com.houvven.guise.ui.screen.launch.home.HomeRoute
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph

private enum class MainBottomBarNavDestination(
    val icon: ImageVector,
    @StringRes val label: Int
) {
    Home(icon = Icons.Filled.Home, label = R.string.home);

    companion object {
        val start = Home
    }
}

@Destination<RootGraph>(start = true)
@Composable
fun MainScreen() {
    var currentDestination by rememberSaveable {
        mutableStateOf(MainBottomBarNavDestination.start)
    }

    val bottomBar: @Composable () -> Unit = {
        if (ModuleStatus.isModuleActive) {
            GuiseAppBottomBar(currentDestination.name) {
                currentDestination = MainBottomBarNavDestination.valueOf(it)
            }
        }
    }

    Scaffold(
        bottomBar = bottomBar
    ) { innerPaddings ->

        val pagerState = rememberPagerState { MainBottomBarNavDestination.entries.size }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(bottom = innerPaddings.calculateBottomPadding()),
            pageSize = PageSize.Fill
        ) {
            when (currentDestination) {
                MainBottomBarNavDestination.Home -> HomeRoute()
            }
        }
    }

}

@Composable
private fun GuiseAppBottomBar(currentDestination: String, onDestinationChange: (String) -> Unit) {

    NavigationBar {
        MainBottomBarNavDestination.entries.forEach { destination ->
            NavigationBarItem(
                selected = currentDestination == destination.name,
                onClick = { onDestinationChange(destination.name) },
                icon = { Icon(destination.icon, contentDescription = null) },
                label = { Text(stringResource(destination.label)) }
            )
        }
    }
}