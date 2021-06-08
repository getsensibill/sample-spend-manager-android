package com.getsensibill.spendmanager.demo.direct

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.getsensibill.spendmanager.demo.R
import com.getsensibill.web.data.configuration.NavigationIntent
import com.getsensibill.web.data.configuration.ProgrammaticTheme
import com.getsensibill.web.data.models.Brand
import com.getsensibill.web.ui.WebUiActivity

/**
 * The most direct way of integrating with Spend Manager.
 * <p>
 * Much more limited means of customization. We are directly calling [WebUiActivity] and letting
 * the default behaviour take place. You can pass in an [ARG_NAVIGATION_OVERRIDE] to override
 * the default navigation intent, and or an [ARG_PROGRAMMATIC_THEME_OVERRIDE] to override the
 * default theme.
 */
class DirectKotlinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_direct_kotlin)

        startSpendManager()
    }

    private fun startSpendManager() {
        startActivity(Intent(this, WebUiActivity::class.java))
    }

    private fun startSpendManagerWithArguments() {
        val intent = Intent(this, WebUiActivity::class.java).apply {
            // Pass in a navigation override. Defaults as .DASHBOARD
            putExtra(WebUiActivity.ARG_NAVIGATION_OVERRIDE, NavigationIntent.DASHBOARD)
            // Pass in a custom Theme override. Defaults to null
            putExtra(WebUiActivity.ARG_PROGRAMMATIC_THEME_OVERRIDE, ProgrammaticTheme(Brand()))
        }
        startActivity(intent)
    }
}
