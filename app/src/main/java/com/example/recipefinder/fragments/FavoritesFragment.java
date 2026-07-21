package com.example.recipefinder.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.recipefinder.R;
import com.example.recipefinder.activities.RecipeDetailActivity;
import com.example.recipefinder.adapters.LiveRecipeAdapter;
import com.example.recipefinder.database.RecipeDatabase;
import com.example.recipefinder.models.MealResponse.Meal;
import com.example.recipefinder.models.Recipe;
import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private RecyclerView rvFavorites;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        rvFavorites = view.findViewById(R.id.rv_favorites);
        if (rvFavorites != null) {
            rvFavorites.setLayoutManager(new GridLayoutManager(getContext(), 2));
        }

        loadSavedFavorites();

        return view;
    }

    private void loadSavedFavorites() {
        if (getContext() == null) return;

        RecipeDatabase.getDatabase(getContext()).favoriteDao().getAllFavorites()
                .observe(getViewLifecycleOwner(), recipes -> {
                    if (recipes != null && !recipes.isEmpty()) {
                        List<Meal> mappedMeals = new ArrayList<>();
                        for (Recipe r : recipes) {
                            Meal m = new Meal();
                            m.setIdMeal(String.valueOf(r.getId()));
                            m.setStrMeal(r.getTitle());
                            m.setStrMealThumb(r.getImageUrl());
                            mappedMeals.add(m);
                        }

                        LiveRecipeAdapter adapter = new LiveRecipeAdapter(mappedMeals, meal -> {
                            Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
                            intent.putExtra("MEAL_ID", meal.getIdMeal());
                            startActivity(intent);
                        });

                        if (rvFavorites != null) {
                            rvFavorites.setAdapter(adapter);
                        }
                    }
                });
    }
}