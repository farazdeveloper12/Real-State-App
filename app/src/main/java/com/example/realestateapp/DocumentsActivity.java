package com.example.realestateapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DocumentsActivity extends AppCompatActivity {

    private RecyclerView documentsRecyclerView;
    private FloatingActionButton fabAddDocument;
    private DocumentsAdapter documentsAdapter;
    private List<Document> documentsList = new ArrayList<>();
    private ActivityResultLauncher<String> documentPickerLauncher;
    private View emptyStateView;
    private CardView documentTypeCard;
    private LinearLayout identityDocumentOption, propertyDocumentOption, paymentDocumentOption;
    private MaterialButton addFirstDocumentButton, cancelDocTypeButton;
    private String selectedDocumentType = "";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);

        mAuth = FirebaseAuth.getInstance();

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.documents_title);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        initializeViews();
        setupDocumentPicker();
        setupRecyclerView();
        setupClickListeners();
        checkForEmptyState();
    }

    private void initializeViews() {
        documentsRecyclerView = findViewById(R.id.documentsRecyclerView);
        fabAddDocument = findViewById(R.id.fabAddDocument);
        emptyStateView = findViewById(R.id.emptyStateView);
        documentTypeCard = findViewById(R.id.documentTypeCard);

        identityDocumentOption = findViewById(R.id.identityDocumentOption);
        propertyDocumentOption = findViewById(R.id.propertyDocumentOption);
        paymentDocumentOption = findViewById(R.id.paymentDocumentOption);

        addFirstDocumentButton = findViewById(R.id.addFirstDocumentButton);
        cancelDocTypeButton = findViewById(R.id.cancelDocTypeButton);
    }

    private void setupDocumentPicker() {
        documentPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        uploadDocument(uri);
                    } else {
                        hideDocumentTypeSelector();
                    }
                }
        );
    }

    private void setupRecyclerView() {
        documentsAdapter = new DocumentsAdapter(documentsList, this::showDocumentDetails);
        documentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        documentsRecyclerView.setAdapter(documentsAdapter);

        loadUserDocuments();
    }

    private void setupClickListeners() {
        fabAddDocument.setOnClickListener(v -> showDocumentTypeSelector());

        addFirstDocumentButton.setOnClickListener(v -> showDocumentTypeSelector());

        identityDocumentOption.setOnClickListener(v -> {
            selectedDocumentType = "ID";
            documentPickerLauncher.launch("*/*");
        });

        propertyDocumentOption.setOnClickListener(v -> {
            selectedDocumentType = "Property";
            documentPickerLauncher.launch("*/*");
        });

        paymentDocumentOption.setOnClickListener(v -> {
            selectedDocumentType = "Payment";
            documentPickerLauncher.launch("*/*");
        });

        cancelDocTypeButton.setOnClickListener(v -> hideDocumentTypeSelector());
    }

    private void showDocumentTypeSelector() {
        documentTypeCard.setVisibility(View.VISIBLE);
        documentTypeCard.setAlpha(0f);
        documentTypeCard.setTranslationY(200f);

        documentTypeCard.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .start();

        fabAddDocument.hide();
    }

    private void hideDocumentTypeSelector() {
        documentTypeCard.animate()
                .alpha(0f)
                .translationY(200f)
                .setDuration(300)
                .withEndAction(() -> {
                    documentTypeCard.setVisibility(View.GONE);
                    fabAddDocument.show();
                })
                .start();
    }

    private void loadUserDocuments() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && documentsList.isEmpty()) {
            documentsList.add(new Document("National ID Card", "ID", "1.2MB", "2024-01-15"));
            documentsList.add(new Document("Property Agreement", "Property", "2.5MB", "2024-02-10"));
            documentsList.add(new Document("Payment Receipt", "Payment", "1.0MB", "2024-03-05"));
            documentsAdapter.notifyItemRangeInserted(0, documentsList.size());
        }
        checkForEmptyState();
    }

    private void uploadDocument(Uri uri) {
        fabAddDocument.setEnabled(false);

        String fileName = getFileNameFromUri(uri);

        final String finalDocumentName;
        switch (selectedDocumentType) {
            case "ID":
                finalDocumentName = "ID Document: " + fileName;
                break;
            case "Property":
                finalDocumentName = "Property Document: " + fileName;
                break;
            case "Payment":
                finalDocumentName = "Payment Document: " + fileName;
                break;
            default:
                finalDocumentName = fileName;
                break;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        Snackbar.make(documentsRecyclerView, "Uploading document...", Snackbar.LENGTH_SHORT).show();

        final String finalSelectedDocumentType = selectedDocumentType;
        new android.os.Handler().postDelayed(() -> {
            if ("ID".equals(finalSelectedDocumentType)) {
                Snackbar.make(documentsRecyclerView, "Scanning ID document...", Snackbar.LENGTH_SHORT).show();

                new android.os.Handler().postDelayed(() -> {
                    Document newDoc = new Document(finalDocumentName, finalSelectedDocumentType, "1.5MB", currentDate);
                    documentsList.add(0, newDoc);
                    documentsAdapter.notifyItemInserted(0);

                    hideDocumentTypeSelector();
                    Snackbar.make(documentsRecyclerView, "ID document scanned and uploaded successfully", Snackbar.LENGTH_SHORT).show();
                    checkForEmptyState();
                    fabAddDocument.setEnabled(true);
                    updateDocumentCount();
                    showDocumentDetails(documentsList.get(0));
                }, 2000);
            } else {
                Document newDoc = new Document(finalDocumentName, finalSelectedDocumentType, "1.5MB", currentDate);
                documentsList.add(0, newDoc);
                documentsAdapter.notifyItemInserted(0);

                hideDocumentTypeSelector();
                Snackbar.make(documentsRecyclerView, R.string.document_upload_success, Snackbar.LENGTH_SHORT).show();
                checkForEmptyState();
                fabAddDocument.setEnabled(true);
                updateDocumentCount();
            }
        }, 1500);
    }

    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            String[] projection = { android.provider.MediaStore.Images.Media.DISPLAY_NAME };
            try (android.database.Cursor cursor = getContentResolver().query(uri, projection, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.DISPLAY_NAME);
                    result = cursor.getString(columnIndex);
                }
            } catch (Exception e) {
                Log.e("DocumentsActivity", "Error getting file name from URI", e);
            }
        }
        if (result == null) {
            result = uri.getPath();
            if (result != null) {
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
        }
        return result != null ? result : getString(R.string.new_document);
    }

    private void showDocumentDetails(Document document) {
        Intent intent = new Intent(this, DocumentViewActivity.class);
        intent.putExtra("DOCUMENT_TITLE", document.getTitle());
        intent.putExtra("DOCUMENT_TYPE", document.getType());
        intent.putExtra("DOCUMENT_SIZE", document.getSize());
        intent.putExtra("DOCUMENT_DATE", document.getDate());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void updateDocumentCount() {
        try {
            android.content.SharedPreferences prefs = getSharedPreferences("RealEstateStats", MODE_PRIVATE);
            int currentCount = prefs.getInt("documents_count", 3);
            android.content.SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("documents_count", currentCount + 1);
            editor.apply();
        } catch (Exception e) {
            Log.e("DocumentsActivity", "Error updating document count", e);
        }
    }

    private void checkForEmptyState() {
        if (documentsList.isEmpty()) {
            documentsRecyclerView.setVisibility(View.GONE);
            emptyStateView.setVisibility(View.VISIBLE);
        } else {
            documentsRecyclerView.setVisibility(View.VISIBLE);
            emptyStateView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (documentTypeCard.getVisibility() == View.VISIBLE) {
            hideDocumentTypeSelector();
        } else {
            super.onBackPressed();
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }
}