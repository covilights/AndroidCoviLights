/*
 * Copyright 2020 CoviLights GbR
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.covilights.injection

import com.covilights.beacon.config.BeaconConfigProvider
import com.covilights.beacon.config.BeaconConfigProviderImpl
import com.covilights.user.UserManager
import com.covilights.utils.StateManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {

    single { UserManager(androidContext()) }

    single { StateManager(androidContext()) }

    single { androidContext().resources }

    single<BeaconConfigProvider> { BeaconConfigProviderImpl(userManager = get()) }
}
