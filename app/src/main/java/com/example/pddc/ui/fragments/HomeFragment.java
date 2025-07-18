package com.example.pddc.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pddc.R;
import com.example.pddc.ui.activities.BatchesActivity;
import com.example.pddc.ui.activities.CleanlinessActivity;
import com.example.pddc.ui.activities.Detections;
import com.example.pddc.ui.activities.RTMonitorActivity;
import com.example.pddc.ui.activities.SchedulesActivity;
import com.example.pddc.ui.activities.VaccinesActivity;
import com.google.android.material.card.MaterialCardView;

public class HomeFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);


        //Navigate to Batches page
        MaterialCardView mcvBatches = rootView.findViewById(R.id.mcvBatches);
        mcvBatches.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BatchesActivity.class);
            startActivity(intent);
        });

        //Navigate to Schedules page
        MaterialCardView mcvSchedules = rootView.findViewById(R.id.mcvSchedules);
        mcvSchedules.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SchedulesActivity.class);
            startActivity(intent);
        });

        LinearLayout llMonitor = rootView.findViewById(R.id.llMonitor);
        llMonitor.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RTMonitorActivity.class);
            startActivity(intent);
        });

        LinearLayout llDetections = rootView.findViewById(R.id.llDetections);
        llDetections.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Detections.class);
            startActivity(intent);
        });

        LinearLayout llVaccines = rootView.findViewById(R.id.llVaccines);
        llVaccines.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), VaccinesActivity.class);
            startActivity(intent);
        });

        LinearLayout llCleanliness = rootView.findViewById(R.id.llCleanliness);
        llCleanliness.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CleanlinessActivity.class);
            startActivity(intent);
        });

        return rootView;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}