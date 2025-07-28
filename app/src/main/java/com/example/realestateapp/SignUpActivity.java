package com.example.realestateapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private MaterialButton signUpButton;
    private FirebaseAuth mAuth;
    private static final String TAG = "SignUpActivity";
    private Handler handler;

    // Success message view
    private View successMessageView;
    private LottieAnimationView successAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        handler = new Handler(Looper.getMainLooper());
        initializeViews();
        setupListeners();

        // Handle back press using OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d(TAG, "onBackPressed: Back button pressed");
                try {
                    SignUpActivity.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    setEnabled(false); // Disable this callback
                    getOnBackPressedDispatcher().onBackPressed(); // Proceed with default back press behavior
                } catch (Exception e) {
                    Log.e(TAG, "Error handling back press", e);
                    finish(); // Just finish if there's an error
                }
            }
        });
    }

    private void initializeViews() {
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        signUpButton = findViewById(R.id.signUpButton);

        // Inflate the success message layout with the parent view
        ViewGroup rootView = findViewById(android.R.id.content);
        successMessageView = getLayoutInflater().inflate(R.layout.signup_success_message, rootView, false);
        successAnimation = successMessageView.findViewById(R.id.successAnimation);
        if (successAnimation != null) {
            Log.d(TAG, "SuccessAnimation found, setting visibility to VISIBLE");
            successAnimation.setVisibility(View.VISIBLE);
            // Check if the animation resource is loaded
            try {
                successAnimation.setAnimation(R.raw.check_animation);
                Log.d(TAG, "Lottie animation resource loaded successfully");
            } catch (Exception e) {
                Log.e(TAG, "Failed to load Lottie animation resource: " + e.getMessage());
                // Fallback: Set a static tick icon if the animation fails to load
                successAnimation.setImageResource(android.R.drawable.checkbox_on_background);
            }
            // Add a listener to check if the animation is loaded and played
            successAnimation.addAnimatorListener(new android.animation.Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull android.animation.Animator animation) {
                    Log.d(TAG, "Lottie animation started");
                }

                @Override
                public void onAnimationEnd(@NonNull android.animation.Animator animation) {
                    Log.d(TAG, "Lottie animation ended");
                }

                @Override
                public void onAnimationCancel(@NonNull android.animation.Animator animation) {
                    Log.d(TAG, "Lottie animation canceled");
                }

                @Override
                public void onAnimationRepeat(@NonNull android.animation.Animator animation) {
                    Log.d(TAG, "Lottie animation repeated");
                }
            });
        } else {
            Log.e(TAG, "SuccessAnimation is null, cannot play animation");
        }
    }

    private void setupListeners() {
        signUpButton.setOnClickListener(v -> attemptSignUp());

        findViewById(R.id.loginLink).setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }

    private void attemptSignUp() {
        String name = nameEditText.getText() != null ? nameEditText.getText().toString().trim() : "";
        String email = emailEditText.getText() != null ? emailEditText.getText().toString().trim() : "";
        String password = passwordEditText.getText() != null ? passwordEditText.getText().toString().trim() : "";
        String confirmPassword = confirmPasswordEditText.getText() != null ? confirmPasswordEditText.getText().toString().trim() : "";

        if (validateInput(name, email, password, confirmPassword)) {
            signUpButton.setEnabled(false);
            createAccount(name, email, password);
        }
    }

    private boolean validateInput(String name, String email, String password, String confirmPassword) {
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Name is required");
            return false;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Valid email is required");
            return false;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return false;
        }

        return true;
    }

    private void createAccount(String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    signUpButton.setEnabled(true);
                    if (task.isSuccessful()) {
                        // Update user profile with name
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(profileTask -> {
                                        if (profileTask.isSuccessful()) {
                                            showSuccessMessage();
                                        } else {
                                            String errorMessage = profileTask.getException() != null
                                                    ? profileTask.getException().getMessage()
                                                    : "Unknown error";
                                            Log.e(TAG, "Failed to update user profile: " + errorMessage);
                                            Toast.makeText(SignUpActivity.this,
                                                    "Profile update failed: " + errorMessage,
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });
                        } else {
                            Log.e(TAG, "Current user is null after successful signup");
                            Toast.makeText(SignUpActivity.this,
                                    "Sign up failed: User not found",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        String errorMessage = task.getException() != null
                                ? task.getException().getMessage()
                                : "Unknown error";
                        Log.e(TAG, "Sign up failed: " + errorMessage);
                        Toast.makeText(SignUpActivity.this,
                                "Sign up failed: " + errorMessage,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showSuccessMessage() {
        // Add the success message view to the activity's root layout
        ViewGroup rootView = findViewById(android.R.id.content);
        if (successMessageView.getParent() != null) {
            ViewGroup parent = (ViewGroup) successMessageView.getParent();
            parent.removeView(successMessageView);
        }
        rootView.addView(successMessageView);

        // Play the Lottie animation
        if (successAnimation != null) {
            Log.d(TAG, "Playing Lottie animation");
            successAnimation.playAnimation();
        } else {
            Log.e(TAG, "SuccessAnimation is null during signup success");
        }

        // Navigate to OnboardingActivity after a delay to allow the animation to play
        handler.postDelayed(() -> {
            Log.d(TAG, "Attempting to navigate to OnboardingActivity after delay");
            try {
                if (!isFinishing()) {
                    Intent intent = new Intent(SignUpActivity.this, OnboardingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                    Log.d(TAG, "Successfully navigated to OnboardingActivity");
                } else {
                    Log.w(TAG, "Activity is finishing, cannot start OnboardingActivity");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error navigating to OnboardingActivity", e);
                Toast.makeText(SignUpActivity.this,
                        "Navigation error: " + (e.getMessage() != null ? e.getMessage() : "Unknown error"),
                        Toast.LENGTH_LONG).show();
            }
        }, 2000); // Delay of 2 seconds to allow animation to complete
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Cleaning up SignUpActivity");

        // Remove any pending callbacks to prevent memory leaks
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            Log.d(TAG, "onDestroy: Removed all handler callbacks");
        }
    }
}