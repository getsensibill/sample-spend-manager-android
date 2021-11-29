package com.getsensibill.spendmanager.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.getsensibill.spendmanager.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.authKotlinButton.setOnClickListener {
            startActivity(Intent(this, DemoAuthActivity::class.java))
        }

        binding.authJavaButton.setOnClickListener {
            startActivity(Intent(this, DemoAuthJavaActivity::class.java))
        }
    }
}
