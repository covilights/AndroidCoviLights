package com.covilights.view.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.covilights.databinding.SplashActivityBinding

class SplashActivity : AppCompatActivity() {

    lateinit var binding: SplashActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SplashActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}
