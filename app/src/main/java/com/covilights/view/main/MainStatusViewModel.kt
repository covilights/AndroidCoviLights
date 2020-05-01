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
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.covilights.R
import com.covilights.user.UserStatus
import com.covilights.user.UserViewModel

internal class MainStatusViewModel(val context: Context, val userViewModel: UserViewModel) : ViewModel() {

    private val userStatus: LiveData<UserStatus>
        get() = userViewModel.userStatus

    val statusIcon: LiveData<Drawable> = Transformations.map(userStatus) { status ->
        status?.let {
            val resourceId = when (status) {
                UserStatus.NEGATIVE -> R.drawable.ic_confirm
                UserStatus.EXPOSED -> R.drawable.ic_attention
                UserStatus.POSITIVE -> R.drawable.ic_alert
            }
            ContextCompat.getDrawable(context, resourceId)
        }
    }

    val statusIconTint: LiveData<ColorStateList> = Transformations.map(userStatus) { status ->
        status?.let {
            val resourceId = when (status) {
                UserStatus.POSITIVE,
                UserStatus.NEGATIVE -> R.color.status_ok_icon
                UserStatus.EXPOSED -> R.color.primary
            }
            ColorStateList.valueOf(ContextCompat.getColor(context, resourceId))
        }
    }

    val statusTextColor: LiveData<Int> = Transformations.map(userStatus) { status ->
        status?.let {
            val resourceId = when (status) {
                UserStatus.POSITIVE,
                UserStatus.NEGATIVE -> R.color.status_ok_icon
                UserStatus.EXPOSED -> R.color.primary
            }
            ContextCompat.getColor(context, resourceId)
        }
    }

    val statusTitle: LiveData<String> = Transformations.map(userStatus) { status ->
        status?.let {
            val resourceId = when (status) {
                UserStatus.NEGATIVE -> R.string.main_status_ok_title
                UserStatus.EXPOSED -> R.string.main_status_exposed_title
                UserStatus.POSITIVE -> R.string.main_status_positive_title
            }
            context.getString(resourceId)
        }
    }

    val statusSubtitle: LiveData<String> = Transformations.map(userStatus) { status ->
        status?.let {
            val resourceId = when (status) {
                UserStatus.NEGATIVE -> R.string.main_status_ok_subtitle
                UserStatus.EXPOSED -> R.string.main_status_exposed_subtitle
                UserStatus.POSITIVE -> R.string.main_status_positive_subtitle
            }
            context.getString(resourceId)
        }
    }

    val statusBackgroundColor: LiveData<Int> = Transformations.map(userStatus) { status ->
        status?.let {
            val resourceId = when (status) {
                UserStatus.NEGATIVE -> R.color.status_ok_bg
                UserStatus.EXPOSED -> R.color.status_exposed_bg
                UserStatus.POSITIVE -> R.color.status_positive_bg
            }
            ContextCompat.getColor(context, resourceId)
        }
    }

    val statusMoreInfoBackgroundColor: LiveData<Int> = Transformations.map(userStatus) { status ->
        status?.let {
            val resourceId = when (status) {
                UserStatus.NEGATIVE -> R.color.status_ok_more_info_bg
                UserStatus.EXPOSED -> R.color.status_exposed_more_info_bg
                UserStatus.POSITIVE -> R.color.status_positive_more_info_bg
            }
            ContextCompat.getColor(context, resourceId)
        }
    }
}
