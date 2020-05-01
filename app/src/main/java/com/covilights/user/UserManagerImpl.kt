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

package com.covilights.user

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mirhoseini.appsettings.AppSettings
import com.mohsenoid.closetome.utils.toUuid
import java.util.UUID

internal class UserManagerImpl(val context: Context) : UserManager {

    override val userUuid: UUID by lazy {
        AppSettings.getString(context, USER_UUID)?.toUuid() ?: run {
            val newUuid = UUID.randomUUID()
            AppSettings.setValue(context, USER_UUID, newUuid.toString())
            newUuid
        }
    }

    private val _userStatus = MutableLiveData(UserStatus.values()[AppSettings.getInt(context, USER_STATUS) ?: 0])
    override val userStatus: LiveData<UserStatus>
        get() = _userStatus

    init {
        userStatus.observeForever { value ->
            AppSettings.setValue(context, USER_STATUS, value.ordinal)
        }
    }

    override fun setUserStatus(status: UserStatus) {
        if (_userStatus.value != status) _userStatus.value = status
    }

    companion object {
        const val USER_UUID = "user_uuid"
        const val USER_STATUS = "user_status"
    }
}
