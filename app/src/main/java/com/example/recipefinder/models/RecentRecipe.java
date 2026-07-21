package com.example.recipefinder.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recent_recipes")
public class RecentRecipe {
    @PrimaryKey
    private int id;
    private String title;
    private String imageUrl;
    private long timestamp; // Tracks view sequence history

    public RecentRecipe(int id, String title, String imageUrl, long timestamp) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getImageUrl() { return imageUrl; }
    public long getTimestamp() { return timestamp; }
}