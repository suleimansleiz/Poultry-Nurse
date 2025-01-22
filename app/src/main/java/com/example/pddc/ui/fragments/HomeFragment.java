package com.example.pddc.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pddc.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HomeFragment extends Fragment {

    TextView tvFarmName, tvFullName;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        if (getArguments() != null) {
            String fullName = getArguments().getString("fullName");
            String farmName = getArguments().getString("farmName");

            tvFarmName = requireView().findViewById(R.id.tvPHouseName);
            tvFarmName.setText(farmName);
            tvFullName = requireView().findViewById(R.id.tvUserName);
            tvFullName.setText(fullName);
        }

        TextView tv_Day = rootView.findViewById(R.id.tvDay);
        TextView tv_Date = rootView.findViewById(R.id.tvDate);




        //Current day
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        //Current date

        String dayOfWeekString = getDayOfWeekString(dayOfWeek);

        //Date format
        java.lang.String dateFormat = "d MMMM, yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
        String dateString = simpleDateFormat.format(Calendar.getInstance().getTime());

        //Updating day and date.
        tv_Day.setText(dayOfWeekString);
        tv_Date.setText(dateString);

        return rootView;
    }

    private String getDayOfWeekString(int dayOfWeek) {
            String[] daysOfWeek = {"Sunday,","Monday,","Tuesday,","Wednesday,","Thursday,","Friday,","Saturday,"};
        return daysOfWeek[dayOfWeek -1];
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}