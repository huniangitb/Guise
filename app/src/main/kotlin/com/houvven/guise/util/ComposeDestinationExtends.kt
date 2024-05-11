package com.houvven.guise.util

import androidx.navigation.NavHostController
import com.ramcosta.composedestinations.spec.Direction


fun NavHostController.navigateDirection(direction: Direction) {
    navigate(direction.route)
}