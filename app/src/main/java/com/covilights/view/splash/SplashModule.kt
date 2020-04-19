package com.covilights.view.splash

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val splashModule = module {
    viewModel { SplashViewModel(stateManager = get()) }
}
