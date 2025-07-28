package com.example.realestateapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.ArrayList;
import java.util.List;

public class SavedPropertiesActivity extends AppCompatActivity {

    private RecyclerView savedPropertiesRecyclerView;
    private PropertyAdapter propertyAdapter;
    private List<Property> savedProperties = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_properties);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Saved Properties");

        savedPropertiesRecyclerView = findViewById(R.id.savedPropertiesRecyclerView);
        savedPropertiesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Add some dummy data (replace with actual saved properties from database)
        savedProperties.add(new Property("Luxury Villa", "PKR 50,000,000", "DHA Phase 6, Karachi", "https://via.placeholder.com/300x200"));
        savedProperties.add(new Property("Modern Apartment", "PKR 15,000,000", "Gulberg, Lahore", "https://via.placeholder.com/300x200"));

        propertyAdapter = new PropertyAdapter(savedProperties);
        savedPropertiesRecyclerView.setAdapter(propertyAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
