package com.getsensibill.spendmanager.demo.direct

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.getsensibill.spendmanager.demo.R
import com.getsensibill.web.ui.WebUiActivity

class DirectKotlinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_direct_kotlin)

        startActivity(Intent(this, WebUiActivity::class.java))
    }
}
