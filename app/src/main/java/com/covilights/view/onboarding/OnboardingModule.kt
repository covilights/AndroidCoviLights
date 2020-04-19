package com.covilights.view.onboarding

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val onboardingModule = module {
    viewModel { OnboardingViewModel(context = androidContext(), resources = get(), stateManager = get(), userManager = get()) }
}
