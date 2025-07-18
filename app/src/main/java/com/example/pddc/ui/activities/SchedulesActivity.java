package com.example.pddc.ui.activities;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pddc.R;
import com.example.pddc.ui.adapters.SchedulesAdapter;
import com.example.pddc.ui.classes.ReminderReceiver;
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
    private RecyclerView rvSchedules;
    private SchedulesAdapter schedulesAdapter;
    private List<Schedules> schedules;
    ImageButton btnBack;
    TextView tvNoSchedule;

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
        rvSchedules = findViewById(R.id.rvSchedules);

        //Setting up RecyclerView
        schedules = new ArrayList<>();
        schedulesAdapter = new SchedulesAdapter(schedules);
        rvSchedules.setLayoutManager(new LinearLayoutManager(this));
        rvSchedules.setAdapter(schedulesAdapter);


        // Button to open Bottom Sheet
        ImageButton ibAdd = findViewById(R.id.ibAdd);
        ibAdd.setOnClickListener(v -> showBottomSheetDialog());
    }

    private void showBottomSheetDialog() {
        // Inflate Bottom Sheet Layout
        RelativeLayout rlSchedulesBottomSheet = findViewById(R.id.rlSchedulesBottomSheet);
        View view = LayoutInflater.from(this).inflate(R.layout.schedules_bottom_sheet, rlSchedulesBottomSheet);
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
        EditText etTime = view.findViewById(R.id.etTime);
        MaterialButton btnSave = view.findViewById(R.id.btnSave);

        // Date Picker on EditText Click
        etDate.setOnClickListener(v -> showDatePicker(etDate));

        etTime.setOnClickListener(v -> showTimePicker(etTime));

        // Save Button Click Listener
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String time = etTime.getText().toString().trim();

            // Show Toast with Name & Date
            if (!name.isEmpty() && !date.isEmpty() && !time.isEmpty()) {
                scheduleReminder(etName, name, date, time);
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
                    @SuppressLint("DefaultLocale") String selectedDate = year1 + "-" + String.format("%02d", (month1 + 1)) + "-" + String.format("%02d", dayOfMonth);
                    etDate.setText(selectedDate);
                },
                year, month, day);

        datePickerDialog.show();
    }

    /** Show Time Picker **/
    private void showTimePicker(EditText etTime) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog tpd = new TimePickerDialog(this,
                (View, h, m) -> {
            calendar.set(Calendar.HOUR_OF_DAY, h);
            calendar.set(Calendar.MINUTE, m);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            String selectedTime = String.format(Locale.getDefault(),"%02d:%02d", h, m);
            etTime.setText(selectedTime);
        }, hour, minute, true);
        tpd.show();
    }


    @SuppressLint({"ScheduleExactAlarm", "NotifyDataSetChanged"})
    private void scheduleReminder(EditText etName, String name, String date, String time) {
        String reminderText = etName.getText().toString().trim();

        final Calendar calendar = Calendar.getInstance();
        long triggerTime = calendar.getTimeInMillis();

        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("reminder_text", reminderText);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
        tvNoSchedule = findViewById(R.id.tvNoSchedule);
        tvNoSchedule.setVisibility(View.GONE);
        schedules.add(new Schedules(name, time, date, true));
        schedulesAdapter.notifyDataSetChanged();
        rvSchedules.scrollToPosition(schedules.size() -1);
        Toast.makeText(SchedulesActivity.this, "Reminder set Successfully!", Toast.LENGTH_SHORT).show();
    }

    /** Handling Real Date **/
    private String getDayOfWeekString(int dayOfWeek) {
        String[] daysOfWeek = {"Sunday,","Monday,","Tuesday,","Wednesday,","Thursday,","Friday,","Saturday,"};
        return daysOfWeek[dayOfWeek -1];
    }
}