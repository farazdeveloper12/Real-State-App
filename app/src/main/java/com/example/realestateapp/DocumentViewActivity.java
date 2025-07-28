package com.example.realestateapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

public class DocumentViewActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextView documentTitleText, documentTypeText, documentSizeText, documentDateText;
    private ImageView documentIcon;
    private MaterialButton downloadButton, shareButton, deleteButton;
    private CardView documentInfoCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_view);

        // Initialize views
        initializeViews();

        // Setup toolbar
        setupToolbar();

        // Load document details
        loadDocumentDetails();

        // Setup button click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        documentTitleText = findViewById(R.id.documentTitle);
        documentTypeText = findViewById(R.id.documentType);
        documentSizeText = findViewById(R.id.documentSize);
        documentDateText = findViewById(R.id.documentDate);
        documentIcon = findViewById(R.id.documentIcon);
        downloadButton = findViewById(R.id.downloadButton);
        shareButton = findViewById(R.id.shareButton);
        deleteButton = findViewById(R.id.deleteButton);
        documentInfoCard = findViewById(R.id.documentInfoCard);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Document Details");

        // Handle back button properly
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadDocumentDetails() {
        // Get document details from intent
        String title = getIntent().getStringExtra("DOCUMENT_TITLE");
        String type = getIntent().getStringExtra("DOCUMENT_TYPE");
        String size = getIntent().getStringExtra("DOCUMENT_SIZE");
        String date = getIntent().getStringExtra("DOCUMENT_DATE");

        // Set document details
        documentTitleText.setText(title);
        documentTypeText.setText("Type: " + type);
        documentSizeText.setText("Size: " + size);
        documentDateText.setText("Date: " + date);

        // Set icon based on document type
        if (type != null) {
            if (type.equals("ID")) {
                documentIcon.setImageResource(R.drawable.ic_identity_card);
                getSupportActionBar().setTitle("ID Document");
            } else if (type.equals("Property")) {
                documentIcon.setImageResource(R.drawable.ic_property_document);
                getSupportActionBar().setTitle("Property Document");
            } else if (type.equals("Payment")) {
                documentIcon.setImageResource(R.drawable.ic_payment_document);
                getSupportActionBar().setTitle("Payment Document");
            } else if (type.equalsIgnoreCase("PDF")) {
                documentIcon.setImageResource(R.drawable.ic_pdf);
                getSupportActionBar().setTitle("PDF Document");
            } else {
                documentIcon.setImageResource(R.drawable.ic_document);
            }
        }

        // Apply animation to card
        animateInfoCard();
    }

    private void animateInfoCard() {
        documentInfoCard.setAlpha(0f);
        documentInfoCard.setTranslationY(50f);
        documentInfoCard.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .start();
    }

    private void setupClickListeners() {
        downloadButton.setOnClickListener(v -> {
            // Simulate document download
            downloadButton.setEnabled(false);
            downloadButton.setText("Downloading...");

            new android.os.Handler().postDelayed(() -> {
                downloadButton.setEnabled(true);
                downloadButton.setText("Download");
                Snackbar.make(documentInfoCard, "Document downloaded successfully", Snackbar.LENGTH_SHORT).show();
            }, 1500);
        });

        shareButton.setOnClickListener(v -> {
            // Simulate document sharing
            android.content.Intent shareIntent = new android.content.Intent();
            shareIntent.setAction(android.content.Intent.ACTION_SEND);
            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, documentTitleText.getText().toString());
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Please find the attached document.");
            shareIntent.setType("text/plain");

            startActivity(android.content.Intent.createChooser(shareIntent, "Share Document via"));
        });

        deleteButton.setOnClickListener(v -> {
            // Show delete confirmation dialog
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                    .setTitle("Delete Document")
                    .setMessage("Are you sure you want to delete this document? This action cannot be undone.")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Delete", (dialog, which) -> {
                        // Simulate document deletion
                        Snackbar.make(documentInfoCard, "Document deleted successfully", Snackbar.LENGTH_SHORT).show();

                        // Close activity after deletion
                        new android.os.Handler().postDelayed(this::finish, 1000);
                    })
                    .show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}