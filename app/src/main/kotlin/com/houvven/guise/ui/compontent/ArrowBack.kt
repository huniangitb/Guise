package com.houvven.guise.ui.compontent

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.houvven.guise.ui.LocalNavHostController
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
@Composable
fun ArrowBackButton(
    modifier: Modifier = Modifier,
    navHostController: NavHostController = LocalNavHostController.current,
    debounceDuration: Long = 300
) {
    val clickEvent = remember { MutableSharedFlow<Unit>() }
    val coroutineScope = rememberCoroutineScope()
    val debouncedClickEvent by remember {
        mutableStateOf(clickEvent.debounce(debounceDuration))
    }

    IconButton(
        modifier = modifier,
        onClick = { coroutineScope.launch { clickEvent.emit(Unit) } }
    ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription = null)
    }

    LaunchedEffect(debouncedClickEvent) {
        launch {
            debouncedClickEvent.collect { navHostController.popBackStack() }
        }
    }
}