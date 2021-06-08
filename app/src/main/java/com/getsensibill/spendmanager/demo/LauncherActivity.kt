package com.getsensibill.spendmanager.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.getsensibill.spendmanager.demo.databinding.ActivityLauncherBinding

class LauncherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLauncherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            // Basic, direct integration
            directIntegrationKotlin.setOnClickListener { }
            directIntegrationJava.setOnClickListener { }

            // Subclass WebUiActivity integration
            inheritActivityKotlin.setOnClickListener { }
            inheritActivityJava.setOnClickListener { }

            // Direct fragment integration
            directFragmentKotlin.setOnClickListener { }
            directFragmentJava.setOnClickListener { }

            // Capture Flow integration
            captureFlowKotlin.setOnClickListener { }
            captureFlowJava.setOnClickListener { }
        }
    }
}