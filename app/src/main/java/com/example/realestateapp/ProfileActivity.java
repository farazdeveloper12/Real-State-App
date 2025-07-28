package com.example.realestateapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextView nameText, emailText, memberSinceText, userRoleText;
    private TextView savedPropertiesCount, searchesCount, viewsCount;
    private CardView documentsCard, helpCard;
    private LinearLayout savedStatsContainer, searchesStatsContainer, viewsStatsContainer;
    private MaterialButton editProfileButton;
    private ImageView settingsButton;
    private CollapsingToolbarLayout collapsingToolbar;

    private FirebaseAuth mAuth;

    private SharedPreferences statsPreferences;
    private static final String PREFS_NAME = "RealEstateStats";
    private static final String KEY_SAVED = "saved_properties_count";
    private static final String KEY_SEARCHES = "searches_count";
    private static final String KEY_VIEWS = "views_count";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        statsPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        initViews();
        setupToolbar();
        loadUserData();
        setupClickListeners();
        setupBackPressHandler();
    }

    private void initViews() {
        profileImage = findViewById(R.id.profileImage);
        nameText = findViewById(R.id.userName);
        emailText = findViewById(R.id.userEmail);
        memberSinceText = findViewById(R.id.memberSince);
        userRoleText = findViewById(R.id.userRole);
        collapsingToolbar = findViewById(R.id.collapsingToolbar);

        savedPropertiesCount = findViewById(R.id.savedCount);
        searchesCount = findViewById(R.id.searchesCount);
        viewsCount = findViewById(R.id.viewsCount);

        savedStatsContainer = findViewById(R.id.savedContainer);
        searchesStatsContainer = findViewById(R.id.searchesContainer);
        viewsStatsContainer = findViewById(R.id.viewsContainer);

        documentsCard = findViewById(R.id.documentsCard);
        helpCard = findViewById(R.id.helpCard);

        editProfileButton = findViewById(R.id.editProfileButton);
        settingsButton = findViewById(R.id.settingsButton);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        if (collapsingToolbar != null) {
            collapsingToolbar.setTitle("My Profile");
            collapsingToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent, null));
            collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.white, null));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String displayName = user.getDisplayName();

            nameText.setAlpha(0f);
            nameText.setText(displayName != null ? displayName : "User");
            nameText.animate().alpha(1f).setDuration(500).start();

            emailText.setAlpha(0f);
            emailText.setText(user.getEmail());
            emailText.animate().alpha(1f).setDuration(500).setStartDelay(100).start();

            SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
            String memberSince = "Member since: " + (user.getMetadata() != null && user.getMetadata().getCreationTimestamp() > 0 ?
                    sdf.format(new Date(user.getMetadata().getCreationTimestamp())) : "April 2023");

            memberSinceText.setAlpha(0f);
            memberSinceText.setText(memberSince);
            memberSinceText.animate().alpha(1f).setDuration(500).setStartDelay(200).start();

            userRoleText.setAlpha(0f);
            userRoleText.animate().alpha(1f).setDuration(500).setStartDelay(150).start();

            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(R.drawable.ic_person)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(profileImage);
            } else {
                profileImage.setImageResource(R.drawable.ic_person);
                profileImage.setAlpha(0f);
                profileImage.animate().alpha(1f).setDuration(500).start();
            }

            loadUserStatistics();
        }
    }

    private void loadUserStatistics() {
        int savedCount = statsPreferences.getInt(KEY_SAVED, 12);
        int searchesCount = statsPreferences.getInt(KEY_SEARCHES, 25);
        int viewsCount = statsPreferences.getInt(KEY_VIEWS, 48);

        animateCounterText(savedPropertiesCount, 0, savedCount);
        animateCounterText(this.searchesCount, 0, searchesCount);
        animateCounterText(this.viewsCount, 0, viewsCount);
    }

    private int incrementStat(String key) {
        int currentCount = statsPreferences.getInt(key, 0);
        int newCount = currentCount + 1;

        SharedPreferences.Editor editor = statsPreferences.edit();
        editor.putInt(key, newCount);
        editor.apply();

        return newCount;
    }

    private void animateCounterText(final TextView textView, int start, int end) {
        android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofInt(start, end);
        animator.setDuration(1000);
        animator.addUpdateListener(animation -> textView.setText(String.valueOf(animation.getAnimatedValue())));
        animator.start();
    }

    private void setupClickListeners() {
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,
                    Pair.create(profileImage, "profileImage")
            );
            startActivity(intent, options.toBundle());
        });

        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityWithAnimation(intent);
        });

        savedStatsContainer.setOnClickListener(v -> {
            Intent intent = new Intent(this, PropertyListActivity.class);
            intent.putExtra("MODE", "SAVED");
            startActivityWithAnimation(intent);
        });

        searchesStatsContainer.setOnClickListener(v -> {
            int newCount = incrementStat(KEY_SEARCHES);
            animateCounterText(searchesCount, Integer.parseInt(searchesCount.getText().toString()), newCount);
            Intent intent = new Intent(this, SearchActivity.class);
            startActivityWithAnimation(intent);
        });

        viewsStatsContainer.setOnClickListener(v -> {
            int newCount = incrementStat(KEY_VIEWS);
            animateCounterText(viewsCount, Integer.parseInt(viewsCount.getText().toString()), newCount);
            Intent intent = new Intent(this, PropertyListActivity.class);
            intent.putExtra("MODE", "VIEWED");
            startActivityWithAnimation(intent);
        });

        documentsCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, DocumentsActivity.class);
            startActivityWithAnimation(intent);
        });

        helpCard.setOnClickListener(v -> showHelpDialog());
    }

    private void showHelpDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Help & Support")
                .setMessage("This is the Real Estate App help center. " +
                        "For assistance, please contact support@realestateapp.com or " +
                        "call our helpline at +92-123-4567890.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void startActivityWithAnimation(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}