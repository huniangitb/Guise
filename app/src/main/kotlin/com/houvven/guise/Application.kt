package com.houvven.guise

import com.houvven.guise.injection.storeModule
import com.houvven.guise.injection.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin


typealias AndroidApplication = android.app.Application

class Application : AndroidApplication() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@Application)
            modules(
                storeModule,
                viewModelModule
            )
        }
    }
}