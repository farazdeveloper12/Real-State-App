package com.example.realestateapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "LoginActivity";
    private Handler handler;

    // Success message view
    private View successMessageView;
    private LottieAnimationView successAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starting LoginActivity");
        try {
            overridePendingTransition(R.anim.slide_up, R.anim.fade_out);
            setContentView(R.layout.activity_login);
        } catch (Exception e) {
            Log.e(TAG, "onCreate: Failed to set content view", e);
            Toast.makeText(this, "Error initializing UI: " + (e.getMessage() != null ? e.getMessage() : "Unknown error"), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Initialize Firebase Auth
        try {
            mAuth = FirebaseAuth.getInstance();
        } catch (Exception e) {
            Log.e(TAG, "onCreate: Failed to initialize FirebaseAuth", e);
            Toast.makeText(this, "Error initializing authentication: " + (e.getMessage() != null ? e.getMessage() : "Unknown error"), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Configure Google Sign-In
        try {
            GoogleSignInOptions.Builder builder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN);
            builder.requestIdToken("319889492180-1o24ajnaofnnluahdik0boa389fos7g8.apps.googleusercontent.com");
            builder.requestEmail();
            GoogleSignInOptions gso = builder.build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            // Sign out any existing Google account to force account selection
            mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> Log.d(TAG, "Signed out from previous Google account"));
        } catch (Exception e) {
            Log.e(TAG, "onCreate: Failed to configure Google Sign-In", e);
            Toast.makeText(this, "Error configuring Google Sign-In: " + (e.getMessage() != null ? e.getMessage() : "Unknown error"), Toast.LENGTH_LONG).show();
        }

        // Initialize ActivityResultLauncher for Google Sign-In
        ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.d(TAG, "Google Sign-In result code: " + result.getResultCode());
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data == null) {
                            Log.e(TAG, "Google Sign-In intent data is null");
                            Toast.makeText(LoginActivity.this, "Google Sign-In failed: No data returned", Toast.LENGTH_LONG).show();
                            return;
                        }
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            Log.d(TAG, "Google Sign-In successful, account: " + account.getEmail());
                            firebaseAuthWithGoogle(account);
                        } catch (ApiException e) {
                            Log.e(TAG, "Google Sign-In failed, status code: " + e.getStatusCode() + ", message: " + (e.getMessage() != null ? e.getMessage() : "Unknown error"), e);
                            Toast.makeText(LoginActivity.this, "Google Sign-In failed: " + (e.getMessage() != null ? e.getMessage() : "Unknown error"), Toast.LENGTH_LONG).show();
                        }
                    } else if (result.getResultCode() == RESULT_CANCELED) {
                        Log.w(TAG, "Google Sign-In canceled by user or system");
                        Toast.makeText(LoginActivity.this, "Google Sign-In canceled", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Google Sign-In failed with result code: " + result.getResultCode());
                        Toast.makeText(LoginActivity.this, "Google Sign-In failed with result code: " + result.getResultCode(), Toast.LENGTH_LONG).show();
                    }
                });

        // Initialize UI components as local variables
        final EditText emailEditText = findViewById(R.id.emailEditText);
        final EditText passwordEditText = findViewById(R.id.passwordEditText);
        final Button loginButton = findViewById(R.id.loginButton);
        final TextView signUpLink = findViewById(R.id.signUpLink);
        final TextView forgotPasswordLink = findViewById(R.id.forgotPasswordLink);
        final ImageButton googleLoginButton = findViewById(R.id.googleLoginButton);
        final ImageButton facebookLoginButton = findViewById(R.id.facebookLoginButton);
        final ImageButton twitterLoginButton = findViewById(R.id.twitterLoginButton);
        final ImageButton togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);

        // Check if critical views are missing
        if (emailEditText == null || passwordEditText == null || loginButton == null) {
            Log.e(TAG, "onCreate: Critical UI components are missing, cannot proceed");
            Toast.makeText(this, "Critical UI initialization error", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Initialize Handler
        handler = new Handler(Looper.getMainLooper());

        // Inflate the success message layout with the parent view
        ViewGroup rootView = findViewById(android.R.id.content);
        successMessageView = getLayoutInflater().inflate(R.layout.login_success_message, rootView, false);
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
                public void onAnimationStart(android.animation.Animator animation) {
                    Log.d(TAG, "Lottie animation started");
                }

                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    Log.d(TAG, "Lottie animation ended");
                }

                @Override
                public void onAnimationCancel(android.animation.Animator animation) {
                    Log.d(TAG, "Lottie animation canceled");
                }

                @Override
                public void onAnimationRepeat(android.animation.Animator animation) {
                    Log.d(TAG, "Lottie animation repeated");
                }
            });
        } else {
            Log.e(TAG, "SuccessAnimation is null, cannot play animation");
        }

        // Toggle password visibility
        boolean passwordVisible = false;
        if (togglePasswordVisibility != null) {
            final boolean[] finalPasswordVisible = {passwordVisible};
            togglePasswordVisibility.setOnClickListener(v -> {
                finalPasswordVisible[0] = !finalPasswordVisible[0];
                if (finalPasswordVisible[0]) {
                    // Show password
                    passwordEditText.setTransformationMethod(null);
                    togglePasswordVisibility.setImageResource(R.drawable.ic_lock_open);
                } else {
                    // Hide password
                    passwordEditText.setTransformationMethod(new PasswordTransformationMethod());
                    togglePasswordVisibility.setImageResource(R.drawable.ic_lock);
                }
                // Move cursor to the end of text
                passwordEditText.setSelection(passwordEditText.getText().length());
            });
        }

        // Login button click listener
        loginButton.setOnClickListener(v -> {
            Log.d(TAG, "Login button clicked");
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Log.w(TAG, "Email or password is empty");
                Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Firebase authentication successful for email/password login");

                            // Add the success message view to the activity's root layout
                            if (successMessageView.getParent() != null) {
                                ((ViewGroup) successMessageView.getParent()).removeView(successMessageView);
                            }
                            rootView.addView(successMessageView);

                            // Play the Lottie animation
                            if (successAnimation != null) {
                                Log.d(TAG, "Playing Lottie animation");
                                successAnimation.playAnimation();
                            } else {
                                Log.e(TAG, "SuccessAnimation is null during login success");
                            }

                            // Navigate to OnboardingActivity after a delay to allow the animation to play
                            handler.postDelayed(() -> {
                                Log.d(TAG, "Attempting to navigate to OnboardingActivity after delay");
                                try {
                                    if (!isFinishing()) {
                                        Intent intent = new Intent(LoginActivity.this, OnboardingActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.fade_in, R.anim.slide_down);
                                        finish();
                                        Log.d(TAG, "Successfully navigated to OnboardingActivity");
                                    } else {
                                        Log.w(TAG, "Activity is finishing, cannot start OnboardingActivity");
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error navigating to OnboardingActivity", e);
                                    Toast.makeText(LoginActivity.this, "Navigation error: " + (e.getMessage() != null ? e.getMessage() : "Unknown error"), Toast.LENGTH_LONG).show();
                                }
                            }, 2000); // Delay of 2 seconds to allow animation to complete
                        } else {
                            Log.e(TAG, "Login failed", task.getException());
                            Toast.makeText(LoginActivity.this, "Login failed: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Sign Up link click listener
        if (signUpLink != null) {
            signUpLink.setOnClickListener(v -> {
                Log.d(TAG, "Sign Up link clicked");
                try {
                    startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                    overridePendingTransition(R.anim.fade_in, R.anim.slide_down);
                } catch (Exception e) {
                    Log.e(TAG, "Error navigating to SignUpActivity", e);
                    Toast.makeText(this, "Navigation error", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Forgot Password link click listener
        if (forgotPasswordLink != null) {
            forgotPasswordLink.setOnClickListener(v -> {
                Log.d(TAG, "Forgot Password link clicked");
                try {
                    Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                    intent.putExtra("email", emailEditText.getText().toString().trim());
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.slide_down);
                } catch (Exception e) {
                    Log.e(TAG, "Error navigating to ForgotPasswordActivity", e);
                    Toast.makeText(this, "Navigation error", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Google Login button click listener
        if (googleLoginButton != null) {
            googleLoginButton.setOnClickListener(v -> {
                Log.d(TAG, "Initiating Google Sign-In");
                try {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    googleSignInLauncher.launch(signInIntent);
                } catch (Exception e) {
                    Log.e(TAG, "Error launching Google Sign-In", e);
                    Toast.makeText(this, "Error launching Google Sign-In", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Facebook Login button click listener
        if (facebookLoginButton != null) {
            facebookLoginButton.setOnClickListener(v -> {
                Toast.makeText(LoginActivity.this, "Facebook Login clicked", Toast.LENGTH_SHORT).show();
                // Implement Facebook login logic here
            });
        }

        // Twitter Login button click listener
        if (twitterLoginButton != null) {
            twitterLoginButton.setOnClickListener(v -> {
                Toast.makeText(LoginActivity.this, "Twitter Login clicked", Toast.LENGTH_SHORT).show();
                // Implement Twitter login logic here
            });
        }

        // Handle back press using OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d(TAG, "onBackPressed: Back button pressed");
                try {
                    LoginActivity.this.overridePendingTransition(R.anim.fade_in, R.anim.slide_down);
                    setEnabled(false); // Disable this callback
                    getOnBackPressedDispatcher().onBackPressed(); // Proceed with default back press behavior
                } catch (Exception e) {
                    Log.e(TAG, "Error handling back press", e);
                    finish(); // Just finish if there's an error
                }
            }
        });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "Authenticating with Firebase using Google account: " + acct.getEmail());
        try {
            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Firebase authentication successful for Google Sign-In");

                            // Add the success message view to the activity's root layout
                            ViewGroup rootView = findViewById(android.R.id.content);
                            if (successMessageView.getParent() != null) {
                                ((ViewGroup) successMessageView.getParent()).removeView(successMessageView);
                            }
                            rootView.addView(successMessageView);

                            // Play the Lottie animation
                            if (successAnimation != null) {
                                Log.d(TAG, "Playing Lottie animation for Google Sign-In");
                                successAnimation.playAnimation();
                            } else {
                                Log.e(TAG, "SuccessAnimation is null during Google Sign-In success");
                            }

                            // Navigate to DashboardActivity after a delay to allow the animation to play
                            handler.postDelayed(() -> {
                                Log.d(TAG, "Attempting to navigate to DashboardActivity after Google Sign-In delay");
                                try {
                                    if (!isFinishing()) {
                                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.fade_in, R.anim.slide_down);
                                        finish();
                                        Log.d(TAG, "Successfully navigated to DashboardActivity after Google Sign-In");
                                    } else {
                                        Log.w(TAG, "Activity is finishing, cannot start DashboardActivity after Google Sign-In");
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error navigating to DashboardActivity after Google Sign-In", e);
                                    Toast.makeText(LoginActivity.this, "Navigation error", Toast.LENGTH_LONG).show();
                                }
                            }, 2000); // Delay of 2 seconds to allow animation to complete
                        } else {
                            Log.e(TAG, "Firebase authentication failed for Google Sign-In", task.getException());
                            Toast.makeText(LoginActivity.this, "Google Authentication failed", Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error in firebaseAuthWithGoogle", e);
            Toast.makeText(this, "Authentication error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Cleaning up LoginActivity");

        // Remove any pending callbacks to prevent memory leaks
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            Log.d(TAG, "onDestroy: Removed all handler callbacks");
        }
    }
}