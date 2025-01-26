package com.example.pddc.ui.fragments;


import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.pddc.R;
import com.example.pddc.ui.activities.ProfileEditActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.Objects;


public class SettingsFragment extends Fragment {
    AlertDialog.Builder builder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences preferences = requireContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        int themeMode = preferences.getInt("ThemeMode", AppCompatDelegate.MODE_NIGHT_NO); // Default to Light Mode
        AppCompatDelegate.setDefaultNightMode(themeMode);
        View rootView =  inflater.inflate(R.layout.fragment_settings, container, false);


        //Navigate to Profile Editing Page
        MaterialButton btn_EditProfile = rootView.findViewById(R.id.btnEditProfile);
        btn_EditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
            startActivity(intent);
        });


        //Handling General Settings


        MaterialCardView mcvShareApp = rootView.findViewById(R.id.mcvShareApp);
        MaterialCardView mcvRateApp = rootView.findViewById(R.id.mcvRateApp);
        MaterialCardView mcvTheme = rootView.findViewById(R.id.mcvTheme);

        mcvTheme.setOnClickListener(v -> showThemeDialog());

        mcvShareApp.setOnClickListener(v -> shareApp());

        mcvRateApp.setOnClickListener(v -> rateApp());

        return rootView;
    }

    /**
     Handling App theme
     **/
    private void setAppTheme(int mode) {
        AppCompatDelegate.setDefaultNightMode(mode);
        SharedPreferences preferences = requireContext().getSharedPreferences("AppSettings", MODE_PRIVATE);
        SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
        editor.putInt("ThemeMode", mode);
        editor.apply();
    }

    private void showThemeDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.theme_dialog, null);

        RadioGroup radioGroupTheme = dialogView.findViewById(R.id.radioGroupTheme);
        RadioButton rbDarkMode = dialogView.findViewById(R.id.rbDarkMode);
        RadioButton rbLightMode = dialogView.findViewById(R.id.rbLightMode);


        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Theme")
                .setView(dialogView)
                .setCancelable(true)
                .setPositiveButton("OK", (dialog, which) -> {
                    int selectedId = radioGroupTheme.getCheckedRadioButtonId();
                    if (selectedId == R.id.rbDarkMode) {
                        setAppTheme(AppCompatDelegate.MODE_NIGHT_YES);
                    } else if (selectedId == R.id.rbLightMode) {
                        setAppTheme(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Are you interested in managing a smart Poultry Farm? Click the link below to download Poultry Disease Detection and Control (P D D C) App: https://sleiz.com/sleizwaredevelopment/PDDC");
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private void rateApp() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    private String getPackageName() {
        return null;
    }
}