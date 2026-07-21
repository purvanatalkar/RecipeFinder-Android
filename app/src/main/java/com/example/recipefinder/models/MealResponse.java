package com.example.recipefinder.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MealResponse {
    @SerializedName("meals")
    private List<Meal> meals;

    public List<Meal> getMeals() { return meals; }
    public void setMeals(List<Meal> meals) { this.meals = meals; }

    public static class Meal {
        @SerializedName("idMeal")
        private String idMeal;

        @SerializedName("strMeal")
        private String strMeal;

        @SerializedName("strMealThumb")
        private String strMealThumb;

        @SerializedName("strInstructions")
        private String strInstructions;

        // NEW FIELDS: Added to support Phase 8 Cuisine Tags and Video Features
        @SerializedName("strCategory")
        private String strCategory;

        @SerializedName("strArea")
        private String strArea;

        @SerializedName("strYoutube")
        private String strYoutube;

        // Existing Getters and Setters
        public String getIdMeal() { return idMeal; }
        public void setIdMeal(String idMeal) { this.idMeal = idMeal; }

        public String getStrMeal() { return strMeal; }
        public void setStrMeal(String strMeal) { this.strMeal = strMeal; }

        public String getStrMealThumb() { return strMealThumb; }
        public void setStrMealThumb(String strMealThumb) { this.strMealThumb = strMealThumb; }

        public String getStrInstructions() { return strInstructions; }
        public void setStrInstructions(String strInstructions) { this.strInstructions = strInstructions; }

        // New Getters and Setters
        public String getStrCategory() { return strCategory; }
        public void setStrCategory(String strCategory) { this.strCategory = strCategory; }

        public String getStrArea() { return strArea; }
        public void setStrArea(String strArea) { this.strArea = strArea; }

        public String getStrYoutube() { return strYoutube; }
        public void setStrYoutube(String strYoutube) { this.strYoutube = strYoutube; }
    }
}