package com.example.pddc.ui.fragments;


import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.pddc.R;
import com.example.pddc.ui.activities.ProfileEditActivity;
import com.example.pddc.ui.activities.WelcomePage;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;


public class SettingsFragment extends Fragment {
    AlertDialog.Builder builder;
    TextView tvFullName, tvFarmName;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences preferences = requireContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        int themeMode = preferences.getInt("ThemeMode", AppCompatDelegate.MODE_NIGHT_NO); // Default to Light Mode
        AppCompatDelegate.setDefaultNightMode(themeMode);
        View rootView =  inflater.inflate(R.layout.fragment_settings, container, false);


        //Passing User Credentials
        db = FirebaseFirestore.getInstance();

        tvFullName = rootView.findViewById(R.id.tvFullName);
        tvFarmName = rootView.findViewById(R.id.tvFarmName);


        SharedPreferences userCredPreferences = requireContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        String farmerId = userCredPreferences.getString("farmerId", "");

        if (!farmerId.isEmpty()) {
            fetchUserDetails(farmerId);
        }


        //Navigate to Profile Editing Page
        MaterialButton btn_EditProfile = rootView.findViewById(R.id.btnEditProfile);
        btn_EditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
            startActivity(intent);
        });

        //Logging out
        MaterialButton mbLogout = rootView.findViewById(R.id.mbLogout);
        mbLogout.setOnClickListener(v -> handleLogOut());


        //Handling General Settings

        MaterialCardView mcvShareApp = rootView.findViewById(R.id.mcvShareApp);
        MaterialCardView mcvRateApp = rootView.findViewById(R.id.mcvRateApp);
        MaterialCardView mcvTheme = rootView.findViewById(R.id.mcvTheme);

        mcvTheme.setOnClickListener(v -> showThemeDialog());

        mcvShareApp.setOnClickListener(v -> shareApp());

        mcvRateApp.setOnClickListener(v -> rateApp());

        return rootView;
    }

    /** Fetching details from db **/
    private void fetchUserDetails(String farmerId) {
        db.collection("Users").document(farmerId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fullName = documentSnapshot.getString("fullName");
                        String houseName = documentSnapshot.getString("houseName");

                        tvFullName.setText(fullName);
                        tvFarmName.setText(houseName);
                    } else {
                        Log.d("Firestore", "No such document");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching data", e));
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
        LinearLayout llThemeDialog = requireView().findViewById(R.id.llThemeDialog);
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.theme_dialog, llThemeDialog);

        RadioGroup radioGroupTheme = dialogView.findViewById(R.id.radioGroupTheme);
        TextView tvCancelTheme = dialogView.findViewById(R.id.tvCancelTheme);
        TextView tvOkTheme = dialogView.findViewById(R.id.tvOkTheme);

        builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView)
                .setCancelable(true);
        AlertDialog dialog = builder.create();
        tvOkTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroupTheme.getCheckedRadioButtonId();
                if (selectedId == R.id.rbDarkMode) {
                    setAppTheme(AppCompatDelegate.MODE_NIGHT_YES);
                    dialog.dismiss();
                } else if (selectedId == R.id.rbLightMode) {
                    setAppTheme(AppCompatDelegate.MODE_NIGHT_NO);
                    dialog.dismiss();
                }
            }
        });
        tvCancelTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();
    }


    /**
     Handling Logout
     **/
    private void handleLogOut() {
        LinearLayout llLogout = requireView().findViewById(R.id.llLogout);
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.logout_dialog, llLogout);
        TextView tvYesLogout = dialogView.findViewById(R.id.tvYesLogout);
        TextView tvCancelLogout = dialogView.findViewById(R.id.tvCancelLogout);

        builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView)
                .setCancelable(true);

        AlertDialog dialog = builder.create();
        tvYesLogout.setOnClickListener(v -> {
            SharedPreferences preferences = requireContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear(); // Clear all saved data
            editor.apply();
            Intent intent = new Intent(getActivity(), WelcomePage.class);
            startActivity(intent);
        });
        tvCancelLogout.setOnClickListener(v -> dialog.dismiss());
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
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