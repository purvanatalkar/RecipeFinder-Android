package com.example.recipefinder.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.recipefinder.models.Recipe;
import com.example.recipefinder.models.RecentRecipe;
import java.util.List;

@Dao
public interface FavoriteDao {

    // --- Favorite Recipes Operations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavoriteRecipe(Recipe recipe);

    @Delete
    void deleteFavoriteRecipe(Recipe recipe);

    @Query("SELECT EXISTS(SELECT 1 FROM recipes WHERE id = :id)")
    boolean isFavorite(int id);

    @Query("SELECT * FROM recipes")
    LiveData<List<Recipe>> getAllFavorites();

    // --- NEW: Recently Viewed Recipes Operations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecentRecipe(RecentRecipe recipe);

    @Query("SELECT * FROM recent_recipes ORDER BY timestamp DESC LIMIT 5")
    LiveData<List<RecentRecipe>> getRecentlyViewed();
}