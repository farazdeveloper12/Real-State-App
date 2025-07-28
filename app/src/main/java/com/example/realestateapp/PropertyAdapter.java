package com.example.realestateapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder> {

    private List<Property> properties;
    private OnPropertyClickListener listener;

    public interface OnPropertyClickListener {
        void onPropertyClick(Property property, int position);
    }

    public PropertyAdapter(List<Property> properties) {
        this.properties = properties;
    }

    public PropertyAdapter(List<Property> properties, OnPropertyClickListener listener) {
        this.properties = properties;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_property, parent, false);
        return new PropertyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
        Property property = properties.get(position);
        holder.bind(property, position);

        // Add click listener to the entire card
        holder.cardView.setOnClickListener(v -> {
            // Open PropertyDetailActivity when clicked
            Intent intent = new Intent(holder.itemView.getContext(), PropertyDetailActivity.class);
            intent.putExtra("PROPERTY_TITLE", property.getTitle());
            intent.putExtra("PROPERTY_PRICE", property.getPrice());
            intent.putExtra("PROPERTY_LOCATION", property.getLocation());
            intent.putExtra("PROPERTY_IMAGE", property.getImageUrl());
            holder.itemView.getContext().startActivity(intent);

            // Add animation
            ((AppCompatActivity)holder.itemView.getContext()).overridePendingTransition(
                    R.anim.slide_in_right, R.anim.slide_out_left);
        });

    }

    @Override
    public int getItemCount() {
        return properties.size();
    }

    class PropertyViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ImageView propertyImage;
        private TextView titleText, priceText, locationText;

        PropertyViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.propertyCard);
            propertyImage = itemView.findViewById(R.id.propertyImage);
            titleText = itemView.findViewById(R.id.propertyTitle);
            priceText = itemView.findViewById(R.id.propertyPrice);
            locationText = itemView.findViewById(R.id.propertyLocation);
        }

        void bind(Property property, int position) {
            titleText.setText(property.getTitle());
            priceText.setText(property.getPrice());
            locationText.setText(property.getLocation());

            // Load image with Glide for better image handling
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));

            Glide.with(itemView.getContext())
                    .load(property.getImageUrl())
                    .apply(requestOptions)
                    .placeholder(R.drawable.property_placeholder)
                    .error(R.drawable.property_placeholder)
                    .into(propertyImage);

            // Set click listener
            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPropertyClick(property, position);
                }
            });
        }
    }
}