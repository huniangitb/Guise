package com.houvven.guise.ui.compontent

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.houvven.guise.util.app.App

@Composable
fun AppListItem(
    app: App,
    modifier: Modifier = Modifier,
    onClick: (App) -> Unit = {},
    colors: ListItemColors = ListItemDefaults.colors()
) {
    ListItem(
        headlineContent = { Text(text = app.name) },
        supportingContent = { if (app.name != app.packageName) Text(text = app.packageName) },
        leadingContent = {
            Image(
                bitmap = app.icon,
                contentDescription = null,
                modifier = Modifier
                    .size(35.dp)
                    .clip(MaterialTheme.shapes.small)
            )
        },
        modifier = modifier.clickable { onClick.invoke(app) },
        colors = colors
    )
}