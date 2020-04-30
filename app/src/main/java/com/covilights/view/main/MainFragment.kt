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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.covilights.databinding.MainFragmentBinding
import com.covilights.service.BeaconServiceActions
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules

/**
 * This fragment contains the app main screen including user states and other functionality.
 */
class MainFragment : Fragment() {

    private lateinit var binding: MainFragmentBinding

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadKoinModules(mainModule)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = MainFragmentBinding.inflate(layoutInflater)
        val view = binding.root
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.navigate.observe(viewLifecycleOwner, Observer { direction ->
            view.findNavController().navigate(direction)
        })

        startBeaconService()

        return view
    }

    private fun startBeaconService() {
        ContextCompat.startForegroundService(requireContext(), BeaconServiceActions.AppStart.toIntent(requireContext()))
    }

    override fun onDestroy() {
        unloadKoinModules(mainModule)
        super.onDestroy()
    }
}
