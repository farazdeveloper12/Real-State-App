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

public class SearchHistoryActivity extends AppCompatActivity {

    private RecyclerView searchHistoryRecyclerView;
    private TextView emptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_history);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search History");

        // Initialize views
        searchHistoryRecyclerView = findViewById(R.id.searchHistoryRecyclerView);
        emptyStateTextView = findViewById(R.id.emptyStateTextView);

        // Set up RecyclerView
        searchHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load search history
        loadSearchHistory();
    }

    private void loadSearchHistory() {
        // In a real app, you would fetch search history from Firebase
        // For now, just display some sample data
        List<SearchHistoryItem> historyItems = getSampleSearchHistory();

        if (historyItems.isEmpty()) {
            searchHistoryRecyclerView.setVisibility(View.GONE);
            emptyStateTextView.setVisibility(View.VISIBLE);
        } else {
            searchHistoryRecyclerView.setVisibility(View.VISIBLE);
            emptyStateTextView.setVisibility(View.GONE);

            SearchHistoryAdapter adapter = new SearchHistoryAdapter(historyItems);
            searchHistoryRecyclerView.setAdapter(adapter);
        }
    }

    private List<SearchHistoryItem> getSampleSearchHistory() {
        List<SearchHistoryItem> items = new ArrayList<>();
        items.add(new SearchHistoryItem("Houses in DHA Phase 6", "April 25, 2024"));
        items.add(new SearchHistoryItem("2 Bedroom Apartment", "April 24, 2024"));
        items.add(new SearchHistoryItem("Properties near F-7 Markaz", "April 23, 2024"));
        items.add(new SearchHistoryItem("Luxury Villas in Bahria Town", "April 22, 2024"));
        items.add(new SearchHistoryItem("Commercial Plazas in Blue Area", "April 21, 2024"));
        return items;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // Simple data class for search history items
    static class SearchHistoryItem {
        String query;
        String date;

        SearchHistoryItem(String query, String date) {
            this.query = query;
            this.date = date;
        }
    }

    // Adapter for search history items
    class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder> {

        private List<SearchHistoryItem> items;

        SearchHistoryAdapter(List<SearchHistoryItem> items) {
            this.items = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_search_history, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            SearchHistoryItem item = items.get(position);
            holder.queryTextView.setText(item.query);
            holder.dateTextView.setText(item.date);

            // Set click listener to perform the search again
            holder.itemView.setOnClickListener(v -> {
                // In a real app, you'd perform the search again
                // For now, just show a message
                android.widget.Toast.makeText(SearchHistoryActivity.this,
                        "Searching for: " + item.query,
                        android.widget.Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView queryTextView;
            TextView dateTextView;

            ViewHolder(View itemView) {
                super(itemView);
                queryTextView = itemView.findViewById(R.id.queryTextView);
                dateTextView = itemView.findViewById(R.id.dateTextView);
            }
        }
    }
}