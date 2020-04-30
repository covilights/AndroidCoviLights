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
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.covilights.R
import com.covilights.databinding.DebugFragmentBinding
import com.covilights.user.UserManager
import com.mohsenoid.closetome.CloseToMe
import com.mohsenoid.closetome.CloseToMeCallback
import com.mohsenoid.closetome.CloseToMeState
import org.koin.android.ext.android.inject

/**
 * This fragment holds debugging tools and buttons and is not visible in final product.
 */
class DebugFragment : Fragment() {

    private lateinit var binding: DebugFragmentBinding

    private val userManager: UserManager by inject()
    private val closeToMe: CloseToMe by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DebugFragmentBinding.inflate(layoutInflater)
        val view = binding.root

        initUi()

        binding.user.text = "User: ${userManager.userUuid}"

        closeToMe.state.observe(viewLifecycleOwner, Observer { state ->
            log("Beacon state: $state")

            if (state == CloseToMeState.STARTED) {
                binding.start.isVisible = false
                binding.stop.isVisible = true
            } else {
                binding.start.isVisible = true
                binding.stop.isVisible = false
            }
        })

        closeToMe.results.observe(viewLifecycleOwner, Observer { beacons ->
            binding.result.text = beacons.values.joinToString("\n--------------------------------\n") {
                "User: ${it.userUuid}\n" +
                    "isVisible: ${it.isVisible}\n" +
                    "isNear: ${it.isNear}\n" +
                    "MinDistance: ${"%.2f".format(it.minDistanceInMeter)}m\n" +
                    "LastDistance: ${"%.2f".format(it.distanceInMeter)}m"
            }

            log("Result: $beacons")
        })

        if (!closeToMe.hasBleFeature()) {
            Toast.makeText(requireContext(), R.string.ble_not_supported, Toast.LENGTH_LONG).show()
        }

        return view
    }

    private fun initUi() {
        binding.log.movementMethod = ScrollingMovementMethod()
        binding.start.setOnClickListener { onStartClick() }
        binding.stop.setOnClickListener { onStopClick() }
    }

    private fun onStartClick() {
        if (closeToMe.isBluetoothEnabled.value == false) {
            log("Enabling bluetooth...")

            closeToMe.enableBluetooth(object : CloseToMeCallback {
                override fun onSuccess() {
                    log("Bluetooth is on now")
                }

                override fun onError(throwable: Throwable) {
                    log(throwable.message ?: throwable.toString())
                }
            })
        } else {
            closeToMe.start(object : CloseToMeCallback {
                override fun onSuccess() {
                    log("Beacon started successfully!")
                }

                override fun onError(throwable: Throwable) {
                    log(throwable.message ?: throwable.toString())
                }
            })
        }
    }

    private fun onStopClick() {
        closeToMe.stop(object : CloseToMeCallback {
            override fun onSuccess() {
                log("Beacon stopped successfully!")
            }

            override fun onError(throwable: Throwable) {
                log(throwable.message ?: throwable.toString())
            }
        })
    }

    private fun log(message: String) {
        requireActivity().runOnUiThread {
            binding.log.text = "$message\n" +
                "-----------------------------------\n" +
                "${binding.log.text}"
        }
    }
}
