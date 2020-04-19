package com.covilights.view.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.covilights.utils.Constants
import com.covilights.utils.StateManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(private val stateManager: StateManager) : ViewModel() {

    private val _navigate = MutableLiveData<Boolean>()
    val navigate: LiveData<Boolean>
        get() = _navigate

    private val job = viewModelScope.launch {
        delay(Constants.SPLASH_TIMEOUT)
        navigate()
    }

    fun onClick() {
        job.cancel()
        navigate()
    }

    private fun navigate() {
        _navigate.value = stateManager.isFirstRun
    }
}
