package com.example.realestateapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailEditText;
    private Button resetPasswordButton;
    private TextView backToLoginLink;
    private ConstraintLayout successMessageContainer;
    private TextView successMessageText;
    private FirebaseAuth mAuth;
    private static final String TAG = "ForgotPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_up, R.anim.fade_out);
        setContentView(R.layout.activity_forgot_password);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        emailEditText = findViewById(R.id.emailEditText);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        backToLoginLink = findViewById(R.id.backToLoginLink);
        successMessageContainer = findViewById(R.id.successMessageContainer);
        successMessageText = findViewById(R.id.successMessageText);

        // Verify all views are initialized
        if (emailEditText == null || resetPasswordButton == null || backToLoginLink == null ||
                successMessageContainer == null || successMessageText == null) {
            Log.e(TAG, "onCreate: One or more UI components are null");
            Toast.makeText(this, "UI initialization error", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Pre-fill email if passed from LoginActivity
        String email = getIntent().getStringExtra("email");
        if (email != null && !email.isEmpty()) {
            emailEditText.setText(email);
        }

        // Reset Password button click listener
        resetPasswordButton.setOnClickListener(v -> {
            String emailText = emailEditText.getText().toString().trim();

            if (emailText.isEmpty()) {
                Toast.makeText(ForgotPasswordActivity.this, "Please enter your email to reset password", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.sendPasswordResetEmail(emailText)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Show success message with animation
                            showSuccessMessage("Password reset email sent");
                            // Navigate back to LoginActivity after a short delay
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                                overridePendingTransition(R.anim.fade_in, R.anim.slide_down);
                                finish();
                            }, 1500); // Delay to show the message for 1.5 seconds
                        } else {
                            Log.e(TAG, "Failed to send reset email", task.getException());
                            Toast.makeText(ForgotPasswordActivity.this, "Failed to send reset email: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // Back to Login link click listener
        backToLoginLink.setOnClickListener(v -> {
            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.slide_down);
            finish();
        });
    }

    private void showSuccessMessage(String message) {
        successMessageText.setText(message);
        successMessageContainer.setVisibility(View.VISIBLE);

        // Load and apply enter animation
        Animation enterAnimation = AnimationUtils.loadAnimation(this, R.anim.success_message_enter);
        successMessageContainer.startAnimation(enterAnimation);

        // Hide the message after a delay with exit animation
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Animation exitAnimation = AnimationUtils.loadAnimation(this, R.anim.success_message_exit);
            exitAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    successMessageContainer.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            successMessageContainer.startAnimation(exitAnimation);
        }, 1000); // Show the message for 1 second
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.slide_down);
    }
}