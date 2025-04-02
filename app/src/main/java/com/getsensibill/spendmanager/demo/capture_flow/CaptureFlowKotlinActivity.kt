package com.getsensibill.spendmanager.demo.capture_flow

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.getsensibill.captureflow.coordinator.CaptureFlowCoordinator
import com.getsensibill.captureflow.coordinator.CaptureFlowState
import com.getsensibill.spendmanager.demo.databinding.ActivityCaptureFlowKotlinBinding
import com.getsensibill.spendmanager.demo.databinding.ActivityFragmentKotlinBinding
import java.util.*

/**
 * Capture Flow Integration
 *
 * This is an example on how you can track the state changing during a capture flow of a receipt
 * using the [CaptureFlowCoordinator].
 */
class CaptureFlowKotlinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCaptureFlowKotlinBinding

    // Creates a capture flow Coordinator
    private val captureFlow = CaptureFlowCoordinator(this)

    // Setup the capture flow listener which is used to listen to the state changing
    private val captureFlowListener: CaptureFlowCoordinator.CaptureFlowListener =
        object : CaptureFlowCoordinator.CaptureFlowListener {

            override fun onCaptureFlowUpdate(newState: CaptureFlowState, externalAccountTransactionId: String?) {
                val text = when (newState) {
                    is CaptureFlowState.ImagesCaptured -> "Images are captured"
                    is CaptureFlowState.FLOW_CANCELLED -> "Capture flow cancelled"
                    is CaptureFlowState.Error -> "Error occurred: ${newState.exception.message}"
                    is CaptureFlowState.DocumentUploading -> {
                        val update = with(newState.update) {
                            "status:$status\nlocalId:$localId\ndocumentId:$documentId\naccountId:$accountId\nextTxnId:$externalAccountTransactionId"
                        }
                        "Uploading\n$update\n(savedExtTxnId:$externalAccountTransactionId)"
                    }
                    is CaptureFlowState.Transacting -> {
                        "Deprecated and replaced with DocumentUploading"
                    }
                }
                binding.progressText.appendOnNewLine("${Date()}: $text")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCaptureFlowKotlinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.launchCapture.setOnClickListener {
            // launches the capture flow, supplying the captureFlowListener so that we can
            // capture and print out the state changes
            captureFlow.launchCaptureFlow(captureFlowListener, externalAccountTransactionId = "testTxnId1")
        }
    }

    /**
     * Helper function for appending new lines for the example
     */
    @SuppressLint("SetTextI18n")
    private fun TextView.appendOnNewLine(newText: CharSequence) {
        runOnUiThread { text = "$text\n$newText" }
    }
}
