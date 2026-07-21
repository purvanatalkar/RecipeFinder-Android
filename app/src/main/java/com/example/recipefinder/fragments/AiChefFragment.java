package com.example.recipefinder.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class AiChefFragment extends Fragment {

    private EditText etIngredients;
    private Button btnSearch;
    private TextView tvHeader;
    private RecyclerView rvResults;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ai_chef, container, false);

        etIngredients = view.findViewById(R.id.et_ai_ingredients);
        btnSearch = view.findViewById(R.id.btn_ai_search);
        tvHeader = view.findViewById(R.id.tv_ai_header);
        rvResults = view.findViewById(R.id.rv_ai_results);

        rvResults.setLayoutManager(new GridLayoutManager(getContext(), 2));
        apiService = ApiClient.getApiService();

        btnSearch.setOnClickListener(v -> {
            String query = etIngredients.getText().toString().trim();
            if (!query.isEmpty()) {
                hideKeyboard();
                searchRecipesByIngredient(query);
            } else {
                Toast.makeText(getContext(), "Please type at least one ingredient!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void searchRecipesByIngredient(String input) {
        // Take the primary ingredient typed by user
        String firstIngredient = input.split("[,\\s]+")[0];

        apiService.searchMealsByName(firstIngredient).enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(@NonNull Call<MealResponse> call, @NonNull Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null) {
                    List<Meal> meals = response.body().getMeals();
                    tvHeader.setVisibility(View.VISIBLE);

                    LiveRecipeAdapter adapter = new LiveRecipeAdapter(meals, meal -> {
                        Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
                        intent.putExtra("MEAL_ID", meal.getIdMeal());
                        startActivity(intent);
                    });
                    rvResults.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "No recipes found for '" + input + "'. Try chicken, paneer, or egg!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MealResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Network Error. Please check your connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideKeyboard() {
        if (getActivity() != null && getActivity().getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }
}