package com.example.realestateapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.snackbar.Snackbar;

public class SettingsActivity extends AppCompatActivity {

    private SwitchMaterial darkModeSwitch;
    private SwitchMaterial notificationsSwitch;
    private SwitchMaterial biometricAuthSwitch;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

        preferences = getSharedPreferences("app_settings", MODE_PRIVATE);

        initializeViews();
        loadSettings();
        setupSettingActions();
    }

    private void initializeViews() {
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        notificationsSwitch = findViewById(R.id.notificationsSwitch);
        biometricAuthSwitch = findViewById(R.id.biometricAuthSwitch);

        // Additional cards
        CardView securityCard = findViewById(R.id.securityCard);
        CardView privacyCard = findViewById(R.id.privacyCard);
        CardView languageCard = findViewById(R.id.languageCard);
        CardView aboutCard = findViewById(R.id.aboutCard);
    }

    private void loadSettings() {
        // Load saved preferences
        boolean isDarkMode = preferences.getBoolean("dark_mode", false);
        boolean areNotificationsEnabled = preferences.getBoolean("notifications_enabled", true);
        boolean isBiometricEnabled = preferences.getBoolean("biometric_enabled", false);

        darkModeSwitch.setChecked(isDarkMode);
        notificationsSwitch.setChecked(areNotificationsEnabled);
        biometricAuthSwitch.setChecked(isBiometricEnabled);
    }

    private void setupSettingActions() {
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save preference
            preferences.edit().putBoolean("dark_mode", isChecked).apply();

            // Apply theme change
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            // Show confirmation
            Snackbar.make(darkModeSwitch, isChecked ? "Dark mode enabled" : "Light mode enabled", Snackbar.LENGTH_SHORT).show();
        });

        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save preference
            preferences.edit().putBoolean("notifications_enabled", isChecked).apply();

            // Show confirmation
            Snackbar.make(notificationsSwitch, isChecked ? "Notifications enabled" : "Notifications disabled", Snackbar.LENGTH_SHORT).show();
        });

        biometricAuthSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save preference
            preferences.edit().putBoolean("biometric_enabled", isChecked).apply();

            // Show confirmation
            Snackbar.make(biometricAuthSwitch, isChecked ? "Biometric authentication enabled" : "Biometric authentication disabled", Snackbar.LENGTH_SHORT).show();
        });

        // Setup other setting cards
        findViewById(R.id.securityCard).setOnClickListener(v ->
                Snackbar.make(v, "Security settings will be available soon", Snackbar.LENGTH_SHORT).show());

        findViewById(R.id.privacyCard).setOnClickListener(v ->
                Snackbar.make(v, "Privacy settings will be available soon", Snackbar.LENGTH_SHORT).show());

        findViewById(R.id.languageCard).setOnClickListener(v ->
                Snackbar.make(v, "Language settings will be available soon", Snackbar.LENGTH_SHORT).show());

        findViewById(R.id.aboutCard).setOnClickListener(v ->
                Snackbar.make(v, "App version 1.0.0", Snackbar.LENGTH_SHORT).show());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        return true;
    }
}