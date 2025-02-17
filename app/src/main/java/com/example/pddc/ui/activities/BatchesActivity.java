package com.example.pddc.ui.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pddc.R;
import com.example.pddc.ui.adapters.BatchesAdapter;
import com.example.pddc.ui.classes.BatchesModel;
import com.example.pddc.ui.classes.RVBatchesInterface;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BatchesActivity extends AppCompatActivity implements RVBatchesInterface {

    AlertDialog.Builder builder;
    BatchesAdapter adapter;
    List<BatchesModel> batchesModels;

    RecyclerView rvBatches;
    private FirebaseFirestore db;

    String farmerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batches);

        FirebaseApp.initializeApp(this);

        db = FirebaseFirestore.getInstance();

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(BatchesActivity.this, MainActivity.class);
            startActivity(intent);
            finishAffinity();
        });

        //Initializing Views
        rvBatches = findViewById(R.id.rvBatches);

        batchesModels = new ArrayList<>();
        adapter = new BatchesAdapter(batchesModels);
        rvBatches.setAdapter(adapter);
        rvBatches.setLayoutManager(new LinearLayoutManager(this));

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

        //Calling stored preference
        SharedPreferences userCredPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        farmerId = userCredPreferences.getString("farmerId", "");

        // Button to open Bottom Sheet
        ImageButton ibAdd = findViewById(R.id.ibAdd);
        ibAdd.setOnClickListener(v -> showBottomSheetDialog());

        //Loading Batches from db
        TextView tvBatchesAvailable = findViewById(R.id.tvBatchesAvailable);
        ShowAvailableBatches(tvBatchesAvailable);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void ShowAvailableBatches(TextView tvBatchesAvailable) {

      db.collection("Users").document(farmerId).collection("Batches")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()){
                        if (documentSnapshot.exists()) {
                            String batchName = documentSnapshot.getString("batchName");
                            String arrivalDate = documentSnapshot.getString("arrivalDate");
                            String chicksAge = documentSnapshot.getString("chicksAge");
                            String chicksNo = documentSnapshot.getString("chicksNo");

                            tvBatchesAvailable.setVisibility(View.GONE);
                            batchesModels.add(new BatchesModel( batchName, arrivalDate, "(" + chicksAge + " weeks)", chicksNo +" chicks", true));
                            adapter.notifyDataSetChanged();
                            rvBatches.scrollToPosition(batchesModels.size() -1);
                        } else {
                            tvBatchesAvailable.setVisibility(View.VISIBLE);
                        }
                    }
                })
              .addOnFailureListener(e -> Toast.makeText(BatchesActivity.this, "Error fetching details!", Toast.LENGTH_SHORT).show());
    }

    /** Handling Real Date **/
    private String getDayOfWeekString(int dayOfWeek) {
        String[] daysOfWeek = {"Sunday,","Monday,","Tuesday,","Wednesday,","Thursday,","Friday,","Saturday,"};
        return daysOfWeek[dayOfWeek -1];
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showBottomSheetDialog() {
        // Inflate Bottom Sheet Layout
        RelativeLayout rlBatches_bottom_sheet = findViewById(R.id.rlBatches_bottom_sheet);
        View view = LayoutInflater.from(this).inflate(R.layout.batch_bottom_sheet, rlBatches_bottom_sheet);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        // Enable drag behavior
        View parent = (View) view.getParent();
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(parent);
        bottomSheetBehavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        // Reference Views
        EditText etBatchName = view.findViewById(R.id.etBatchName);
        EditText etChicksAge = view.findViewById(R.id.etChicksAge);
        EditText etChicksNo = view.findViewById(R.id.etChicksNo);
        EditText etDate = view.findViewById(R.id.etArrivalDate);
        MaterialButton btnSave = view.findViewById(R.id.btnAddBatch);

        // Date Picker on EditText Click
        etDate.setOnClickListener(v -> showDatePicker(etDate));

        // Save Button Click Listener
        btnSave.setOnClickListener(v -> {
            String batchName = etBatchName.getText().toString().trim();
            String chicksAge = etChicksAge.getText().toString().trim();
            String chicksNo = etChicksNo.getText().toString().trim();
            String arrivalDate = etDate.getText().toString().trim();

            if (!batchName.isEmpty() && !arrivalDate.isEmpty() && !chicksAge.isEmpty() && !chicksNo.isEmpty()) {
                //Storing the batches to db
                if (!farmerId.isEmpty()) {
                        CollectionReference batchesRef = db.collection("Users").document(farmerId).collection("Batches");
                        batchesRef
                                .whereEqualTo("batchName", batchName)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                        Toast.makeText(BatchesActivity.this, "Batch with this name already exists!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        storeBatchDetails(farmerId, batchName, chicksNo, chicksAge, arrivalDate, bottomSheetDialog);
                                        recreate();
                                    }
                                })
                                .addOnFailureListener(e -> Toast.makeText(BatchesActivity.this, "Error saving batch details!", Toast.LENGTH_SHORT).show());
                }
                bottomSheetDialog.dismiss();
            } else {
                Toast.makeText(BatchesActivity.this, "Please fill in all the required details!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Saving batch details in db **/
    private void storeBatchDetails(String farmerId, String batchName, String chicksNo, String chicksAge, String arrivalDate, BottomSheetDialog bottomSheetDialog) {
        Map<String, Object> batch = new HashMap<>();
        batch.put("batchName", batchName);
        batch.put("chicksNo", chicksNo);
        batch.put("chicksAge", chicksAge);
        batch.put("arrivalDate", arrivalDate);

        db.collection("Users").document(farmerId).collection("Batches")
                .add(batch)
                .addOnSuccessListener(documentReference -> bottomSheetDialog.dismiss())
                .addOnFailureListener(e -> Toast.makeText(BatchesActivity.this, "Error saving batch details!", Toast.LENGTH_SHORT).show());
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

    @Override
    public void onItemLongClick(int position) {
        LinearLayout llDeleteBatch = findViewById(R.id.llDeleteBatch);
        View view = LayoutInflater.from(this).inflate(R.layout.delete_batch_dialog, llDeleteBatch);

        TextView tvYesDelete = view.findViewById(R.id.tvYesDelete);
        TextView tvDeleteNot = view.findViewById(R.id.tvDeleteNot);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BatchesActivity.this);
        alertDialog.setView(view)
            .setCancelable(true);

        AlertDialog dialog = builder.create();
        tvYesDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                batchesModels.remove(position);
                adapter.notifyItemRemoved(position);
            }
        });

        tvDeleteNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();
    }
}