package com.example.realestateapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 123;

    // UI Components
    private ImageView profileImage;
    private TextInputEditText nameEditText, emailEditText, phoneEditText, locationEditText, bioEditText;
    private TextInputLayout nameInputLayout, emailInputLayout, phoneInputLayout, locationInputLayout, bioInputLayout;
    private MaterialButton saveButton, cancelButton;
    private FloatingActionButton changePictureButton;
    private MaterialCardView profileImageCard;
    private TextView uploadPhotoLabel;

    // Firebase
    private FirebaseAuth mAuth;
    private Uri selectedImageUri = null;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private boolean hasUnsavedChanges = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();

        setupToolbar();
        initializeViews();
        setupPermissionLauncher();
        setupImagePicker();
        loadUserData();
        setupInputValidation();
        setupButtons();
        handleBackPress();

        // Start entry animations
        animateEntryTransition();
    }

    /**
     * Set up the toolbar
     */
    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");

        // Make sure back button navigates correctly
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    /**
     * Initialize all UI components
     */
    private void initializeViews() {
        profileImage = findViewById(R.id.profileImageEdit);
        profileImageCard = findViewById(R.id.profileImageCard);
        changePictureButton = findViewById(R.id.changePictureButton);
        uploadPhotoLabel = findViewById(R.id.uploadPhotoLabel);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        locationEditText = findViewById(R.id.locationEditText);
        bioEditText = findViewById(R.id.bioEditText);

        nameInputLayout = findViewById(R.id.nameInputLayout);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        phoneInputLayout = findViewById(R.id.phoneInputLayout);
        locationInputLayout = findViewById(R.id.locationInputLayout);
        bioInputLayout = findViewById(R.id.bioInputLayout);

        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
    }

    /**
     * Set up permission launcher for storage access
     */
    private void setupPermissionLauncher() {
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Permission granted, launch image picker
                        launchImagePicker();
                    } else {
                        // Permission denied, show rationale or settings dialog
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            showPermissionExplanationDialog();
                        } else {
                            // User checked "Don't ask again", direct them to settings
                            showSettingsDialog();
                        }
                    }
                }
        );
    }

    /**
     * Set up the image picker functionality
     */
    private void setupImagePicker() {
        changePictureButton.setOnClickListener(v -> {
            checkStoragePermission();
        });

        profileImageCard.setOnClickListener(v -> {
            checkStoragePermission();
        });

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        hasUnsavedChanges = true;

                        if (selectedImageUri != null) {
                            Glide.with(this)
                                    .load(selectedImageUri)
                                    .circleCrop()
                                    .into(profileImage);

                            // Show success feedback
                            changePictureButton.hide();
                            changePictureButton.setImageResource(R.drawable.ic_check);
                            changePictureButton.show();

                            // Update helper text
                            uploadPhotoLabel.setText(R.string.photo_selected);
                            uploadPhotoLabel.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                        }
                    }
                }
        );
    }

    /**
     * Check and request storage permission using the new permission launcher
     */
    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted
            launchImagePicker();
        } else {
            // Request permission
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    /**
     * Show a dialog explaining why we need storage permission
     */
    private void showPermissionExplanationDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Permission Required")
                .setMessage("We need storage access to select a profile picture. Please grant this permission.")
                .setPositiveButton("Grant Permission", (dialog, which) -> {
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Show a dialog directing user to app settings to manually grant permission
     */
    private void showSettingsDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Permission Required")
                .setMessage("Storage permission is required to select a profile picture. Please enable it in app settings.")
                .setPositiveButton("Settings", (dialog, which) -> {
                    // Open app settings
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Launch the image picker intent
     */
    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Load user information with fade in animation
            nameEditText.setText(user.getDisplayName());
            emailEditText.setText(user.getEmail());
            emailEditText.setEnabled(false); // Email cannot be changed

            // We'd load these from a database in a real app
            // This is just for demonstration
            phoneEditText.setText("");
            locationEditText.setText("");
            bioEditText.setText("");

            // Load profile image with Glide if available
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl())
                        .placeholder(R.drawable.ic_person)
                        .circleCrop()
                        .into(profileImage);
            }
        }
    }

    private void setupInputValidation() {
        // Name validation
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hasUnsavedChanges = true;
                validateName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Phone validation
        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hasUnsavedChanges = true;
                validatePhone(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Additional field listeners to track changes
        locationEditText.addTextChangedListener(new SimpleTextWatcher(() -> hasUnsavedChanges = true));
        bioEditText.addTextChangedListener(new SimpleTextWatcher(() -> hasUnsavedChanges = true));
    }

    private boolean validateName(String name) {
        if (name.trim().isEmpty()) {
            nameInputLayout.setError("Name cannot be empty");
            return false;
        } else if (name.length() < 3) {
            nameInputLayout.setError("Name is too short");
            return false;
        } else {
            nameInputLayout.setError(null);
            return true;
        }
    }

    private boolean validatePhone(String phone) {
        if (phone.trim().isEmpty()) {
            // Phone is optional, so no error
            phoneInputLayout.setError(null);
            return true;
        } else if (phone.length() < 10) {
            phoneInputLayout.setError("Enter a valid phone number");
            return false;
        } else {
            phoneInputLayout.setError(null);
            return true;
        }
    }

    private void setupButtons() {
        // Cancel button returns to previous screen
        cancelButton.setOnClickListener(v -> {
            if (hasUnsavedChanges) {
                showDiscardChangesDialog();
            } else {
                finishWithAnimation();
            }
        });

        // Save button saves user data
        saveButton.setOnClickListener(v -> {
            if (validateAllInputs()) {
                saveButton.setEnabled(false);
                showLoading(true);
                updateProfile();
            }
        });
    }

    private boolean validateAllInputs() {
        boolean isNameValid = validateName(Objects.requireNonNull(nameEditText.getText()).toString());
        boolean isPhoneValid = validatePhone(Objects.requireNonNull(phoneEditText.getText()).toString());

        // Could add validation for other fields here

        return isNameValid && isPhoneValid;
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            // Show loading state
            saveButton.setText(R.string.loading);
            saveButton.setEnabled(false);
            cancelButton.setEnabled(false);
        } else {
            // Reset button
            saveButton.setText(R.string.save_changes);
            saveButton.setEnabled(true);
            cancelButton.setEnabled(true);
        }
    }

    private void updateProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String newName = Objects.requireNonNull(nameEditText.getText()).toString().trim();
            String newPhone = Objects.requireNonNull(phoneEditText.getText()).toString().trim();
            String newLocation = Objects.requireNonNull(locationEditText.getText()).toString().trim();
            String newBio = Objects.requireNonNull(bioEditText.getText()).toString().trim();

            // Builder for profile update
            UserProfileChangeRequest.Builder profileUpdatesBuilder = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newName);

            if (selectedImageUri != null) {
                profileUpdatesBuilder.setPhotoUri(selectedImageUri);
            }

            UserProfileChangeRequest profileUpdates = profileUpdatesBuilder.build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // In a real app, you would save the other details to a database
                            saveAdditionalUserInfo(user.getUid(), newPhone, newLocation, newBio);

                            // Show success animation
                            showLoading(false);
                            showSuccessDialog();
                        } else {
                            showLoading(false);
                            saveButton.setEnabled(true);

                            Snackbar.make(
                                    saveButton,
                                    "Failed to update profile: " + (task.getException() != null ?
                                            task.getException().getMessage() : "Unknown error"),
                                    Snackbar.LENGTH_LONG
                            ).show();
                        }
                    });
        }
    }

    private void saveAdditionalUserInfo(String userId, String phone, String location, String bio) {
        // In a real app, you would save this to Firebase Firestore or Realtime Database
        // For this example, we're just pretending it's saved
    }

    /**
     * Show a better designed success dialog
     */
    private void showSuccessDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_success, null);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setCancelable(false);

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        // Find continue button and set click listener
        MaterialButton continueButton = dialogView.findViewById(R.id.continueButton);
        continueButton.setOnClickListener(v -> {
            dialog.dismiss();
            finishWithAnimation();
        });
    }

    private void handleBackPress() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (hasUnsavedChanges) {
                    showDiscardChangesDialog();
                } else {
                    finishWithAnimation();
                }
            }
        });
    }

    private void showDiscardChangesDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Discard Changes")
                .setMessage("You have unsaved changes. Are you sure you want to discard them?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Discard", (dialog, which) -> finishWithAnimation())
                .show();
    }

    private void finishWithAnimation() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void animateEntryTransition() {
        // Simple alpha animation for all views
        View[] views = new View[] {
                profileImage,
                uploadPhotoLabel,
                nameInputLayout,
                emailInputLayout,
                phoneInputLayout,
                locationInputLayout,
                bioInputLayout,
                cancelButton,
                saveButton
        };

        for (int i = 0; i < views.length; i++) {
            View view = views[i];
            view.setAlpha(0f);
            view.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .setStartDelay(100 + (i * 50))
                    .start();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (hasUnsavedChanges) {
            showDiscardChangesDialog();
        } else {
            finishWithAnimation();
        }
    }

    // Simple TextWatcher that only implements the necessary method
    private static class SimpleTextWatcher implements TextWatcher {
        private final Runnable action;

        SimpleTextWatcher(Runnable action) {
            this.action = action;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            action.run();
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }
}