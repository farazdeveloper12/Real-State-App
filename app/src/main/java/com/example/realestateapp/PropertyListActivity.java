package com.example.realestateapp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class PropertyListActivity extends AppCompatActivity {

    private RecyclerView propertyRecyclerView;
    private PropertyAdapter propertyAdapter;
    private List<Property> propertyList = new ArrayList<>();
    private String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_list);

        // Get the mode from intent (BUY or RENT)
        mode = getIntent().getStringExtra("MODE");
        if (mode == null) {
            mode = "BUY"; // Default to BUY if not specified
        }

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mode.equals("BUY") ? "Properties for Sale" : "Properties for Rent");

        // Initialize views
        propertyRecyclerView = findViewById(R.id.propertyListRecyclerView);
        TextView emptyView = findViewById(R.id.emptyView);

        // Set up RecyclerView
        propertyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        propertyAdapter = new PropertyAdapter(propertyList, (property, position) -> {
            // Handle property click - open detail view
            // Intent intent = new Intent(PropertyListActivity.this, PropertyDetailActivity.class);
            // intent.putExtra("PROPERTY_ID", property.getId());
            // startActivity(intent);
        });
        propertyRecyclerView.setAdapter(propertyAdapter);

        // Load properties based on mode
        loadProperties();
    }

    private void loadProperties() {
        // In a real app, you would fetch properties from a database or API
        // For now, we'll add dummy data based on the mode
        propertyList.clear();

        if (mode.equals("BUY")) {
            propertyList.add(new Property(
                    "Modern Downtown Apartment",
                    "PKR 9,500,000",
                    "Islamabad, F-7 Markaz",
                    "https://images.unsplash.com/photo-1540518614846-7eded433c457?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60"
            ));

            propertyList.add(new Property(
                    "Luxury Villa with Pool",
                    "PKR 25,000,000",
                    "DHA Phase 5, Lahore",
                    "https://images.unsplash.com/photo-1564013799919-ab600027ffc6?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60"
            ));

            propertyList.add(new Property(
                    "Cozy Family Home",
                    "PKR 7,000,000",
                    "Gulberg, Lahore",
                    "https://images.unsplash.com/photo-1576941089067-2de3c901e126?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60"
            ));
        } else {
            propertyList.add(new Property(
                    "Furnished Apartment",
                    "PKR 45,000/month",
                    "DHA Phase 2, Karachi",
                    "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60"
            ));

            propertyList.add(new Property(
                    "2 Bedroom Flat",
                    "PKR 35,000/month",
                    "Bahria Town, Rawalpindi",
                    "https://images.unsplash.com/photo-1560448204-603b3fc33ddc?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60"
            ));

            propertyList.add(new Property(
                    "Modern Studio Apartment",
                    "PKR 25,000/month",
                    "Johar Town, Lahore",
                    "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60"
            ));
        }

        propertyAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
