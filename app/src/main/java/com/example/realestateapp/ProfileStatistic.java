package com.example.realestateapp;

public class ProfileStatistic {
    private String label;
    private String value;
    private int iconResId;

    public ProfileStatistic(String label, String value, int iconResId) {
        this.label = label;
        this.value = value;
        this.iconResId = iconResId;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public int getIconResId() {
        return iconResId;
    }
}