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

import androidx.lifecycle.LiveData
import java.util.UUID

/**
 * User data manager class holding UUID and Status.
 */
interface UserManager {

    /**
     * User UUID randomly generated at first run.
     */
    val userUuid: UUID

    /**
     * User status stated by the user [LiveData].
     */
    val userStatus: LiveData<UserStatus>

    /**
     * User status setter.
     */
    fun setUserStatus(status: UserStatus)
}
