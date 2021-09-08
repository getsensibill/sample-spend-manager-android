package com.getsensibill.spendmanager.demo.capture_flow;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.getsensibill.captureflow.coordinator.CaptureFlowCoordinator;
import com.getsensibill.captureflow.coordinator.CaptureFlowState;
import com.getsensibill.capturestandalone.models.CaptureConfig;
import com.getsensibill.core.analytic.Transaction;
import com.getsensibill.spendmanager.demo.databinding.ActivityCaptureFlowJavaBinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

/**
 * Capture Flow Integration
 *
 * This is an example on how you can track the state changing during a capture flow of a receipt
 * using the [CaptureFlowCoordinator].
 */
public class CaptureFlowJavaActivity extends AppCompatActivity {
    private ActivityCaptureFlowJavaBinding binding;

    // Creates a capture flow Coordinator
    private CaptureFlowCoordinator captureFlow = new CaptureFlowCoordinator(this);

    // Setup the capture flow listener which is used to listen to the state changing
    private CaptureFlowCoordinator.CaptureFlowListener captureFlowListener = new CaptureFlowCoordinator.CaptureFlowListener() {
        @Override
        public void onCaptureFlowUpdate(@NotNull CaptureFlowState newState,
                                        @Nullable String externalAccountTransactionId) {
            String text = "\n " + new Date().toString() + ": ";

            if (newState instanceof CaptureFlowState.ImagesCaptured) {
                // Handle the state that images have just been captured
                text += "Images are captured";

            } else if (newState instanceof CaptureFlowState.FLOW_CANCELLED) {
                // Handle the user cancelling the flow
                text += "Capture flow cancelled";

            } else if (newState instanceof CaptureFlowState.Error) {
                // Handle an error occurring during the flow
                final CaptureFlowState.Error errorState = (CaptureFlowState.Error) newState;
                final Exception error = errorState.getException();
                text += "Error occurred: " + error.getMessage();

            } else if (newState instanceof  CaptureFlowState.Transacting) {
                // Handle receipt upload transaction updates that will occur after the image has been captured
                // and the receipt is uploading
                final CaptureFlowState.Transacting transactingState = ((CaptureFlowState.Transacting) newState);
                final Transaction transaction = transactingState.getTransaction();

                text += "Transacting";
                text += "\nstatus: " + transaction.getStatus().toString();
                text += "\nnlocalId: " + transaction.getLocalId();
                text += "\nntxnId: " + transaction.getTransactionId();
                text += "\nnreceiptId: " + transaction.getReceiptId();
                text += "\n(savedExtTxnId: " + externalAccountTransactionId + ")";
            }

            String finalText = text;
            runOnUiThread(() -> {
                binding.progressText.append(finalText);
            });
        }
    };

    @Override
    protected void onCreate(@androidx.annotation.Nullable @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCaptureFlowJavaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.launchCapture.setOnClickListener(view ->
                captureFlow.launchCaptureFlow(
                        captureFlowListener));
    }
}
