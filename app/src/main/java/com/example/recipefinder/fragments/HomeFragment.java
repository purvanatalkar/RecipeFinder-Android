package com.example.recipefinder.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.recipefinder.R;
import com.example.recipefinder.activities.RecipeDetailActivity;
import com.example.recipefinder.adapters.LiveRecipeAdapter;
import com.example.recipefinder.models.MealResponse;
import com.example.recipefinder.models.MealResponse.Meal;
import com.example.recipefinder.network.ApiClient;
import com.example.recipefinder.network.ApiService;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView rvPopular;
    private RecyclerView rvRecentlyViewed; // FIXED: Added field tracking layout binding configuration variable
    private ApiService apiService;

    // Chef Recommendation UI bindings
    private ImageView ivChefImage;
    private TextView tvChefTitle;
    private View chefCardView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 1. Bind Popular Recycler Views
        rvPopular = view.findViewById(R.id.rv_popular);
        rvPopular.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // 2. FIXED: Dynamic binding block inside your layout constructor mapping layer method
        rvRecentlyViewed = view.findViewById(R.id.rv_recently_viewed);
        rvRecentlyViewed.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // 3. Bind Chef Recommendation components
        chefCardView = view.findViewById(R.id.cv_chef_recommendation);
        ivChefImage = view.findViewById(R.id.iv_chef_recommendation_image);
        tvChefTitle = view.findViewById(R.id.tv_chef_recommendation_title);

        // Network Layer Init
        apiService = ApiClient.getApiService();

        // Execution pipelines
        fetchPopularRecipes("chicken");
        fetchRecipeOfTheDay();
        insertCustomRecipesToDatabase();

        // FIXED: Launching observer model listener pipeline
        setupRecentlyViewedObserver();

        return view;
    }

    // FIXED: Paste this monitoring method inside your HomeFragment controller scope context layer
    private void setupRecentlyViewedObserver() {
        if (getContext() == null) return;

        com.example.recipefinder.database.RecipeDatabase.getDatabase(getContext())
                .favoriteDao().getRecentlyViewed().observe(getViewLifecycleOwner(), recents -> {
                    if (recents != null && !recents.isEmpty()) {
                        // Map RecentRecipe objects list over into Generic Meal objects to reuse LiveRecipeAdapter
                        List<Meal> mappedList = new ArrayList<>();
                        for (com.example.recipefinder.models.RecentRecipe r : recents) {
                            Meal m = new Meal();
                            m.setIdMeal(String.valueOf(r.getId()));
                            m.setStrMeal(r.getTitle());
                            m.setStrMealThumb(r.getImageUrl());
                            mappedList.add(m);
                        }

                        LiveRecipeAdapter recentAdapter = new LiveRecipeAdapter(mappedList, meal -> {
                            Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
                            intent.putExtra("MEAL_ID", meal.getIdMeal());
                            startActivity(intent);
                        });
                        rvRecentlyViewed.setAdapter(recentAdapter);
                    }
                });
    }

    private void fetchRecipeOfTheDay() {
        apiService.searchMealsByName("paneer").enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(@NonNull Call<MealResponse> call, @NonNull Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null && !response.body().getMeals().isEmpty()) {
                    int randomIndex = new Random().nextInt(response.body().getMeals().size());
                    Meal featuredRecipe = response.body().getMeals().get(randomIndex);

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            tvChefTitle.setText(featuredRecipe.getStrMeal());
                            Glide.with(HomeFragment.this)
                                    .load(featuredRecipe.getStrMealThumb())
                                    .placeholder(android.R.drawable.ic_menu_gallery)
                                    .into(ivChefImage);

                            chefCardView.setOnClickListener(v -> {
                                Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
                                intent.putExtra("MEAL_ID", featuredRecipe.getIdMeal());
                                startActivity(intent);
                            });
                        });
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MealResponse> call, @NonNull Throwable t) {
                // Graceful fallback
            }
        });
    }

    private void fetchPopularRecipes(String keyword) {
        apiService.searchMealsByName(keyword).enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(@NonNull Call<MealResponse> call, @NonNull Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null) {
                    LiveRecipeAdapter adapter = new LiveRecipeAdapter(response.body().getMeals(), meal -> {
                        Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
                        intent.putExtra("MEAL_ID", meal.getIdMeal());
                        startActivity(intent);
                    });
                    rvPopular.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "Server returned empty array lists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MealResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void insertCustomRecipesToDatabase() {
        new Thread(() -> {
            try {
                com.example.recipefinder.database.RecipeDatabase db =
                        com.example.recipefinder.database.RecipeDatabase.getDatabase(getContext());
                com.example.recipefinder.database.FavoriteDao dao = db.favoriteDao();

                // 1. Butter Chicken
                dao.insertFavoriteRecipe(new com.example.recipefinder.models.Recipe(
                        9001, "Butter Chicken", "https://www.themealdb.com/images/media/meals/g046bb1641968842.jpg",
                        40, 4, "Classic Indian butter chicken.",
                        "1. Marinate chicken with yogurt and spices.\n2. Grill or pan-fry until cooked.\n3. Prepare rich tomato, butter, and cream gravy.\n4. Mix together and simmer. Watch: https://www.youtube.com/watch?v=a03U45jFxOI", 4.9));

                // 2. Kadai Paneer
                dao.insertFavoriteRecipe(new com.example.recipefinder.models.Recipe(
                        9002, "Kadai Paneer", "https://www.themealdb.com/images/media/meals/xxyuyt1503146385.jpg",
                        30, 3, "Spicy and flavorful paneer cooked with bell peppers.",
                        "1. Roast and grind kadai masala spices.\n2. Sauté onions, tomatoes, and capsicum.\n3. Add paneer cubes and spices.\n4. Cook on low flame. Watch: https://www.youtube.com/watch?v=O1kG7M2m9wU", 4.8));

                // 3. Chicken Biryani
                dao.insertFavoriteRecipe(new com.example.recipefinder.models.Recipe(
                        9003, "Chicken Biryani", "https://www.themealdb.com/images/media/meals/xrttsx1487340446.jpg",
                        60, 6, "Aromatic layered rice and chicken dish.",
                        "1. Parboil basmati rice with whole spices.\n2. Cook marinated chicken curry.\n3. Layer rice and chicken with saffron and mint.\n4. Dum cook for 20 mins. Watch: https://www.youtube.com/watch?v=M7W7V7_oTTA", 5.0));

                // 4. Paneer Tikka Masala
                dao.insertFavoriteRecipe(new com.example.recipefinder.models.Recipe(
                        9004, "Paneer Tikka Masala", "https://www.themealdb.com/images/media/meals/vsturw1511531094.jpg",
                        35, 4, "Tandoori grilled paneer in a spiced gravy.",
                        "1. Skewer marinated paneer and veggies.\n2. Roast in oven or pan.\n3. Toss into an onion-tomato masala base with heavy cream. Watch: https://www.youtube.com/watch?v=zJg5k67p7pA", 4.7));

                // 5. Palak Paneer
                dao.insertFavoriteRecipe(new com.example.recipefinder.models.Recipe(
                        9005, "Palak Paneer", "https://www.themealdb.com/images/media/meals/xvsurr1511532426.jpg",
                        25, 3, "Paneer cubes in smooth creamed spinach gravy.",
                        "1. Blanch and puree fresh spinach.\n2. Cook with ginger, garlic, and green chilies.\n3. Add lightly pan-fried paneer cubes. Watch: https://www.youtube.com/watch?v=y3Wepj-U-3Y", 4.6));

                // 6. Chicken Tikka Masala
                dao.insertFavoriteRecipe(new com.example.recipefinder.models.Recipe(
                        9006, "Chicken Tikka Masala", "https://www.themealdb.com/images/media/meals/qxytrx1511304021.jpg",
                        45, 4, "Roasted marinated chicken chunks in a spiced sauce.",
                        "1. Grill seasoned chicken tikka pieces.\n2. Build a rich cream-based tomato gravy.\n3. Combine and garnish with fresh coriander. Watch: https://www.youtube.com/watch?v=X30V7_XzC4s", 4.9));

                // 7. Chole Bhature
                dao.insertFavoriteRecipe(new com.example.recipefinder.models.Recipe(
                        9007, "Chole Bhature", "https://www.themealdb.com/images/media/meals/vvpprx1514111757.jpg",
                        50, 4, "Spicy chickpeas served with fried leavened bread.",
                        "1. Soak and boil chickpeas with tea bag.\n2. Sauté with intense dark chole masala spices.\n3. Fry deep puffed bhaturas. Watch: https://www.youtube.com/watch?v=6eG7n4qV4jM", 4.9));

                // 8. Dal Makhani
                dao.insertFavoriteRecipe(new com.example.recipefinder.models.Recipe(
                        9008, "Dal Makhani", "https://www.themealdb.com/images/media/meals/wuxrtw1511541999.jpg",
                        90, 5, "Slow cooked creamy black lentils.",
                        "1. Simmer black lentils and kidney beans overnight.\n2. Temper with butter, tomato puree, and kasuri methi.\n3. Finish with rich cream. Watch: https://www.youtube.com/watch?v=A2y8bLh6U5c", 4.8));

                // 9. Aloo Gobi
                dao.insertFavoriteRecipe(new com.example.recipefinder.models.Recipe(
                        9009, "Aloo Gobi", "https://www.themealdb.com/images/media/meals/tvvxpv1511191952.jpg",
                        20, 3, "Dry potato and cauliflower stir fry.",
                        "1. Cut potatoes and cauliflower florets.\n2. Stir-fry with turmeric, cumin, and garam masala.\n3. Cook covered until tender. Watch: https://www.youtube.com/watch?v=nE5uDkO55zI", 4.5));

                // 10. Crispy Samosa
                dao.insertFavoriteRecipe(new com.example.recipefinder.models.Recipe(
                        9010, "Crispy Samosa", "https://www.themealdb.com/images/media/meals/1550441882.jpg",
                        45, 8, "Flaky pastry stuffed with spiced potato mixture.",
                        "1. Make dough using flour and ajwain.\n2. Stuff with seasoned mashed potatoes and peas.\n3. Deep fry on low heat until golden brown. Watch: https://www.youtube.com/watch?v=2p3nLek8-lU", 4.7));

                // 11. Masala Dosa
                dao.insertFavoriteRecipe(new com.example.recipefinder.models.Recipe(
                        9011, "Masala Dosa", "https://www.themealdb.com/images/media/meals/utxrtv1511545643.jpg",
                        30, 4, "Crispy rice crepe with savory potato filling.",
                        "1. Spread fermented rice batter on hot tawa.\n2. Add butter and place potato bhaji in center.\n3. Roll and serve hot with sambar. Watch: https://www.youtube.com/watch?v=CCab5oh06oc", 4.9));

                // 12. Tandoori Roti
                dao.insertFavoriteRecipe(new com.example.recipefinder.models.Recipe(
                        9012, "Tandoori Roti", "https://www.themealdb.com/images/media/meals/wupxvt1511179043.jpg",
                        15, 5, "Traditional Indian clay-oven flatbread.",
                        "1. Knead whole wheat dough.\n2. Roll flat and stick inside a hot tandoor or tawa handle.\n3. Apply direct flame flip and coat with butter. Watch: https://www.youtube.com/watch?v=TcrK0l4b3Xk", 4.6));

                // 13. Gulab Jamun
                dao.insertFavoriteRecipe(new com.example.recipefinder.models.Recipe(
                        9013, "Gulab Jamun", "https://www.themealdb.com/images/media/meals/ustyvv1511449763.jpg",
                        45, 10, "Milk-solid dumplings fried and soaked in sugar syrup.",
                        "1. Prepare smooth balls from khoya and cardamom.\n2. Deep fry on low until dark brown.\n3. Drop immediately in hot rose-flavored sugar syrup. Watch: https://www.youtube.com/watch?v=v2jJoxE5sEE", 5.0));

                // 14. Garlic Naan
                dao.insertFavoriteRecipe(new com.example.recipefinder.models.Recipe(
                        9014, "Garlic Naan", "https://www.themealdb.com/images/media/meals/rvxxuy1511190455.jpg",
                        25, 4, "Soft flatbread topped with minced garlic and coriander.",
                        "1. Stretch leavened maida dough.\n2. Press minced garlic and coriander on top side.\n3. Bake on hot iron skillet until bubbles form. Watch: https://www.youtube.com/watch?v=S38W8w8XvG8", 4.8));

                // 15. Shahi Paneer
                dao.insertFavoriteRecipe(new com.example.recipefinder.models.Recipe(
                        9015, "Shahi Paneer", "https://www.themealdb.com/images/media/meals/quuxrr1511786523.jpg",
                        35, 4, "Royal gravy dish using paneer, cashew paste, and saffron.",
                        "1. Puree boiled onions, cashews, and almonds.\n2. Cook gravy with mild aromatic cardamoms.\n3. Drop paneer cubes and cream. Watch: https://www.youtube.com/watch?v=4yS9A1z8-qM", 4.9));

                // 16. Chicken Korma
                dao.insertFavoriteRecipe(new com.example.recipefinder.models.Recipe(
                        9016, "Chicken Korma", "https://www.themealdb.com/images/media/meals/uryqtv1511400244.jpg",
                        50, 4, "Rich mughlai chicken dish cooked with fried onion paste.",
                        "1. Fry onions until golden brown and crush into paste.\n2. Cook chicken with yogurt, spices, and the brown onion base.\n3. Simmer until oil separates. Watch: https://www.youtube.com/watch?v=pK_6Z4C_N3s", 4.7));

                // 17. Paneer Bhurji
                dao.insertFavoriteRecipe(new com.example.recipefinder.models.Recipe(
                        9017, "Paneer Bhurji", "https://www.themealdb.com/images/media/meals/wuxrtw1511541999.jpg",
                        20, 3, "Scrambled cottage cheese cooked with aromatic spices.",
                        "1. Sauté finely chopped onions, ginger, and green chilies.\n2. Add tomatoes followed by turmeric and red chili powder.\n3. Crumble paneer into the pan and mix well. Cook for 5 mins. Watch: https://www.youtube.com/watch?v=ce3G79pG480", 4.8));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}