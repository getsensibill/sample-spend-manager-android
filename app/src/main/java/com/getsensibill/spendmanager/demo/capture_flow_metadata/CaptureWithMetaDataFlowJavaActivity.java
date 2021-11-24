package com.getsensibill.spendmanager.demo.capture_flow_metadata;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.getsensibill.capturestandalone.models.CaptureConfig;
import com.getsensibill.capturestandalone.models.FlashMode;
import com.getsensibill.web.ui.CaptureWithMetadataFlowActivity;

import org.jetbrains.annotations.Nullable;

/**
 * Capture With Metadata Flow Integration
 *
 * This is an example on how you use the capture with metadata flow
 */
public class CaptureWithMetaDataFlowJavaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@androidx.annotation.Nullable @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = new Intent(this, CaptureWithMetadataFlowActivity.class);

        // If you wish to override the default capture config
        final CaptureConfig captureConfig = new CaptureConfig(
                true,
                true,
                FlashMode.FLASH_MODE_OFF,
                true,
                true,
                false,
                true,
                3,
                false,
                true,
                false,
                true,
                true,
                true
        );
        intent.putExtra(CaptureWithMetadataFlowActivity.ARG_CAPTURE_CONFIG, captureConfig);

        // If the receipt should be attached to some external transaction
        final String externalAccountTransactionId = "someId";
        intent.putExtra(
                CaptureWithMetadataFlowActivity.ARG_EXTERNAL_ACCOUNT_TRANSACTION_ID,
                externalAccountTransactionId
        );

        startActivity(intent);
    }
}
