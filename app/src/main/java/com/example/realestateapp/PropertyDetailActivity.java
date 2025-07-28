package com.example.realestateapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PropertyDetailActivity extends AppCompatActivity {

    private ImageView propertyImage;
    private TextView titleText, priceText, locationText, descriptionText;
    private FloatingActionButton favoriteButton;
    private Button contactButton, scheduleButton;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_detail);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Property Details");

        // Initialize views
        propertyImage = findViewById(R.id.propertyDetailImage);
        titleText = findViewById(R.id.propertyTitle);
        priceText = findViewById(R.id.propertyPrice);
        locationText = findViewById(R.id.propertyLocation);
        descriptionText = findViewById(R.id.propertyDescription);
        favoriteButton = findViewById(R.id.favoriteButton);
        contactButton = findViewById(R.id.contactButton);
        scheduleButton = findViewById(R.id.scheduleButton);

        // Set up data from intent
        String title = getIntent().getStringExtra("PROPERTY_TITLE");
        String price = getIntent().getStringExtra("PROPERTY_PRICE");
        String location = getIntent().getStringExtra("PROPERTY_LOCATION");
        String imageUrl = getIntent().getStringExtra("PROPERTY_IMAGE");

        titleText.setText(title);
        priceText.setText(price);
        locationText.setText(location);

        // Set property description (sample text)
        String description = "This beautiful property features modern amenities including " +
                "a spacious living area, well-equipped kitchen, and stunning views. " +
                "Located in a prime area with easy access to shopping centers, schools, " +
                "and public transportation.\n\n" +
                "• 3 Bedrooms\n" +
                "• 2 Bathrooms\n" +
                "• 1 Kitchen\n" +
                "• 1 Living Room\n" +
                "• Parking Available\n" +
                "• 24/7 Security";

        descriptionText.setText(description);

        // Load image with Glide
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));

        Glide.with(this)
                .load(imageUrl)
                .apply(requestOptions)
                .placeholder(R.drawable.property_placeholder)
                .error(R.drawable.property_placeholder)
                .into(propertyImage);

        // Set up button click listeners
        favoriteButton.setOnClickListener(v -> {
            isFavorite = !isFavorite;
            favoriteButton.setImageResource(isFavorite ?
                    R.drawable.ic_favorite_filled : R.drawable.ic_favorite_filled);

            Toast.makeText(this,
                    isFavorite ? "Added to favorites" : "Removed from favorites",
                    Toast.LENGTH_SHORT).show();
        });

        contactButton.setOnClickListener(v -> {
            Toast.makeText(this, "Contacting agent...", Toast.LENGTH_SHORT).show();
            // In a real app, open contact options or chat
        });

        scheduleButton.setOnClickListener(v -> {
            Toast.makeText(this, "Opening schedule view...", Toast.LENGTH_SHORT).show();
            // In a real app, open a calendar to schedule viewing
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}