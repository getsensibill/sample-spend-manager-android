package com.getsensibill.spendmanager.demo.direct;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.getsensibill.spendmanager.demo.R;
import com.getsensibill.web.ui.WebUiActivity;

public class DirectJavaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_java);

        startActivity(new Intent(this, WebUiActivity.class));
    }
}
