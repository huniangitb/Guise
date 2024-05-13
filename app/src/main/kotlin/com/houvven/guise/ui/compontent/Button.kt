package com.houvven.guise.ui.compontent

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.houvven.guise.ui.LocalNavHostController

@Composable
fun ArrowBackButton(
    modifier: Modifier = Modifier,
    navHostController: NavHostController = LocalNavHostController.current
) {
    IconButton(
        modifier = modifier,
        onClick = { navHostController.popBackStack() }
    ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription = null)
    }
}