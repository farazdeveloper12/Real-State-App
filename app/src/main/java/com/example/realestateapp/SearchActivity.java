package com.example.realestateapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText searchEditText;
    private RecyclerView searchResultsRecyclerView;
    private ChipGroup filterChipGroup;
    private PropertyAdapter propertyAdapter;
    private List<Property> allProperties = new ArrayList<>();
    private List<Property> filteredProperties = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search Properties");

        searchEditText = findViewById(R.id.searchEditText);
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView);
        filterChipGroup = findViewById(R.id.filterChipGroup);

        findViewById(R.id.filterButton).setOnClickListener(v -> showFilterDialog());

        setupSearchView();
        setupRecyclerView();
        loadProperties();
        setupFilterChips();
    }

    private void setupSearchView() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProperties(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupRecyclerView() {
        propertyAdapter = new PropertyAdapter(filteredProperties);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchResultsRecyclerView.setAdapter(propertyAdapter);
    }

    private void loadProperties() {
        // Load sample properties
        allProperties.add(new Property("Luxury Apartment in DHA", "PKR 15,000,000", "DHA Phase 6, Karachi", "https://via.placeholder.com/300x200"));
        allProperties.add(new Property("Modern House in Bahria Town", "PKR 25,000,000", "Bahria Town, Lahore", "https://via.placeholder.com/300x200"));
        allProperties.add(new Property("Commercial Plaza", "PKR 50,000,000", "Blue Area, Islamabad", "https://via.placeholder.com/300x200"));

        filteredProperties.addAll(allProperties);
        propertyAdapter.notifyDataSetChanged();
    }

    private void setupFilterChips() {
        String[] filters = {"Houses", "Apartments", "Commercial", "For Sale", "For Rent"};
        for (String filter : filters) {
            Chip chip = new Chip(this);
            chip.setText(filter);
            chip.setCheckable(true);
            chip.setChipBackgroundColorResource(R.color.chip_background);
            chip.setTextColor(getResources().getColor(R.color.chip_text_color));
            filterChipGroup.addView(chip);
        }
    }

    private void filterProperties(String query) {
        filteredProperties.clear();
        for (Property property : allProperties) {
            if (property.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    property.getLocation().toLowerCase().contains(query.toLowerCase())) {
                filteredProperties.add(property);
            }
        }
        propertyAdapter.notifyDataSetChanged();
    }

    private void showFilterDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.dialog_filter);

        RangeSlider priceSlider = dialog.findViewById(R.id.priceRangeSlider);
        ChipGroup propertyTypeChips = dialog.findViewById(R.id.propertyTypeChips);

        dialog.findViewById(R.id.applyFilterButton).setOnClickListener(v -> {
            // Apply filters
            dialog.dismiss();
        });

        dialog.show();
    }
}
