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

package com.covilights.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.covilights.BuildConfig

internal class MainViewModel(
    val statusViewModel: MainStatusViewModel,
    val contentViewModel: MainContentViewModel
) : ViewModel() {
    private val _navigate = MutableLiveData<NavDirections>()
    val navigate: LiveData<NavDirections>
        get() = _navigate

    private val _isDebugMode = MutableLiveData(BuildConfig.DEBUG)
    val isDebugMode: LiveData<Boolean> = _isDebugMode

    fun onDebugClick() {
        _navigate.value = MainFragmentDirections.actionMainFragmentToDebugFragment()
    }

    fun onDesignClick() {
        _navigate.value = MainFragmentDirections.actionMainFragmentToDesignFragment()
    }

    fun onToolbarLongClick(): Boolean {
        return if (_isDebugMode.value != true) {
            _isDebugMode.value = true
            true
        } else {
            false
        }
    }
}
