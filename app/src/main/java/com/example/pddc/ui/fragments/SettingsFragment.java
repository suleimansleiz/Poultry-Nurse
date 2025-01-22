package com.example.pddc.ui.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.pddc.R;
import com.example.pddc.ui.activities.LoginActivity;
import com.google.android.material.button.MaterialButton;


public class SettingsFragment extends Fragment {
    private MaterialButton mb_Logout;
    AlertDialog.Builder builder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_settings, container, false);

        if (getArguments() != null) {
            String fullName = getArguments().getString("fullName");
            String email = getArguments().getString("email");

            TextView tvFullName = requireView().findViewById(R.id.tvFullName);
            tvFullName.setText(fullName);
            TextView tvEmail = requireView().findViewById(R.id.tvEmailAddress);
            tvEmail.setText(email);
        }


        MaterialButton btn_EditProfile = rootView.findViewById(R.id.btnEditProfile);
        btn_EditProfile.setOnClickListener(v -> {
            builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Wait!");
            builder.setMessage("Do you want to edit your profile?");
            builder.setCancelable(true);
            builder.setPositiveButton("Yes", (dialog, which) -> {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss()).show();
        });
        return rootView;
    }
}