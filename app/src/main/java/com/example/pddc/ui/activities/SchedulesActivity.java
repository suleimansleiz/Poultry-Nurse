package com.example.pddc.ui.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pddc.R;
import com.example.pddc.ui.adapters.SchedulesAdapter;
import com.example.pddc.ui.classes.Schedules;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SchedulesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SchedulesAdapter schedulesAdapter;
    private List<Schedules> schedules;
    ImageButton btnBack;
    TextView etArrivalDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedules);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(SchedulesActivity.this, MainActivity.class);
            startActivity(intent);
            finishAffinity();
        });

        //Displaying Real Date
        TextView tv_Day = findViewById(R.id.tvDay);
        TextView tv_Date = findViewById(R.id.tvDate);

        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);//Current day

        String dayOfWeekString = getDayOfWeekString(dayOfWeek);//Current date

        //Date format
        java.lang.String dateFormat = "d MMMM, yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
        String dateString = simpleDateFormat.format(Calendar.getInstance().getTime());

        //Updating day and date.
        tv_Day.setText(dayOfWeekString);
        tv_Date.setText(dateString);

        //Initializing Views
        recyclerView = findViewById(R.id.rvSchedules);
        btnBack = findViewById(R.id.btnBack);

        //Setting up RecyclerView
        schedules = new ArrayList<>();
        schedulesAdapter = new SchedulesAdapter(schedules);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(schedulesAdapter);


        // Button to open Bottom Sheet
        ImageButton ibAdd = findViewById(R.id.ibAdd);
        ibAdd.setOnClickListener(v -> showBottomSheetDialog());
    }

    private void showBottomSheetDialog() {
        // Inflate Bottom Sheet Layout
        View view = LayoutInflater.from(this).inflate(R.layout.schedules_bottom_sheet, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        // Enable drag behavior
        View parent = (View) view.getParent();
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(parent);
        bottomSheetBehavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        // Reference Views
        EditText etName = view.findViewById(R.id.etName);
        EditText etDate = view.findViewById(R.id.etDate);
        MaterialButton btnSave = view.findViewById(R.id.btnSave);

        // Date Picker on EditText Click
        etDate.setOnClickListener(v -> showDatePicker(etDate));

        // Save Button Click Listener
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String date = etDate.getText().toString().trim();

            // Show Toast with Name & Date
            if (!name.isEmpty() && !date.isEmpty()) {
                Toast.makeText(SchedulesActivity.this, "Name: " + name + "\nDate: " + date, Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            } else {
                Toast.makeText(SchedulesActivity.this, "Please enter both Name and Date!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePicker(EditText etDate) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    String selectedDate = year1 + "-" + String.format("%02d", (month1 + 1)) + "-" + String.format("%02d", dayOfMonth);
                    etDate.setText(selectedDate);
                },
                year, month, day);

        datePickerDialog.show();
    }


    /** Handling Real Date **/
    private String getDayOfWeekString(int dayOfWeek) {
        String[] daysOfWeek = {"Sunday,","Monday,","Tuesday,","Wednesday,","Thursday,","Friday,","Saturday,"};
        return daysOfWeek[dayOfWeek -1];
    }
}