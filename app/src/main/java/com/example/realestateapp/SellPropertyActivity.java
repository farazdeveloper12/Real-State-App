package com.example.realestateapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class SellPropertyActivity extends AppCompatActivity {

    private TextInputLayout titleLayout, priceLayout, addressLayout, descriptionLayout;
    private EditText titleInput, priceInput, addressInput, descriptionInput;
    private Spinner propertyTypeSpinner, bedroomsSpinner;
    private Button addPhotoButton, submitButton;
    private ImageView propertyImage;
    private Uri selectedImageUri = null;

    private ActivityResultLauncher<Intent> photoPickerLauncher;
    private List<Uri> propertyImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_property);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("List Your Property");

        initializeViews();
        setupSpinners();
        setupPhotoSelection();
    }

    private void initializeViews() {
        // Find views
        titleLayout = findViewById(R.id.titleLayout);
        priceLayout = findViewById(R.id.priceLayout);
        addressLayout = findViewById(R.id.addressLayout);
        descriptionLayout = findViewById(R.id.descriptionLayout);

        titleInput = findViewById(R.id.titleInput);
        priceInput = findViewById(R.id.priceInput);
        addressInput = findViewById(R.id.addressInput);
        descriptionInput = findViewById(R.id.descriptionInput);

        propertyTypeSpinner = findViewById(R.id.propertyTypeSpinner);
        bedroomsSpinner = findViewById(R.id.bedroomsSpinner);

        addPhotoButton = findViewById(R.id.addPhotoButton);
        submitButton = findViewById(R.id.submitButton);
        propertyImage = findViewById(R.id.propertyImage);

        // Setup submit button
        submitButton.setOnClickListener(v -> {
            if (validateInputs()) {
                submitProperty();
            }
        });
    }

    private void setupSpinners() {
        // Property type spinner
        ArrayAdapter<CharSequence> propertyTypeAdapter = ArrayAdapter.createFromResource(
                this, R.array.property_types, android.R.layout.simple_spinner_item);
        propertyTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        propertyTypeSpinner.setAdapter(propertyTypeAdapter);

        // Bedrooms spinner
        ArrayAdapter<CharSequence> bedroomsAdapter = ArrayAdapter.createFromResource(
                this, R.array.bedrooms_options, android.R.layout.simple_spinner_item);
        bedroomsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bedroomsSpinner.setAdapter(bedroomsAdapter);
    }

    private void setupPhotoSelection() {
        // Setup photo picker launcher
        photoPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        propertyImage.setImageURI(selectedImageUri);
                        propertyImage.setVisibility(View.VISIBLE);
                        propertyImages.add(selectedImageUri);
                    }
                }
        );

        // Setup add photo button
        addPhotoButton.setOnClickListener(v -> {
            // Check permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            } else {
                openGallery();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoPickerLauncher.launch(intent);
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Validate title
        if (titleInput.getText().toString().trim().isEmpty()) {
            titleLayout.setError("Please enter a title");
            isValid = false;
        } else {
            titleLayout.setError(null);
        }

        // Validate price
        if (priceInput.getText().toString().trim().isEmpty()) {
            priceLayout.setError("Please enter a price");
            isValid = false;
        } else {
            priceLayout.setError(null);
        }

        // Validate address
        if (addressInput.getText().toString().trim().isEmpty()) {
            addressLayout.setError("Please enter an address");
            isValid = false;
        } else {
            addressLayout.setError(null);
        }

        // Check if at least one image is selected
        if (propertyImages.isEmpty()) {
            Snackbar.make(submitButton, "Please add at least one photo", Snackbar.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private void submitProperty() {
        // Here you would normally upload the property data to your server
        // For now, just show a success message and finish the activity

        Toast.makeText(this, "Property submitted successfully!", Toast.LENGTH_SHORT).show();

        // Go back to dashboard after submission
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}