package com.example.realestateapp;

public class Property {
    private String id;
    private String title;
    private String price;
    private String location;
    private String imageUrl;

    // Constructor for compatibility with existing code
    public Property(String title, String price, String location, String imageUrl) {
        this.title = title;
        this.price = price;
        this.location = location;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getLocation() {
        return location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

