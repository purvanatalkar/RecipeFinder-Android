package com.example.recipefinder.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import com.example.recipefinder.R;

public class SettingsFragment extends Fragment {

    private SwitchCompat switchDarkMode, switchNotifications;
    private TextView tvLanguage, tvAbout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        switchDarkMode = view.findViewById(R.id.switch_dark_mode);
        switchNotifications = view.findViewById(R.id.switch_notifications);
        tvLanguage = view.findViewById(R.id.tv_settings_language);
        tvAbout = view.findViewById(R.id.tv_settings_about);

        // Track theme change requests live
        switchDarkMode.setChecked(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(getContext(), isChecked ? "Notifications Enabled" : "Notifications Muted", Toast.LENGTH_SHORT).show();
        });

        tvLanguage.setOnClickListener(v -> Toast.makeText(getContext(), "Languages support configured natively via system settings!", Toast.LENGTH_SHORT).show());

        // Simple informational modular alert dialog popup creation
        tvAbout.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                .setTitle("About Chef's Palette")
                .setMessage("Recipe Finder App v1.0.0\nCreated using Java, Retrofit, and Room Database Cache architecture.")
                .setPositiveButton("Awesome", null)
                .show());

        return view;
    }
}