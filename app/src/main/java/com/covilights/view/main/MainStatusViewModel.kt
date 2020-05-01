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
import com.covilights.user.UserManager
import com.covilights.user.UserStatus

internal class MainStatusViewModel(val context: Context, userManager: UserManager) : ViewModel() {

    val statusIcon: LiveData<Drawable> = Transformations.map(userManager.userStatus) { status ->
        status?.let {
            val resourceId = when (status) {
                UserStatus.NEGATIVE -> R.drawable.ic_confirm
                UserStatus.EXPOSED_WITHOUT_SYMPTOMS,
                UserStatus.EXPOSED_WITH_SYMPTOMS -> R.drawable.ic_attention
                UserStatus.POSITIVE -> R.drawable.ic_alert
            }
            ContextCompat.getDrawable(context, resourceId)
        }
    }

    val statusIconTint: LiveData<ColorStateList> = Transformations.map(userManager.userStatus) { status ->
        status?.let {
            val resourceId = when (status) {
                UserStatus.POSITIVE,
                UserStatus.NEGATIVE -> R.color.status_ok_icon
                UserStatus.EXPOSED_WITHOUT_SYMPTOMS,
                UserStatus.EXPOSED_WITH_SYMPTOMS -> R.color.primary
            }
            ColorStateList.valueOf(ContextCompat.getColor(context, resourceId))
        }
    }

    val statusTextColor: LiveData<Int> = Transformations.map(userManager.userStatus) { status ->
        status?.let {
            val resourceId = when (status) {
                UserStatus.POSITIVE,
                UserStatus.NEGATIVE -> R.color.status_ok_icon
                UserStatus.EXPOSED_WITHOUT_SYMPTOMS,
                UserStatus.EXPOSED_WITH_SYMPTOMS -> R.color.primary
            }
            ContextCompat.getColor(context, resourceId)
        }
    }

    val statusTitle: LiveData<String> = Transformations.map(userManager.userStatus) { status ->
        status?.let {
            val resourceId = when (status) {
                UserStatus.NEGATIVE -> R.string.main_status_ok_title
                UserStatus.EXPOSED_WITHOUT_SYMPTOMS -> R.string.main_status_exposed_without_symptoms_title
                UserStatus.EXPOSED_WITH_SYMPTOMS -> R.string.main_status_exposed_with_symptoms_title
                UserStatus.POSITIVE -> R.string.main_status_positive_title
            }
            context.getString(resourceId)
        }
    }

    val statusSubtitle: LiveData<String> = Transformations.map(userManager.userStatus) { status ->
        status?.let {
            val resourceId = when (status) {
                UserStatus.NEGATIVE -> R.string.main_status_ok_subtitle
                UserStatus.EXPOSED_WITHOUT_SYMPTOMS -> R.string.main_status_exposed_without_symptoms_subtitle
                UserStatus.EXPOSED_WITH_SYMPTOMS -> R.string.main_status_exposed_with_symptoms_subtitle
                UserStatus.POSITIVE -> R.string.main_status_positive_subtitle
            }
            context.getString(resourceId)
        }
    }

    val quarantineDays: Int = EXPOSED_QUARANTINE_DAYS

    val statusBackgroundColor: LiveData<Int> = Transformations.map(userManager.userStatus) { status ->
        status?.let {
            val resourceId = when (status) {
                UserStatus.NEGATIVE -> R.color.status_ok_bg
                UserStatus.EXPOSED_WITHOUT_SYMPTOMS,
                UserStatus.EXPOSED_WITH_SYMPTOMS -> R.color.status_exposed_bg
                UserStatus.POSITIVE -> R.color.status_positive_bg
            }
            ContextCompat.getColor(context, resourceId)
        }
    }

    val statusMoreInfoBackgroundColor: LiveData<ColorStateList> = Transformations.map(userManager.userStatus) { status ->
        status?.let {
            val resourceId = when (status) {
                UserStatus.NEGATIVE -> R.color.status_ok_more_info_bg
                UserStatus.EXPOSED_WITHOUT_SYMPTOMS,
                UserStatus.EXPOSED_WITH_SYMPTOMS -> R.color.status_exposed_more_info_bg
                UserStatus.POSITIVE -> R.color.status_positive_more_info_bg
            }
            ColorStateList.valueOf(ContextCompat.getColor(context, resourceId))
        }
    }

    companion object {
        const val EXPOSED_QUARANTINE_DAYS = 4
    }
}
