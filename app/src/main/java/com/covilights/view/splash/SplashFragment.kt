package com.covilights.view.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.covilights.databinding.SplashFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashFragment : Fragment() {

    lateinit var binding: SplashFragmentBinding

    private val viewModel: SplashViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SplashFragmentBinding.inflate(layoutInflater)
        val view = binding.root
        binding.viewmodel = viewModel

        viewModel.navigate.observe(viewLifecycleOwner, Observer { direction ->
            view.findNavController().navigate(direction)
        })

        return view
    }
}
