package com.covilights.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections

class MainViewModel() : ViewModel() {

    private val _navigate = MutableLiveData<NavDirections>()
    val navigate: LiveData<NavDirections>
        get() = _navigate

    fun onDebugClick() {
        _navigate.value = MainFragmentDirections.actionMainFragmentToDebugFragment()
    }

    fun onDesignClick() {
        _navigate.value = MainFragmentDirections.actionMainFragmentToDesignFragment()
    }
}
