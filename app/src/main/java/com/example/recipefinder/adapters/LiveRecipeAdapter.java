package com.example.recipefinder.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.recipefinder.R;
import com.example.recipefinder.models.MealResponse.Meal;
import java.util.List;

public class LiveRecipeAdapter extends RecyclerView.Adapter<LiveRecipeAdapter.RecipeViewHolder> {

    private final List<Meal> mealList;
    private final OnRecipeClickListener listener;

    public interface OnRecipeClickListener {
        void onRecipeClick(Meal meal);
    }

    public LiveRecipeAdapter(List<Meal> mealList, OnRecipeClickListener listener) {
        this.mealList = mealList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_card, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Meal meal = mealList.get(position);

        holder.tvTitle.setText(meal.getStrMeal());

        // Proper Glide loading to fetch real images directly from URLs
        Glide.with(holder.itemView.getContext())
                .load(meal.getStrMealThumb())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_dialog_alert)
                .into(holder.ivRecipe);

        holder.itemView.setOnClickListener(v -> listener.onRecipeClick(meal));
    }

    @Override
    public int getItemCount() {
        return mealList != null ? mealList.size() : 0;
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRecipe;
        TextView tvTitle;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            // DIRECT BINDING: Using the exact IDs from item_recipe_card.xml
            ivRecipe = itemView.findViewById(R.id.iv_recipe_image);
            tvTitle = itemView.findViewById(R.id.tv_recipe_title);
        }
    }
}