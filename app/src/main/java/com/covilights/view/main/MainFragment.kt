package com.covilights.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.covilights.databinding.MainFragmentBinding
import com.covilights.service.BeaconServiceActions
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules

class MainFragment : Fragment() {

    lateinit var binding: MainFragmentBinding

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
        // setupOnBackExit()

        return view
    }

    private fun startBeaconService() {
        ContextCompat.startForegroundService(requireContext(), BeaconServiceActions.AppStart.toIntent(requireContext()))
    }

    private fun setupOnBackExit() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
    }

    override fun onDestroy() {
        unloadKoinModules(mainModule)
        super.onDestroy()
    }
}
