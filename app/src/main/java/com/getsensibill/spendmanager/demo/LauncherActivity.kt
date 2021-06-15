package com.getsensibill.spendmanager.demo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.getsensibill.spendmanager.demo.capture_flow.CaptureFlowKotlinActivity
import com.getsensibill.spendmanager.demo.databinding.ActivityLauncherBinding
import com.getsensibill.spendmanager.demo.direct.DirectJavaActivity
import com.getsensibill.spendmanager.demo.direct.DirectKotlinActivity
import com.getsensibill.spendmanager.demo.fragment.FragmentJavaActivity
import com.getsensibill.spendmanager.demo.fragment.FragmentKotlinActivity
import com.getsensibill.spendmanager.demo.subclass.SubclassJavaActivity
import com.getsensibill.spendmanager.demo.subclass.SubclassKotlinActivity

class LauncherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLauncherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            // Basic, direct integration
            directIntegrationKotlin.setOnClickListener {
                goToActivity(DirectKotlinActivity::class.java)
            }
            directIntegrationJava.setOnClickListener {
                goToActivity(DirectJavaActivity::class.java)
            }

            // Subclass WebUiActivity integration
            subclassActivityKotlin.setOnClickListener {
                goToActivity(SubclassKotlinActivity::class.java)
            }
            subclassActivityJava.setOnClickListener {
                goToActivity(SubclassJavaActivity::class.java)
            }

            // Direct fragment integration
            directFragmentKotlin.setOnClickListener {
                goToActivity(FragmentKotlinActivity::class.java)
            }
            directFragmentJava.setOnClickListener {
                goToActivity(FragmentJavaActivity::class.java)
            }

            // Capture Flow integration
            captureFlowKotlin.setOnClickListener {
                goToActivity(CaptureFlowKotlinActivity::class.java)
            }
            captureFlowJava.setOnClickListener { }
        }
    }

    /**
     * Helper function to keep the intent building simple
     */
    private fun goToActivity(kClass: Class<*>?) {
        val intent = Intent(this, kClass)
        startActivity(intent)
    }
}
