package com.example.realestateapp;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PropertyMapActivity extends AppCompatActivity {

    private MapView map;
    private HashMap<String, String> userPreferences;
    private List<Property> properties = new ArrayList<>();
    private List<Property> matchedProperties = new ArrayList<>();
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private CardView propertyDetailCard;
    private TextView propertyTitle, propertyLocation, propertyPrice;
    private FloatingActionButton filterButton;
    private Marker selectedMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        setContentView(R.layout.activity_property_map);

        // Get user preferences from intent
        Intent intent = getIntent();
        if (intent.hasExtra("preferences")) {
            userPreferences = (HashMap<String, String>) intent.getSerializableExtra("preferences");
        }

        initializeViews();
        initializeMap();
        setupBottomSheet();
        generateSampleProperties();
    }

    private void initializeViews() {
        propertyDetailCard = findViewById(R.id.propertyDetailCard);
        propertyTitle = findViewById(R.id.propertyTitle);
        propertyLocation = findViewById(R.id.propertyLocation);
        propertyPrice = findViewById(R.id.propertyPrice);
        filterButton = findViewById(R.id.filterButton);

        filterButton.setOnClickListener(v -> showFilterDialog());
    }

    private void initializeMap() {
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(12.0);
        GeoPoint startPoint = new GeoPoint(40.7128, -74.0060); // New York
        mapController.setCenter(startPoint);

        // After setting up the map, filter and display properties
        filterPropertiesByPreferences();
        displayPropertiesOnMap();
    }

    private void setupBottomSheet() {
        View bottomSheet = findViewById(R.id.bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN && selectedMarker != null) {
                    selectedMarker.closeInfoWindow();
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
                // Handle slide offset if needed
            }
        });
    }

    private void generateSampleProperties() {
        // Generate sample properties for demonstration
        properties.add(new Property("Luxury Apartment", "123 Main St", "$500,000",
                40.7128, -74.0060, "Apartment", "City Center", "3 bed, 2 bath"));
        properties.add(new Property("Family House", "456 Oak Ave", "$750,000",
                40.7228, -74.0160, "House", "Suburbs", "4 bed, 3 bath"));
        properties.add(new Property("Modern Villa", "789 Beach Rd", "$1,200,000",
                40.7328, -74.0260, "Villa", "Coastal Area", "5 bed, 4 bath"));
        properties.add(new Property("Studio Apartment", "321 Downtown Ave", "$250,000",
                40.7028, -74.0360, "Apartment", "City Center", "1 bed, 1 bath"));
        properties.add(new Property("Countryside House", "159 Rural Lane", "$450,000",
                40.7428, -73.9960, "House", "Countryside", "3 bed, 2 bath"));
    }

    private void filterPropertiesByPreferences() {
        matchedProperties.clear();

        for (Property property : properties) {
            if (matchesPreferences(property)) {
                matchedProperties.add(property);
            }
        }
    }

    private boolean matchesPreferences(Property property) {
        // Implement filtering logic based on user preferences
        String propertyType = userPreferences.get("What type of property are you looking for?");
        String area = userPreferences.get("Which area do you prefer?");

        return (propertyType == null || property.getType().equals(propertyType)) &&
                (area == null || property.getArea().equals(area));
    }

    private void displayPropertiesOnMap() {
        map.getOverlays().clear();

        for (Property property : matchedProperties) {
            Marker marker = new Marker(map);
            marker.setPosition(new GeoPoint(property.getLatitude(), property.getLongitude()));
            marker.setTitle(property.getTitle());
            marker.setSnippet(property.getPrice());
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            marker.setOnMarkerClickListener((marker1, mapView) -> {
                selectedMarker = marker1;
                Property clickedProperty = findPropertyByMarker(marker1);
                if (clickedProperty != null) {
                    showPropertyDetails(clickedProperty);
                }
                return true;
            });

            map.getOverlays().add(marker);
        }

        map.invalidate();

        // Show success message if properties found
        if (!matchedProperties.isEmpty()) {
            Toast.makeText(this, "Found " + matchedProperties.size() + " matching properties!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private Property findPropertyByMarker(Marker marker) {
        for (Property property : matchedProperties) {
            if (property.getTitle().equals(marker.getTitle())) {
                return property;
            }
        }
        return null;
    }

    private void showPropertyDetails(Property property) {
        propertyTitle.setText(property.getTitle());
        propertyLocation.setText(property.getAddress());
        propertyPrice.setText(property.getPrice());

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        animatePropertyCard();
    }

    private void animatePropertyCard() {
        propertyDetailCard.setScaleX(0.8f);
        propertyDetailCard.setScaleY(0.8f);
        propertyDetailCard.setAlpha(0f);

        propertyDetailCard.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(300)
                .start();
    }

    private void showFilterDialog() {
        // Implement filter dialog
        Toast.makeText(this, "Filter dialog coming soon", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    // Property model class
    public static class Property {
        private String title;
        private String address;
        private String price;
        private double latitude;
        private double longitude;
        private String type;
        private String area;
        private String details;

        public Property(String title, String address, String price, double latitude,
                        double longitude, String type, String area, String details) {
            this.title = title;
            this.address = address;
            this.price = price;
            this.latitude = latitude;
            this.longitude = longitude;
            this.type = type;
            this.area = area;
            this.details = details;
        }

        // Getters
        public String getTitle() { return title; }
        public String getAddress() { return address; }
        public String getPrice() { return price; }
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        public String getType() { return type; }
        public String getArea() { return area; }
        public String getDetails() { return details; }
    }
}