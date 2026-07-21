package com.example.recipefinder.network;

import com.example.recipefinder.models.MealResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    // 1. PHASE 5 & 7: Search meals by their name token (e.g., "chicken", "pasta", "cake")
    @GET("api/json/v1/1/search.php")
    Call<MealResponse> searchMealsByName(@Query("s") String query);

    // 2. PHASE 7 (EXTENDED): Filter meals by their primary ingredient (e.g., "chicken_breast", "garlic")
    @GET("api/json/v1/1/filter.php")
    Call<MealResponse> filterMealsByIngredient(@Query("i") String ingredient);

    // 3. PHASE 5: Pulls a single random meal item card from the API server
    @GET("api/json/v1/1/random.php")
    Call<MealResponse> getRandomMeal();

    // 4. PHASE 8: Lookup full, explicit meal details by its unique ID matching key
    @GET("api/json/v1/1/lookup.php")
    Call<MealResponse> lookupMealById(@Query("i") String mealId);
}