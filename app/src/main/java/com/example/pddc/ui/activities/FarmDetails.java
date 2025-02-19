package com.example.pddc.ui.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pddc.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class FarmDetails extends AppCompatActivity {

    //Small farms Payment plans
    String monthPaySmall = "15000";
    String threeMonthPaySmall = "40000";
    String sixMonthPaySmall = "80000";
    String twelveMonthPaySmall = "150000";

    //Medium farms Payment plans
    String monthPayMedium = "25000";
    String threeMonthPayMedium = "70000";
    String sixMonthPayMedium = "100000";
    String twelveMonthPayMedium = "250000";

    //Large farms Payment plans
    String monthPayLarge = "40000";
    String threeMonthPayLarge = "110000";
    String sixMonthPayLarge = "200000";
    String twelveMonthPayLarge = "450000";


    TextView tvTrial, tvMonthPayment, tvThreeMonthsPayment, tvSixMonthsPayment, tvTwelveMonthsPayment, membershipNo, farmName, fullName, email, phoneNo, farmLocation, region, chickenNo, farmSize;
    private FirebaseFirestore db;


    @SuppressLint("DefaultLocale")
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


        //Initializing Views
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

        SharedPreferences userCredPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        String farmerId = userCredPreferences.getString("farmerId", "");

        if (!farmerId.isEmpty()) {
            fetchUserDetails(farmerId);
        }

        tvTrial = findViewById(R.id.tvTrial);
        int daysLeft = getRemainingDays();
        tvTrial.setText(String.format("Your Trial expires in %d days", daysLeft));
    }

    /** calculating remaining days **/
    @SuppressLint("CommitPrefEdits")
    public int getRemainingDays() {
        SharedPreferences prefs = getSharedPreferences("AppTrialPrefs", MODE_PRIVATE);
        long createdTime = prefs.getLong("account_created", 0);
        int lastDismissedDay = prefs.getInt("last_dismissed_day", -1);

        if (createdTime == 0) {
            createdTime = System.currentTimeMillis();
            prefs.edit().putLong("account_created", createdTime);
            return 30;
        }

        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime -createdTime;
        long daysPassed = TimeUnit.MILLISECONDS.toDays(elapsedTime);

        int remainingDays = (int) (30 - daysPassed);
        if ((remainingDays == 25 || remainingDays == 24 || remainingDays == 2 || remainingDays == 1) && lastDismissedDay != remainingDays) {
            showRemainingTrialDaysDialog(remainingDays);
        }

        return Math.max(remainingDays, 0);
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
                        String chicken_No = documentSnapshot.getString("chickenNo");

                        membershipNo.setText(membership_no);
                        fullName.setText(full_Name);
                        farmName.setText(farm_Name);
                        email.setText(tv_email);
                        phoneNo.setText(String.format("255%s", phone_No));
                        farmLocation.setText(farm_location);
                        region.setText(tv_region);
                        farmSize.setText(farm_Size);
                        chickenNo.setText(chicken_No);

                        checkFarmSize(farm_Size);
                    } else {
                        Log.d("Firestore", "No such document");
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(FarmDetails.this, "Error fetching details!", Toast.LENGTH_SHORT).show());

        // Button to open Bottom Sheet
        MaterialButton mbPayNow = findViewById(R.id.mbPayNow);
        mbPayNow.setOnClickListener(v -> showBottomSheetDialog());
    }

    /** Checking Farm Size **/
    private void checkFarmSize(String farm_Size) {
        // Inflate Bottom Sheet Layout
        LinearLayout llPayment_bottom_sheet = findViewById(R.id.llPayment_bottom_sheet);
        View view = LayoutInflater.from(this).inflate(R.layout.payment_bottom_sheet, llPayment_bottom_sheet);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);

        TextView tvMonthPayment = view.findViewById(R.id.tvMonthPayment);
        TextView tvThreeMonthsPayment = view.findViewById(R.id.tvThreeMonthsPayment);
        TextView tvSixMonthsPayment = view.findViewById(R.id.tvSixMonthsPayment);
        TextView tvTwelveMonthsPayment = view.findViewById(R.id.tvTwelveMonthsPayment);

        if (Objects.equals(farm_Size, "Small")){
            tvMonthPayment.setText(String.format("%s Tsh", monthPaySmall));
            tvThreeMonthsPayment.setText(String.format("%s Tsh", threeMonthPaySmall));
            tvSixMonthsPayment.setText(String.format("%s Tsh", sixMonthPaySmall));
            tvTwelveMonthsPayment.setText(String.format("%s Tsh", twelveMonthPaySmall));
        }
        if (Objects.equals(farm_Size, "Medium")){
            tvMonthPayment.setText(String.format("%s Tsh", monthPayMedium));
            tvThreeMonthsPayment.setText(String.format("%s Tsh", threeMonthPayMedium));
            tvSixMonthsPayment.setText(String.format("%s Tsh", sixMonthPayMedium));
            tvTwelveMonthsPayment.setText(String.format("%s Tsh", twelveMonthPayMedium));
        }
        if (Objects.equals(farm_Size, "Large")) {
            tvMonthPayment.setText(String.format("%s Tsh", monthPayLarge));
            tvThreeMonthsPayment.setText(String.format("%s Tsh", threeMonthPayLarge));
            tvSixMonthsPayment.setText(String.format("%s Tsh", sixMonthPayLarge));
            tvTwelveMonthsPayment.setText(String.format("%s Tsh", twelveMonthPayLarge));
        }
    }

    /** Opening Payment Plan Bottom Sheet**/
    private void showBottomSheetDialog() {
        LinearLayout llPayment_bottom_sheet = findViewById(R.id.llPayment_bottom_sheet);
        // Inflate Bottom Sheet Layout
        View view = LayoutInflater.from(this).inflate(R.layout.payment_bottom_sheet, llPayment_bottom_sheet);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);
        if (bottomSheetDialog.getWindow() != null) {
            bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        bottomSheetDialog.show();

        // Enable drag behavior
        View parent = (View) view.getParent();
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(parent);
        bottomSheetBehavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        // Reference Views
        MaterialButton btnPayOneMonth = view.findViewById(R.id.btnPayOneMonth);
        MaterialButton btnPayThreeMonths = view.findViewById(R.id.btnPayThreeMonths);
        MaterialButton btnPaySixMonths = view.findViewById(R.id.btnPaySixMonths);
        MaterialButton btnPayTwelveMonths = view.findViewById(R.id.btnPayTwelveMonths);

        btnPayOneMonth.setOnClickListener(v -> {
            String monthAmount = tvMonthPayment.getText().toString().trim();
            openMonthPaymentDialog(monthAmount);
        });
        btnPayThreeMonths.setOnClickListener(v -> {
            String threeMonthsAmount = tvThreeMonthsPayment.getText().toString().trim();
            openThreeMonthsPaymentDialog(threeMonthsAmount);
        });
        btnPaySixMonths.setOnClickListener(v -> {
            String sixMonthsAmount = tvSixMonthsPayment.getText().toString().trim();
            openSixMonthsPaymentDialog(sixMonthsAmount);
        });
        btnPayTwelveMonths.setOnClickListener(v -> {
            String twelveMonthsAmount = tvTwelveMonthsPayment.getText().toString().trim();
            openTwelveMonthsPaymentDialog(twelveMonthsAmount);
        });
    }

    /** Inserting Payment Number **/
    private void openMonthPaymentDialog(String monthAmount) {
        LinearLayout llPayment_number_dialog = findViewById(R.id.llPayment_number_dialog);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.payment_number_dialog, llPayment_number_dialog);

        //Referencing Views
        TextView tvAmountToBePaid = dialogView.findViewById(R.id.tvAmountToBePaid);
        EditText etPaymentNo = dialogView.findViewById(R.id.etPaymentNo);
        MaterialButton btnContinue = dialogView.findViewById(R.id.btnContinue);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancel);

        AlertDialog.Builder builder = new AlertDialog.Builder(FarmDetails.this);
        builder.setView(dialogView)
                .setCancelable(true);
        tvAmountToBePaid.setText(String.format("You are about to pay %s as a month payment.", monthAmount));
        AlertDialog dialog = builder.create();

        btnContinue.setOnClickListener(v -> {
            Toast.makeText(FarmDetails.this, "USSD Push Notification will be integrated soon!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();
    }

    private void openThreeMonthsPaymentDialog(String threeMonthsAmount) {
        LinearLayout llPayment_number_dialog = findViewById(R.id.llPayment_number_dialog);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.payment_number_dialog, llPayment_number_dialog);

        //Referencing Views
        TextView tvAmountToBePaid = dialogView.findViewById(R.id.tvAmountToBePaid);
        EditText etPaymentNo = dialogView.findViewById(R.id.etPaymentNo);
        MaterialButton btnContinue = dialogView.findViewById(R.id.btnContinue);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancel);

        AlertDialog.Builder builder = new AlertDialog.Builder(FarmDetails.this);
        builder.setView(dialogView)
                .setCancelable(true);
        tvAmountToBePaid.setText(String.format("You are about to pay %s as a three months payment.", threeMonthsAmount));
        final AlertDialog dialog = builder.create();

        btnContinue.setOnClickListener(v -> {
            Toast.makeText(FarmDetails.this, "USSD Push Notification will be integrated soon!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();
    }

    private void openSixMonthsPaymentDialog(String sixMonthsAmount) {
        LinearLayout llPayment_number_dialog = findViewById(R.id.llPayment_number_dialog);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.payment_number_dialog, llPayment_number_dialog);

        //Referencing Views
        TextView tvAmountToBePaid = dialogView.findViewById(R.id.tvAmountToBePaid);
        EditText etPaymentNo = dialogView.findViewById(R.id.etPaymentNo);
        MaterialButton btnContinue = dialogView.findViewById(R.id.btnContinue);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancel);

        AlertDialog.Builder builder = new AlertDialog.Builder(FarmDetails.this);
        builder.setView(dialogView)
                .setCancelable(true);
        tvAmountToBePaid.setText(String.format("You are about to pay %s as a six months payment.", sixMonthsAmount));
        AlertDialog dialog = builder.create();

        btnContinue.setOnClickListener(v -> {
            Toast.makeText(FarmDetails.this, "USSD Push Notification will be integrated soon!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();
    }

    private void openTwelveMonthsPaymentDialog(String twelveMonthsAmount) {
        LinearLayout llPayment_number_dialog = findViewById(R.id.llPayment_number_dialog);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.payment_number_dialog, llPayment_number_dialog);

        //Referencing Views
        TextView tvAmountToBePaid = dialogView.findViewById(R.id.tvAmountToBePaid);
        EditText etPaymentNo = dialogView.findViewById(R.id.etPaymentNo);
        MaterialButton btnContinue = dialogView.findViewById(R.id.btnContinue);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancel);

        AlertDialog.Builder builder = new AlertDialog.Builder(FarmDetails.this);
        builder.setView(dialogView)
                .setCancelable(true);
        tvAmountToBePaid.setText(String.format("You are about to pay %s as a twelve months payment.", twelveMonthsAmount));
        AlertDialog dialog = builder.create();

        btnContinue.setOnClickListener(v -> {
            Toast.makeText(FarmDetails.this, "USSD Push Notification will be integrated soon!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();
    }

    private void showRemainingTrialDaysDialog(int remainingDays){
        LinearLayout llTrialRemainderDialog = findViewById(R.id.llTrialRemainderDialog);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.trial_dialog, llTrialRemainderDialog);

        //Referencing Views
        TextView tvTrialDaysRemaining = dialogView.findViewById(R.id.tvTrialDaysRemaining);
        MaterialButton btnPayNow = dialogView.findViewById(R.id.btnPayNow);
        TextView tvCancel = dialogView.findViewById(R.id.tvCancel);

        AlertDialog.Builder builder = new AlertDialog.Builder(FarmDetails.this);
        builder.setView(dialogView)
                .setCancelable(true);
        tvTrialDaysRemaining.setText(String.format("Your Trial expires in %s days.", remainingDays));

        AlertDialog dialog = builder.create();

        btnPayNow.setOnClickListener(v -> {
            showBottomSheetDialog();
            dialog.dismiss();
        });

        tvCancel.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("AppTrialPrefs", MODE_PRIVATE);
            prefs.edit().putInt("last_dismissed_day", remainingDays).apply();
            dialog.dismiss();
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();
    }

    /** Handling Real Date **/
    private String getDayOfWeekString(int dayOfWeek) {
        String[] daysOfWeek = {"Sunday,","Monday,","Tuesday,","Wednesday,","Thursday,","Friday,","Saturday,"};
        return daysOfWeek[dayOfWeek -1];
    }
}