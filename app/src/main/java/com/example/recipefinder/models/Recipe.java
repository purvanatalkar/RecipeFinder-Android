package com.example.recipefinder.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipes")
public class Recipe {
    @PrimaryKey
    private int id;
    private String title;
    private String imageUrl;
    private int readyInMinutes;
    private int servings;
    private String summary;
    private String instructions;
    private double spoonacularScore;
    private boolean isFavorite;

    // Full constructor जिसकी आपकी Detail Activity को तलाश है
    public Recipe(int id, String title, String imageUrl, int readyInMinutes, int servings,
                  String summary, String instructions, double spoonacularScore) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.readyInMinutes = readyInMinutes;
        this.servings = servings;
        this.summary = summary;
        this.instructions = instructions;
        this.spoonacularScore = spoonacularScore;
        this.isFavorite = false;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getReadyInMinutes() { return readyInMinutes; }
    public void setReadyInMinutes(int readyInMinutes) { this.readyInMinutes = readyInMinutes; }

    public int getServings() { return servings; }
    public void setServings(int servings) { this.servings = servings; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public double getSpoonacularScore() { return spoonacularScore; }
    public void setSpoonacularScore(double spoonacularScore) { this.spoonacularScore = spoonacularScore; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
}