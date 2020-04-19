package com.covilights.view.onboarding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.covilights.databinding.OnboardingActivityBinding

class OnboardingActivity : AppCompatActivity() {

    lateinit var binding: OnboardingActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OnboardingActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}
