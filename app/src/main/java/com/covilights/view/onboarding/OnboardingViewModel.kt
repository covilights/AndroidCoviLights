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

package com.covilights.view.onboarding

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.covilights.R
import com.covilights.user.UserStatus
import com.covilights.user.UserViewModel
import com.covilights.utils.StateManager

internal class OnboardingViewModel(
    private val context: Context,
    private val resources: Resources,
    private val stateManager: StateManager,
    private val userViewModel: UserViewModel
) : ViewModel() {

    private val _stepIndex = MutableLiveData(0)
    val stepIndex: LiveData<Int>
        get() = _stepIndex

    private val _navigate = MutableLiveData<NavDirections>()
    val navigate: LiveData<NavDirections>
        get() = _navigate

    private val _permission = MutableLiveData(false)
    val permission: LiveData<Boolean>
        get() = _permission

    private val _requirePermissionToast = MutableLiveData(false)
    val requirePermissionToast: LiveData<Boolean>
        get() = _requirePermissionToast

    private val _requirePermissionSnackbar = MutableLiveData(false)
    val requirePermissionSnackbar: LiveData<Boolean>
        get() = _requirePermissionSnackbar

    private val _statusDialog = MutableLiveData(false)
    val statusDialog: LiveData<Boolean>
        get() = _statusDialog

    val startVisible: LiveData<Boolean> = Transformations.map(_stepIndex) { index ->
        index == 0
    }

    val icon: LiveData<Drawable> = Transformations.map(_stepIndex) { index ->
        val resourceId = resources.obtainTypedArray(R.array.onboarding_icon).getResourceId(index, -1)
        ContextCompat.getDrawable(context, resourceId)
    }

    val title: LiveData<String> = Transformations.map(_stepIndex) { index ->
        resources.getStringArray(R.array.onboarding_title)[index]
    }

    val subtitle: LiveData<String> = Transformations.map(_stepIndex) { index ->
        resources.getStringArray(R.array.onboarding_subtitle)[index]
    }

    fun onNextClick() {
        when (_stepIndex.value ?: 0) {
            LAST_STEP_INDEX -> {
                stateManager.isFirstRun = false
                _navigate.value = OnboardingFragmentDirections.actionOnboardingFragmentToMainFragment()
            }
            PERMISSION_STEP_INDEX -> _permission.value = true
            STATUS_STEP_INDEX -> _statusDialog.value = true
            else -> stepForward()
        }
    }

    fun onPermissionsGranted() {
        stepForward()
    }

    fun onPermissionsNotGranted() {
        _requirePermissionToast.value = true
    }

    fun onPermissionsDenied() {
        _requirePermissionSnackbar.value = true
    }

    fun onStatusSelected(status: UserStatus) {
        userViewModel.userStatus.value = status
        stepForward()
    }

    fun onPrevClick() {
        stepBackward()
    }

    private fun stepForward() {
        _stepIndex.value = (_stepIndex.value ?: 0) + 1
    }

    private fun stepBackward() {
        _stepIndex.value = (_stepIndex.value ?: 0) - 1
    }

    companion object {
        private const val LAST_STEP_INDEX = 3
        private const val PERMISSION_STEP_INDEX = 1
        private const val STATUS_STEP_INDEX = 2
    }
}
