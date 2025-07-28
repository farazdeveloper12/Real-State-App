package com.example.realestateapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DialogHelper {

    public static void showSuccessDialog(Context context, String title, String message, final OnContinueListener listener) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_success);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        // Find views using the correct IDs from your layout
        TextView titleText = dialog.findViewById(R.id.dialogTitle);
        TextView messageText = dialog.findViewById(R.id.dialogMessage);
        Button continueButton = dialog.findViewById(R.id.continueButton);
        ImageView successIcon = dialog.findViewById(R.id.successIcon);

        // Set text
        titleText.setText(title);
        messageText.setText(message);

        // Set up continue button
        continueButton.setOnClickListener(v -> {
            dialog.dismiss();
            if (listener != null) {
                listener.onContinue();
            }
        });

        dialog.show();
    }

    public interface OnContinueListener {
        void onContinue();
    }
}
