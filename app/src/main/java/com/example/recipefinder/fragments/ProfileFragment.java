package com.example.recipefinder.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.recipefinder.R;
import com.example.recipefinder.activities.LoginActivity;
import com.example.recipefinder.database.RecipeDatabase;
import com.example.recipefinder.utils.SessionManager;

public class ProfileFragment extends Fragment {

    private TextView tvProfileName, tvSavedCount;
    private Button btnSettings, btnLogout;

    private SessionManager sessionManager;
    private RecipeDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // 1. Bind UI Components
        tvProfileName = view.findViewById(R.id.tv_profile_name);
        tvSavedCount = view.findViewById(R.id.tv_saved_recipes_count);
        btnSettings = view.findViewById(R.id.btn_profile_settings);
        btnLogout = view.findViewById(R.id.btn_profile_logout);

        // 2. Instantiate Managers
        sessionManager = new SessionManager(requireContext());
        db = RecipeDatabase.getDatabase(requireContext());

        // 3. Configure User State Identity Check
        if (sessionManager.isLoggedIn()) {
            tvProfileName.setText("admin@chef.com");
            btnLogout.setText("Log Out");
        } else {
            // Guest mode routing fallback from Phase 3 parameters
            tvProfileName.setText("Guest Chef");
            btnLogout.setText("Exit Guest Mode / Login");
        }

        // 4. Track Saved Recipe Records Live dynamically using LiveData
        db.favoriteDao().getAllFavorites().observe(getViewLifecycleOwner(), favorites -> {
            if (favorites != null) {
                tvSavedCount.setText(String.valueOf(favorites.size()));
            } else {
                tvSavedCount.setText("0");
            }
        });

        // 5. Wire Settings Interaction Stub
        btnSettings.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Opening Preferences Framework Window...", Toast.LENGTH_SHORT).show();
        });

        // 6. Handle Safe Application Logout Actions
        btnLogout.setOnClickListener(v -> executeLogoutRoutine());

        return view;
    }

    private void executeLogoutRoutine() {
        // Clear active user validation flags out of persistent SharedPreferences caches
        sessionManager.logoutUser();

        Toast.makeText(getContext(), "Logged out successfully!", Toast.LENGTH_SHORT).show();

        // Redirect back out to the base Login entry vector screen
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        // Clear activity task navigation histories so pressing "Back" won't reopen the app dashboard
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}