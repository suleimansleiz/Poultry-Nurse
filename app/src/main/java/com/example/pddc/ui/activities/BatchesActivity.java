package com.example.pddc.ui.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pddc.ui.classes.N_RecyclerViewAdapter;
import com.example.pddc.ui.classes.NotificationModel;
import com.example.pddc.R;
import com.example.pddc.ui.classes.RecyclerViewInterface;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class BatchesActivity extends AppCompatActivity implements RecyclerViewInterface {

    AlertDialog.Builder builder;
    N_RecyclerViewAdapter adapter;
    ArrayList<NotificationModel> notificationModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batches);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(BatchesActivity.this, MainActivity.class);
            startActivity(intent);
            finishAffinity();
        });

        RecyclerView rv_Notifications = findViewById(R.id.rvNotifications);

        setNotificationModels();

        adapter = new N_RecyclerViewAdapter(this, notificationModels, this);
        rv_Notifications.setAdapter(adapter);
        rv_Notifications.setLayoutManager(new LinearLayoutManager(this));

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
    }

    /** Handling Real Date **/
    private String getDayOfWeekString(int dayOfWeek) {
        String[] daysOfWeek = {"Sunday,","Monday,","Tuesday,","Wednesday,","Thursday,","Friday,","Saturday,"};
        return daysOfWeek[dayOfWeek -1];
    }


    private void setNotificationModels(){
        String[] aiName = getResources().getStringArray(R.array.name_AI);
        String[] aiNote = getResources().getStringArray(R.array.rv_Note);
        String[] aiNoteBig = getResources().getStringArray(R.array.note_inText);
        for (int i = 0; i < aiName.length; i++){
            notificationModels.add(new NotificationModel(aiName[i], aiNote[i], aiNoteBig[i]));
    }
        }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(BatchesActivity.this, InnerNotification.class);
        intent.putExtra("PAGE_HEADER", notificationModels.get(position).getAiName());
        intent.putExtra("NAME", notificationModels.get(position).getAiNote());
        intent.putExtra("DESCRIPTION", notificationModels.get(position).getAiNoteBig());
        startActivity(intent);
    }


    @Override
    public void onItemLongClick(int position) {
        builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you wish to delete this batch");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            notificationModels.remove(position);
            adapter.notifyItemRemoved(position);
        })
        .setNegativeButton("No", (dialog, which) -> dialog.dismiss()).show();
    }
}