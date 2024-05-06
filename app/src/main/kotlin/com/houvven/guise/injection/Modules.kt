package com.houvven.guise.injection

import com.houvven.guise.data.AppsStore
import com.houvven.guise.ui.screen.launch.home.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val storeModule = module {
    single<AppsStore> { AppsStore(androidContext().packageManager) }
}

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
}