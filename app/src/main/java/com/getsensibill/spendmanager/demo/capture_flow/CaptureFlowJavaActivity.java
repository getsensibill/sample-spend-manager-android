package com.getsensibill.spendmanager.demo.capture_flow;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.getsensibill.captureflow.coordinator.CaptureFlowCoordinator;
import com.getsensibill.captureflow.coordinator.CaptureFlowState;
import com.getsensibill.core.analytic.DocumentUploadUpdateData;
import com.getsensibill.core.analytic.Transaction;
import com.getsensibill.core.documents.DocumentStatus;
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
                // **ONLY APPLICABLE IF CONFIGURED USING API V1 (RECEIPTS) ENDPOINTS**

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
            } else if (newState instanceof  CaptureFlowState.DocumentUploading) {
                // **ONLY APPLICABLE IF CONFIGURED USING API V2 (DOCUMENTS) ENDPOINTS**

                // Handle document upload progress updates that will occur after the image has been captured and the
                // Document is uploading
                DocumentUploadUpdateData update = ((CaptureFlowState.DocumentUploading) newState).getUpdate();
                DocumentStatus status = update.getStatus();
                text += "DocumentUploading";
                text += "(documentId" + update.getDocumentId();
                text += ", status:" + status;
                text += "):\n";
                text += update.toString();
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
