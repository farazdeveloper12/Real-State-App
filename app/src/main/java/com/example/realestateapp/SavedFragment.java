package com.example.realestateapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.ArrayList;
import java.util.List;

public class SavedFragment extends Fragment {

    private MaterialToolbar toolbar;
    private RecyclerView savedPropertiesRecyclerView;
    private PropertyAdapter propertyAdapter;
    private List<Property> savedProperties;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_saved_properties, container, false);

        // Initialize UI components
        toolbar = view.findViewById(R.id.toolbar);
        savedPropertiesRecyclerView = view.findViewById(R.id.savedPropertiesRecyclerView);

        // Set up toolbar
        if (toolbar != null) {
            toolbar.setTitle("Saved Properties");
            toolbar.setNavigationOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
        }

        // Initialize RecyclerView
        savedProperties = new ArrayList<>();
        // Add some dummy data (replace with actual saved properties from database)
        savedProperties.add(new Property("Luxury Villa", "PKR 50,000,000", "DHA Phase 6, Karachi", "https://via.placeholder.com/300x200"));
        savedProperties.add(new Property("Modern Apartment", "PKR 15,000,000", "Gulberg, Lahore", "https://via.placeholder.com/300x200"));
        propertyAdapter = new PropertyAdapter(savedProperties);
        savedPropertiesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        savedPropertiesRecyclerView.setAdapter(propertyAdapter);

        return view;
    }
}
