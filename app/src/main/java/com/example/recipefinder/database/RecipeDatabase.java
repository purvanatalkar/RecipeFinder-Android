package com.example.recipefinder.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.recipefinder.models.Recipe;
import com.example.recipefinder.models.RecentRecipe;

// FIXED: Added RecentRecipe.class to entities and incremented version schema tracking level to 2
@Database(entities = {Recipe.class, RecentRecipe.class}, version = 2, exportSchema = false)
public abstract class RecipeDatabase extends RoomDatabase {
    private static volatile RecipeDatabase INSTANCE;

    public abstract FavoriteDao favoriteDao();

    public static RecipeDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RecipeDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    RecipeDatabase.class, "recipe_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}