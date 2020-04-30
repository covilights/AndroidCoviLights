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

package com.covilights.view.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.covilights.utils.Constants
import com.covilights.utils.StateManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class SplashViewModel(private val stateManager: StateManager) : ViewModel() {

    private val _navigate = MutableLiveData<NavDirections>()
    val navigate: LiveData<NavDirections>
        get() = _navigate

    private val job = viewModelScope.launch {
        delay(Constants.SPLASH_TIMEOUT)
        navigate()
    }

    fun onClick() {
        job.cancel()
        navigate()
    }

    private fun navigate() {
        _navigate.value = if (stateManager.isFirstRun) {
            SplashFragmentDirections.actionSplashFragmentToOnboardingFragment()
        } else {
            SplashFragmentDirections.actionSplashFragmentToMainFragment()
        }
    }
}
