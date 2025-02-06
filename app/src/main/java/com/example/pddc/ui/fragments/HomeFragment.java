package com.example.pddc.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pddc.R;
import com.example.pddc.ui.activities.BatchesActivity;
import com.google.android.material.card.MaterialCardView;

public class HomeFragment extends Fragment {



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

//        //Passing User Credentials
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        //Navigate to Profile Editing Page

        MaterialCardView mcvBatches = rootView.findViewById(R.id.mcvBatches);
        mcvBatches.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BatchesActivity.class);
            startActivity(intent);
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}