package com.houvven.guise.ui

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.houvven.guise.ui.theme.GuiseTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GuiseApp() {
    GuiseTheme {
        Scaffold(
            modifier = Modifier
        ) { innerPadding ->

        }
    }
}