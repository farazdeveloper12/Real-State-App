package com.example.realestateapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ViewedPropertiesActivity extends AppCompatActivity {

    private RecyclerView viewedPropertiesRecyclerView;
    private TextView emptyStateTextView;
    private List<Property> viewedProperties = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewed_properties);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Recently Viewed");

        // Initialize views
        viewedPropertiesRecyclerView = findViewById(R.id.viewedPropertiesRecyclerView);
        emptyStateTextView = findViewById(R.id.emptyStateTextView);

        // Setup RecyclerView
        viewedPropertiesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load viewed properties
        loadViewedProperties();
    }

    private void loadViewedProperties() {
        // In a real app, you would fetch this data from Firebase or local storage
        // For now, just use sample data
        viewedProperties = getSampleViewedProperties();

        if (viewedProperties.isEmpty()) {
            viewedPropertiesRecyclerView.setVisibility(View.GONE);
            emptyStateTextView.setVisibility(View.VISIBLE);
        } else {
            viewedPropertiesRecyclerView.setVisibility(View.VISIBLE);
            emptyStateTextView.setVisibility(View.GONE);

            PropertyAdapter adapter = new PropertyAdapter(viewedProperties,
                    (property, position) -> {
                        // Open property details when clicked
                        // In a real app, you'd navigate to PropertyDetailActivity
                    });
            viewedPropertiesRecyclerView.setAdapter(adapter);
        }
    }

    private List<Property> getSampleViewedProperties() {
        List<Property> properties = new ArrayList<>();

        // Add some sample viewed properties
        properties.add(new Property(
                "Modern Apartment in F-7",
                "PKR 12,500,000",
                "F-7 Markaz, Islamabad",
                "https://via.placeholder.com/300x200"
        ));

        properties.add(new Property(
                "Luxury Villa in DHA",
                "PKR 35,000,000",
                "DHA Phase 5, Lahore",
                "https://via.placeholder.com/300x200"
        ));

        properties.add(new Property(
                "Commercial Plaza for Sale",
                "PKR 85,000,000",
                "Blue Area, Islamabad",
                "https://via.placeholder.com/300x200"
        ));

        return properties;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}