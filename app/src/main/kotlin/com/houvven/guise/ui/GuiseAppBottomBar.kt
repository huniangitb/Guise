package com.houvven.guise.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.houvven.guise.R
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.HomeRouteDestination
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import com.ramcosta.composedestinations.spec.TypedDestinationSpec
import com.ramcosta.composedestinations.utils.currentDestinationAsState
import com.ramcosta.composedestinations.utils.startDestination

enum class BottomBarDestination(
    val direction: DirectionDestinationSpec,
    val icon: ImageVector,
    @StringRes val label: Int
) {
    Home(
        direction = HomeRouteDestination,
        icon = Icons.Filled.Home,
        label = R.string.home
    );

    companion object {
        @Suppress("unused")
        fun contains(destination: TypedDestinationSpec<out Any?>): Boolean {
            return entries.any { it.direction == destination }
        }
    }
}

@Composable
fun GuiseAppBottomBar(navController: NavController) {
    val currentDestination = navController.currentDestinationAsState().value
        ?: NavGraphs.root.startDestination

    val onNavigationItemClicked: (DirectionDestinationSpec) -> Unit = { destination ->
        navController.navigate(destination.route) {
            launchSingleTop = true
        }
    }

    // if currentDestination not in BottomBarDestination, don't show bottom bar
    if (BottomBarDestination.contains(currentDestination)) {
        NavigationBar {
            BottomBarDestination.entries.forEach { destination ->
                NavigationBarItem(
                    selected = currentDestination == destination.direction,
                    onClick = { onNavigationItemClicked(destination.direction) },
                    icon = { Icon(destination.icon, contentDescription = null) },
                    label = { Text(stringResource(destination.label)) }
                )
            }
        }
    }
}