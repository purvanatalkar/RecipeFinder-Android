package com.example.recipefinder.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.recipefinder.R;
import com.example.recipefinder.activities.RecipeDetailActivity;
import com.example.recipefinder.adapters.LiveRecipeAdapter;
import com.example.recipefinder.models.MealResponse;
import com.example.recipefinder.models.MealResponse.Meal;
import com.example.recipefinder.network.ApiClient;
import com.example.recipefinder.network.ApiService;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private RecyclerView rvResults;
    private SearchView searchView;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = view.findViewById(R.id.search_view);
        rvResults = view.findViewById(R.id.rv_search_results);
        rvResults.setLayoutManager(new GridLayoutManager(getContext(), 2));

        apiService = ApiClient.getApiService();

        // FIX: Force keyboard & focus on click
        searchView.setIconifiedByDefault(false);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                hideKeyboard();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() >= 2) {
                    performSearch(newText);
                }
                return false;
            }
        });

        return view;
    }

    private void performSearch(String query) {
        apiService.searchMealsByName(query).enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(@NonNull Call<MealResponse> call, @NonNull Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null) {
                    List<Meal> meals = response.body().getMeals();
                    LiveRecipeAdapter adapter = new LiveRecipeAdapter(meals, meal -> {
                        Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
                        intent.putExtra("MEAL_ID", meal.getIdMeal());
                        startActivity(intent);
                    });
                    rvResults.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MealResponse> call, @NonNull Throwable t) {}
        });
    }

    private void hideKeyboard() {
        if (getActivity() != null && getActivity().getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }
}