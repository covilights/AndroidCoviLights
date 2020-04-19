package com.covilights.view.main.design

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.covilights.databinding.DesignFragmentBinding

class DesignFragment : Fragment() {

    lateinit var binding: DesignFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DesignFragmentBinding.inflate(layoutInflater)
        return binding.root
    }
}
