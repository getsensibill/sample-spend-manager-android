package com.getsensibill.spendmanager.demo.capture_flow_metadata

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.getsensibill.capturestandalone.config.CaptureConfig
import com.getsensibill.web.ui.CaptureWithMetadataFlowActivity

/**
 * Capture With Metadata Flow Integration
 *
 * This is an example on how you use the capture with metadata flow
 */
class CaptureWithMetaDataFlowKotlinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, CaptureWithMetadataFlowActivity::class.java)

        // copies the default capture config, disables long capture for demo
        val captureConfig = CaptureConfig.defaultReceiptCaptureConfig.copy(enableLongCapture = false)

        intent.putExtra(CaptureWithMetadataFlowActivity.ARG_CAPTURE_CONFIG, captureConfig)

        // If the receipt should be attached to some external transaction
        val externalAccountTransactionId = "someId"
        intent.putExtra(
            CaptureWithMetadataFlowActivity.ARG_EXTERNAL_ACCOUNT_TRANSACTION_ID,
            externalAccountTransactionId
        )

        startActivity(intent)
    }
}
