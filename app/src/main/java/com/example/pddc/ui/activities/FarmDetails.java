package com.example.pddc.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pddc.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FarmDetails extends AppCompatActivity {

    TextView membershipNo, farmName, fullName, email, phoneNo, farmLocation, region, chickenNo, farmSize, noOfFarms;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_details);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(FarmDetails.this, MainActivity.class);
            startActivity(intent);
            finish();
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


        //Passing User Credentials
        db = FirebaseFirestore.getInstance();

        membershipNo = findViewById(R.id.membershipNo);
        farmName = findViewById(R.id.farmName);
        fullName = findViewById(R.id.fullName);
        email = findViewById(R.id.email);
        phoneNo = findViewById(R.id.phoneNo);
        farmLocation = findViewById(R.id.farmLocation);
        region = findViewById(R.id.region);
        chickenNo = findViewById(R.id.chickenNo);
        farmSize = findViewById(R.id.farmSize);
        noOfFarms = findViewById(R.id.noOfFarms);

        SharedPreferences userCredPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        String farmerId = userCredPreferences.getString("farmerId", "");

        if (!farmerId.isEmpty()) {
            fetchUserDetails(farmerId);
        }
    }

    /** Fetching details from db **/
    private void fetchUserDetails(String farmerId) {
        db.collection("Users").document(farmerId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String membership_no = documentSnapshot.getString("membershipNo");
                        String full_Name = documentSnapshot.getString("fullName");
                        String farm_Name = documentSnapshot.getString("houseName");
                        String tv_email = documentSnapshot.getString("email");
                        String phone_No = documentSnapshot.getString("phoneNo");
                        String farm_location = documentSnapshot.getString("farmLocation");
                        String tv_region = documentSnapshot.getString("region");
                        String farm_Size = documentSnapshot.getString("farmSize");
                        String noOf_Farms = documentSnapshot.getString("numberOfFarms");
                        String chicken_No = documentSnapshot.getString("chickenNo");

                        membershipNo.setText(membership_no);
                        fullName.setText(full_Name);
                        farmName.setText(farm_Name);
                        email.setText(tv_email);
                        phoneNo.setText(phone_No);
                        farmLocation.setText(farm_location);
                        region.setText(tv_region);
                        farmSize.setText(farm_Size);
                        noOfFarms.setText(noOf_Farms);
                        chickenNo.setText(chicken_No);
                    } else {
                        Log.d("Firestore", "No such document");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching data", e));
    }

    /** Handling Real Date **/
    private String getDayOfWeekString(int dayOfWeek) {
        String[] daysOfWeek = {"Sunday,","Monday,","Tuesday,","Wednesday,","Thursday,","Friday,","Saturday,"};
        return daysOfWeek[dayOfWeek -1];
    }
}