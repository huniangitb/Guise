package com.houvven.guise.injection

import com.houvven.guise.BuildConfig
import com.houvven.guise.data.AppsStore
import com.houvven.guise.ui.screen.launch.home.HomeViewModel
import com.houvven.guise.util.app.AppScanner
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val commonModule = module {
    // Provide the AppScanner
    single<AppScanner> {
        AppScanner(
            androidContext().packageManager,
            setOf(
                BuildConfig.APPLICATION_ID
            )
        )
    }

}

val storeModule = module {
    // Provide the AppsStore
    single<AppsStore> {
        AppsStore(
            appScanner = get()
        )
    }
}

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
}