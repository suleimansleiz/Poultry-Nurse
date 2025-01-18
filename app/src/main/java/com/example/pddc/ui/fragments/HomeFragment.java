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


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        TextView tvUserName = rootView.findViewById(R.id.tvUserName);
        TextView tv_PHouseName = rootView.findViewById(R.id.tvPHouseName);

        TextView tv_Day = rootView.findViewById(R.id.tvDay);
        TextView tv_Date = rootView.findViewById(R.id.tvDate);

        //Current day
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        //Current date
        int date = Calendar.getInstance().get(Calendar.DATE);

        String dayOfWeekString = getDayOfWeekString(dayOfWeek);

        //Date format
        String dateFormat = "d MMMM, yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
        String dateString = simpleDateFormat.format(Calendar.getInstance().getTime());

        //Updating day and date.
        tv_Day.setText(dayOfWeekString);
        tv_Date.setText(dateString);

        return rootView;
    }

    //Helper method converting dat of week ints to Strings
    private String getDayOfWeekString(int dayOfWeek) {
            String[] daysOfWeek = {"Sunday,","Monday,","Tuesday,","Wednesday,","Thursday,","Friday,","Saturday,"};
        return daysOfWeek[dayOfWeek -1];
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}