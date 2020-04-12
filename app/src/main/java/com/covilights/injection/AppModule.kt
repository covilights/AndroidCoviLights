package com.covilights.injection

import com.covilights.beacon.config.BeaconConfigProvider
import com.covilights.beacon.config.BeaconConfigProviderImpl
import com.covilights.user.UserManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {

    single { UserManager(androidContext()) }

    single<BeaconConfigProvider> { BeaconConfigProviderImpl(userManager = get()) }
}
