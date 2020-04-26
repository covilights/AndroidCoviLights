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

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.covilights.R
import com.covilights.user.UserManager
import com.covilights.user.UserStatus

class MainViewModel(val context: Context, val userManager: UserManager) : ViewModel() {

    private val _navigate = MutableLiveData<NavDirections>()
    val navigate: LiveData<NavDirections>
        get() = _navigate
    val status: LiveData<UserStatus>
        get() = _status
    private val _status: MutableLiveData<UserStatus> = MutableLiveData(userManager.userStatus)

    val statusIcon: LiveData<Drawable> = Transformations.map(_status) { userStatus ->
        val resourceId = when (userStatus) {
            UserStatus.NEGATIVE -> R.drawable.ic_confirm
            UserStatus.EXPOSED -> R.drawable.ic_attention
            UserStatus.POSITIVE -> R.drawable.ic_alert
        }
        ContextCompat.getDrawable(context, resourceId)
    }

    val statusIconColor: LiveData<Int> = Transformations.map(_status) { userStatus ->
        val resourceId = when (userStatus) {
            UserStatus.POSITIVE,
            UserStatus.NEGATIVE -> R.color.status_ok_icon
            UserStatus.EXPOSED -> R.color.primary
        }
        ContextCompat.getColor(context, resourceId)
    }

    val statusTextColor: LiveData<Int> = Transformations.map(_status) { userStatus ->
        val resourceId = when (userStatus) {
            UserStatus.POSITIVE,
            UserStatus.NEGATIVE -> R.color.status_ok_icon
            UserStatus.EXPOSED -> R.color.primary
        }
        ContextCompat.getColor(context, resourceId)
    }

    val statusTitle: LiveData<String> = Transformations.map(_status) { userStatus ->
        val resourceId = when (userStatus) {
            UserStatus.NEGATIVE -> R.string.main_status_ok_title
            UserStatus.EXPOSED -> R.string.main_status_exposed_title
            UserStatus.POSITIVE -> R.string.main_status_positive_title
        }
        context.getString(resourceId)
    }

    val statusSubtitle: LiveData<String> = Transformations.map(_status) { userStatus ->
        val resourceId = when (userStatus) {
            UserStatus.NEGATIVE -> R.string.main_status_ok_subtitle
            UserStatus.EXPOSED -> R.string.main_status_exposed_subtitle
            UserStatus.POSITIVE -> R.string.main_status_positive_subtitle
        }
        context.getString(resourceId)
    }

    val statusBackgroundColor: LiveData<Int> = Transformations.map(_status) { userStatus ->
        val resourceId = when (userStatus) {
            UserStatus.NEGATIVE -> R.color.status_ok_bg
            UserStatus.EXPOSED -> R.color.status_exposed_bg
            UserStatus.POSITIVE -> R.color.status_positive_bg
        }
        ContextCompat.getColor(context, resourceId)
    }

    val statusMoreInfoBgColor: LiveData<Int> = Transformations.map(_status) { userStatus ->
        val resourceId = when (userStatus) {
            UserStatus.NEGATIVE -> R.color.status_ok_more_info_bg
            UserStatus.EXPOSED -> R.color.status_exposed_more_info_bg
            UserStatus.POSITIVE -> R.color.status_positive_more_info_bg
        }
        ContextCompat.getColor(context, resourceId)
    }
}
