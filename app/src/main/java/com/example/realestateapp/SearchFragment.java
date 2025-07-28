package com.example.realestateapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private MaterialToolbar toolbar;
    private EditText searchEditText;
    private ImageButton filterButton;
    private ChipGroup filterChipGroup;
    private RecyclerView searchResultsRecyclerView;
    private LinearLayout emptyState;
    private PropertyAdapter propertyAdapter;
    private List<Property> searchResults;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_search, container, false);

        // Initialize UI components
        toolbar = view.findViewById(R.id.toolbar);
        searchEditText = view.findViewById(R.id.searchEditText);
        filterButton = view.findViewById(R.id.filterButton);
        filterChipGroup = view.findViewById(R.id.filterChipGroup);
        searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView);
        emptyState = view.findViewById(R.id.emptyState);

        // Initialize RecyclerView
        searchResults = new ArrayList<>();
        propertyAdapter = new PropertyAdapter(searchResults);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchResultsRecyclerView.setAdapter(propertyAdapter);

        // Set up toolbar
        if (toolbar != null) {
            toolbar.setTitle("Search");
            toolbar.setNavigationOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
        }

        // Set up search functionality
        if (searchEditText != null) {
            searchEditText.setOnEditorActionListener((v, actionId, event) -> {
                String query = searchEditText.getText().toString().trim();
                performSearch(query);
                return true;
            });
        }

        // Set up filter button
        if (filterButton != null) {
            filterButton.setOnClickListener(v -> {
                // Implement filter functionality (e.g., show filter dialog)
            });
        }

        return view;
    }

    private void performSearch(String query) {
        // Simulate search results (replace with actual search logic)
        searchResults.clear();
        if (query.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            searchResultsRecyclerView.setVisibility(View.GONE);
        } else {
            // Add sample properties based on query
            searchResults.add(new Property("Apartment in " + query, "AED 1,000,000", query, "https://via.placeholder.com/300x200"));
            searchResults.add(new Property("Villa in " + query, "AED 2,000,000", query, "https://via.placeholder.com/300x200"));
            propertyAdapter.notifyDataSetChanged();
            emptyState.setVisibility(View.GONE);
            searchResultsRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}
