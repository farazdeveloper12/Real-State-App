package com.example.realestateapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.List;

public class HelpCenterActivity extends AppCompatActivity {

    private RecyclerView faqRecyclerView;
    private View faqSection, contactSection, tutorialSection, feedbackSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_center);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.help_center_title);
        }

        initializeViews();
        setupFAQSection();
        setupSections();
    }

    private void initializeViews() {
        faqRecyclerView = findViewById(R.id.faqRecyclerView);
        faqSection = findViewById(R.id.faqSection);
        contactSection = findViewById(R.id.contactSection);
        tutorialSection = findViewById(R.id.tutorialSection);
        feedbackSection = findViewById(R.id.feedbackSection);

        CardView faqCard = findViewById(R.id.faqCard);
        CardView contactSupportCard = findViewById(R.id.contactSupportCard);
        CardView tutorialsCard = findViewById(R.id.tutorialsCard);
        CardView feedbackCard = findViewById(R.id.feedbackCard);

        // Set up card click listeners
        faqCard.setOnClickListener(v -> toggleSection(faqSection));
        contactSupportCard.setOnClickListener(v -> toggleSection(contactSection));
        tutorialsCard.setOnClickListener(v -> toggleSection(tutorialSection));
        feedbackCard.setOnClickListener(v -> toggleSection(feedbackSection));

        findViewById(R.id.aiChatButton).setOnClickListener(v -> {
            startActivity(new Intent(this, AIChatActivity.class));
            overridePendingTransition(R.anim.slide_in_up, R.anim.fade_out);
        });
    }

    private void toggleSection(View section) {
        // Hide all sections first
        faqSection.setVisibility(View.GONE);
        contactSection.setVisibility(View.GONE);
        tutorialSection.setVisibility(View.GONE);
        feedbackSection.setVisibility(View.GONE);

        // Show selected section
        section.setVisibility(View.VISIBLE);

        // Scroll to the section
        section.requestFocus();
    }

    private void setupFAQSection() {
        // Setup FAQ RecyclerView
        List<FAQItem> faqItems = new ArrayList<>();
        faqItems.add(new FAQItem(getString(R.string.faq_search_question),
                getString(R.string.faq_search_answer)));
        faqItems.add(new FAQItem(getString(R.string.faq_save_question),
                getString(R.string.faq_save_answer)));
        faqItems.add(new FAQItem(getString(R.string.faq_contact_question),
                getString(R.string.faq_contact_answer)));
        faqItems.add(new FAQItem(getString(R.string.faq_list_question),
                getString(R.string.faq_list_answer)));
        faqItems.add(new FAQItem(getString(R.string.faq_payment_question),
                getString(R.string.faq_payment_answer)));

        FAQAdapter faqAdapter = new FAQAdapter(faqItems);
        faqRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        faqRecyclerView.setAdapter(faqAdapter);
    }

    private void setupSections() {
        // Setup Contact Support section
        findViewById(R.id.emailSupportButton).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:support@realestateapp.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_email_subject));

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Snackbar.make(v, R.string.no_email_app, Snackbar.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.callSupportButton).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:+1234567890"));

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Snackbar.make(v, R.string.no_phone_app, Snackbar.LENGTH_SHORT).show();
            }
        });

        // Setup Tutorials section
        findViewById(R.id.tutorial1Card).setOnClickListener(v -> {
            Snackbar.make(v, getString(R.string.tutorial_opening, getString(R.string.tutorial_getting_started)), Snackbar.LENGTH_SHORT).show();
            // In a real app, you would open a video or tutorial screen
        });

        findViewById(R.id.tutorial2Card).setOnClickListener(v -> {
            Snackbar.make(v, getString(R.string.tutorial_opening, getString(R.string.tutorial_finding_properties)), Snackbar.LENGTH_SHORT).show();
            // In a real app, you would open a video or tutorial screen
        });

        findViewById(R.id.tutorial3Card).setOnClickListener(v -> {
            Snackbar.make(v, getString(R.string.tutorial_opening, getString(R.string.tutorial_managing_documents)), Snackbar.LENGTH_SHORT).show();
            // In a real app, you would open a video or tutorial screen
        });

        // Setup Feedback section
        findViewById(R.id.submitFeedbackButton).setOnClickListener(v -> {
            String feedback = ((EditText) findViewById(R.id.feedbackEditText)).getText().toString().trim();
            if (feedback.isEmpty()) {
                Snackbar.make(v, R.string.feedback_empty_error, Snackbar.LENGTH_SHORT).show();
            } else {
                // In a real app, you would send the feedback to your server
                Snackbar.make(v, R.string.feedback_success, Snackbar.LENGTH_LONG).show();
                ((EditText) findViewById(R.id.feedbackEditText)).setText("");
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}