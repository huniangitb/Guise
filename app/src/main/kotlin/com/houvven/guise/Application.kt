package com.houvven.guise

import dagger.hilt.android.HiltAndroidApp


typealias AndroidApplication = android.app.Application

@HiltAndroidApp
class Application : AndroidApplication()
    override fun onCreate() {
        super.onCreate()
        // Initialize App Scanner
        AppsStore.initialize(packageManager)
    }