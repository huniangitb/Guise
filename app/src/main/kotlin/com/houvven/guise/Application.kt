package com.houvven.guise

import dagger.hilt.android.HiltAndroidApp


typealias AndroidApplication = android.app.Application

@HiltAndroidApp
class Application : AndroidApplication()