package com.example.realestateapp;

public class Document {
    private String title;
    private String type;
    private String size;
    private String date;

    public Document(String title, String type, String size, String date) {
        this.title = title;
        this.type = type;
        this.size = size;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getSize() {
        return size;
    }

    public String getDate() {
        return date;
    }
}