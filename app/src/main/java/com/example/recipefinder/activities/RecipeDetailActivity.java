package com.example.recipefinder.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.recipefinder.R;
import com.example.recipefinder.database.RecipeDatabase;
import com.example.recipefinder.models.MealResponse;
import com.example.recipefinder.models.MealResponse.Meal;
import com.example.recipefinder.models.Recipe;
import com.example.recipefinder.models.RecentRecipe;
import com.example.recipefinder.network.ApiClient;
import com.example.recipefinder.network.ApiService;

import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeDetailActivity extends AppCompatActivity {

    private ImageView ivMeal;
    private TextView tvTitle, tvCategory, tvArea, tvInstructions;
    private Button btnFavoriteToggle, btnYoutube;

    private ApiService apiService;
    private RecipeDatabase db;
    private Meal currentMeal;
    private boolean isCurrentlyFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // 1. Bind Layout Elements
        ivMeal = findViewById(R.id.iv_detail_image);
        tvTitle = findViewById(R.id.tv_detail_title);
        tvCategory = findViewById(R.id.tv_detail_category);
        tvArea = findViewById(R.id.tv_detail_area);
        tvInstructions = findViewById(R.id.tv_detail_instructions);
        btnFavoriteToggle = findViewById(R.id.btn_favorite_toggle);
        btnYoutube = findViewById(R.id.btn_youtube);

        // 2. Initialize Core Subsystems
        apiService = ApiClient.getApiService();
        db = RecipeDatabase.getDatabase(this);

        // 3. Extract the unique ID passed from the previous list view screen
        String mealId = getIntent().getStringExtra("MEAL_ID");

        if (mealId != null) {
            loadCompleteRecipeDetails(mealId);
            checkFavoriteStatus(Integer.parseInt(mealId));
        }

        // 4. Setup Click Listeners
        btnFavoriteToggle.setOnClickListener(v -> toggleFavoriteState());
    }

    // Checks background local database records to update the styling of the favorite toggle button
    private void checkFavoriteStatus(int mealId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            isCurrentlyFavorite = db.favoriteDao().isFavorite(mealId);
            runOnUiThread(() -> {
                if (isCurrentlyFavorite) {
                    btnFavoriteToggle.setText("Remove from Favorites");
                    btnFavoriteToggle.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFD32F2F)); // Red hex
                } else {
                    btnFavoriteToggle.setText("Save to Favorites");
                    btnFavoriteToggle.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFF9800)); // Orange hex
                }
            });
        });
    }

    // Handles the toggling logic (Insert to database vs Delete from database)
    private void toggleFavoriteState() {
        if (currentMeal == null) {
            Toast.makeText(this, "Recipe data still loading...", Toast.LENGTH_SHORT).show();
            return;
        }

        int mealId = Integer.parseInt(currentMeal.getIdMeal());

        // Map data from external API structure into your custom local Room Database blueprint model
        Recipe localRecipe = new Recipe(
                mealId,
                currentMeal.getStrMeal(),
                currentMeal.getStrMealThumb(),
                20, 4, "",
                currentMeal.getStrInstructions(),
                0.0
        );

        Executors.newSingleThreadExecutor().execute(() -> {
            if (isCurrentlyFavorite) {
                db.favoriteDao().deleteFavoriteRecipe(localRecipe);
                isCurrentlyFavorite = false;
            } else {
                localRecipe.setFavorite(true);
                db.favoriteDao().insertFavoriteRecipe(localRecipe);
                isCurrentlyFavorite = true;
            }

            // Push updates back onto the Main Thread to refresh the user interface view instantly
            runOnUiThread(() -> {
                checkFavoriteStatus(mealId);
                Toast.makeText(this, isCurrentlyFavorite ? "Saved to your Cookbook!" : "Removed from Favorites", Toast.LENGTH_SHORT).show();
            });
        });
    }

    // Asynchronously downloads detailed recipe documents from the API server pipelines
    private void loadCompleteRecipeDetails(String id) {
        apiService.lookupMealById(id).enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(@NonNull Call<MealResponse> call, @NonNull Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null) {
                    currentMeal = response.body().getMeals().get(0);

                    // Populate data elements into UI layout text objects
                    tvTitle.setText(currentMeal.getStrMeal());
                    tvInstructions.setText(currentMeal.getStrInstructions());

                    // Extracted extensions for Category & Area fields if they exist in network responses
                    if (currentMeal.getStrCategory() != null) tvCategory.setText(currentMeal.getStrCategory());
                    if (currentMeal.getStrArea() != null) tvArea.setText(currentMeal.getStrArea());

                    // Render layout photography image view using Glide loading thread engines
                    Glide.with(RecipeDetailActivity.this)
                            .load(currentMeal.getStrMealThumb())
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .into(ivMeal);

                    // If a YouTube link exists, show the video action redirect trigger button
                    if (currentMeal.getStrYoutube() != null && !currentMeal.getStrYoutube().isEmpty()) {
                        btnYoutube.setVisibility(View.VISIBLE);
                        btnYoutube.setOnClickListener(v -> {
                            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentMeal.getStrYoutube()));
                            startActivity(webIntent);
                        });
                    }

                    // Log recipe view into recent user history inside local SQLite Room storage
                    saveToRecentHistory(currentMeal);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MealResponse> call, @NonNull Throwable t) {
                Toast.makeText(RecipeDetailActivity.this, "Network Error: Unable to fetch recipe details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Save the viewed item configuration to local history
    private void saveToRecentHistory(Meal meal) {
        try {
            int mealId = Integer.parseInt(meal.getIdMeal());
            Executors.newSingleThreadExecutor().execute(() -> {
                RecentRecipe recent = new RecentRecipe(
                        mealId,
                        meal.getStrMeal(),
                        meal.getStrMealThumb(),
                        System.currentTimeMillis()
                );
                db.favoriteDao().insertRecentRecipe(recent);
            });
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}