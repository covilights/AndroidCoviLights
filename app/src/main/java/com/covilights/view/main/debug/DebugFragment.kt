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

package com.covilights.view.main.debug

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.covilights.databinding.DebugFragmentBinding
import com.covilights.user.UserManager
import org.koin.android.ext.android.inject

/**
 * This fragment holds debugging tools and buttons and is not visible in final product.
 */
class DebugFragment : Fragment() {

    private lateinit var binding: DebugFragmentBinding

    private val userManager: UserManager by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DebugFragmentBinding.inflate(layoutInflater)
        val view = binding.root

        initUi()

        binding.user.text = "User: ${userManager.userUuid}"


        return view
    }

    private fun initUi() {
        binding.log.movementMethod = ScrollingMovementMethod()
        binding.start.setOnClickListener { onStartClick() }
        binding.stop.setOnClickListener { onStopClick() }
    }

    private fun onStartClick() {
    }

    private fun onStopClick() {
    }

    private fun log(message: String) {
        requireActivity().runOnUiThread {
            binding.log.text = "$message\n" +
                "-----------------------------------\n" +
                "${binding.log.text}"
        }
    }
}
