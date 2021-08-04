package com.getsensibill.spendmanager.demo.direct;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.getsensibill.spendmanager.demo.R;
import com.getsensibill.web.data.configuration.NavigationIntent;
import com.getsensibill.web.data.configuration.WebTheme;
import com.getsensibill.web.data.models.Brand;
import com.getsensibill.web.ui.WebUiActivity;

/**
 * The most direct way of integrating with Spend Manager.
 * <p>
 * Much more limited means of customization. We are directly calling [WebUiActivity] and letting
 * the default behaviour take place. You can pass in an [ARG_NAVIGATION_OVERRIDE] to override
 * the default navigation intent, and or an [ARG_WEB_THEME_OVERRIDE] to override the
 * default theme.
 */
public class DirectJavaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_java);

        startSpendManager();
    }

    private void startSpendManager() {
        startActivity(new Intent(this, WebUiActivity.class));
    }

    private void startSpendManagerWithArguments() {
        Intent intent = new Intent(this, WebUiActivity.class);

        // Pass in a navigation override. Defaults as .DASHBOARD
        intent.putExtra(
                WebUiActivity.ARG_NAVIGATION_OVERRIDE,
                NavigationIntent.DASHBOARD.INSTANCE
        );

        // Pass in a custom Theme override. Defaults to null
        intent.putExtra(
                WebUiActivity.ARG_WEB_THEME_OVERRIDE,
                new WebTheme(new Brand())
        );

        startActivity(intent);
    }
}
