package com.example.pddc.ui.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pddc.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    String[] farm_size = {"Small", "Medium", "Large"};
    String[] numberOfChicken = {"Less than 50", "Between 50 and 300", "More than 300"};
    AutoCompleteTextView acFarmSize, acNumberOfChicken;
    ArrayAdapter<String> adapterFarmSize, adapterNumberOfChicken;
    private FirebaseFirestore db;

    String idLetter = "P25-Farm", membershipNo;

    String selectedFarmSize, selectedChickenNo;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        //AutoComplete Input Field.
        acFarmSize = findViewById(R.id.acFarmSize);
        acFarmSize.setAdapter(adapterFarmSize);
        if (getBaseContext() != null) {
            adapterFarmSize = new ArrayAdapter<>(getBaseContext(), R.layout.list_item, farm_size);
            acFarmSize.setAdapter(adapterFarmSize);
        }
        acFarmSize.setOnItemClickListener((parent, view, position, id) -> selectedFarmSize = (String) parent.getItemAtPosition(position));


        acNumberOfChicken = findViewById(R.id.acNumberOfChicken);
        if (getBaseContext() != null) {
            adapterNumberOfChicken = new ArrayAdapter<>(getBaseContext(), R.layout.list_item, numberOfChicken);
            acNumberOfChicken.setAdapter(adapterNumberOfChicken);
        }

        acNumberOfChicken.setOnItemClickListener((parent, view, position, id) -> selectedChickenNo = (String) parent.getItemAtPosition(position));


        //Firestore Initialization
        db = FirebaseFirestore.getInstance();

        TextView tvToLogin = findViewById(R.id.tvToLogin);
        tvToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        EditText etFullName = findViewById(R.id.etFullName);
        EditText etHouseName = findViewById(R.id.etHouseName);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPhoneNo = findViewById(R.id.etPhoneNo);
        EditText etRegion = findViewById(R.id.etRegion);
        EditText etFarmLocation = findViewById(R.id.etFarmLocation);

        etFullName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        etHouseName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        etPhoneNo.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        etRegion.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        etFarmLocation.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        //Performing SignUp
        MaterialButton btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(v -> {
            String userId = idLetter + "-";
            String fullName = etFullName.getText().toString().trim();
            String houseName = etHouseName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phoneNo = etPhoneNo.getText().toString().trim();
            String region = etRegion.getText().toString().trim();
            String farmLocation = etFarmLocation.getText().toString().trim();
            String farmSize = selectedFarmSize.trim();
            String chickenNo = selectedChickenNo.trim();
            membershipNo = userId + phoneNo;


            if (houseName.isEmpty() || fullName.isEmpty() || email.isEmpty() || farmLocation.isEmpty() || phoneNo.isEmpty() || region.isEmpty() || chickenNo.isEmpty() || farmSize.isEmpty()){
                showAlertDialog("Please fill all the required information.");
            }else if (isOnline()) {
                btnSignUp.setEnabled(false);
                btnSignUp.setText(R.string.signing_up);
                checkIfUserExists(membershipNo, userId, fullName, houseName, email, phoneNo, region, farmLocation, farmSize, chickenNo);
            } else {
                btnSignUp.setEnabled(true);
                btnSignUp.setText(R.string.sign_up);
                showAlertDialog("Please check your network and try again");
            }
        });
    }


    private void showAlertDialog(String message) {
        LinearLayout llRandomDialog = findViewById(R.id.llRandomDialog);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.payment_number_dialog, llRandomDialog);

        TextView tvParticularText = dialogView.findViewById(R.id.tvParticularText);
        TextView tvOkContent = dialogView.findViewById(R.id.tvOkContent);

        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                builder.setTitle("Error!")
                .setCancelable(true);

        tvParticularText.setText(message);
        AlertDialog dialog = builder.create();

        tvOkContent.setOnClickListener(View -> dialog.dismiss());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();
    }

    private void checkIfUserExists(String membershipNo, String userId, String fullName, String houseName, String email, String phoneNo, String region, String farmLocation, String farmSize, String chickenNo) {
        this.membershipNo = membershipNo;
        CollectionReference usersRef = db.collection("Users");
        usersRef
                .whereEqualTo("email", email)
                .whereEqualTo("phoneNo", phoneNo)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        showAlertDialog("An account with these credentials already exists!");
                    } else {
                        saveUserDetails(userId, fullName, houseName, email, phoneNo, region, farmLocation, farmSize, chickenNo);
                        SharedPreferences prefs = getSharedPreferences("AppTrialPrefs", MODE_PRIVATE);
                        if (!prefs.contains("account_created")) {
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putLong("account_created", System.currentTimeMillis());
                            editor.apply();
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(SignUpActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveUserDetails(String userId, String fullName, String houseName, String email, String phoneNo, String region, String farmLocation, String farmSize, String chickenNo) {
        Map<String, Object> user = new HashMap<>();
        user.put("membershipNo", membershipNo);
        user.put("fullName", fullName);
        user.put("houseName", houseName);
        user.put("email", email);
        user.put("phoneNo", phoneNo);
        user.put("farmLocation", farmLocation);
        user.put("region", region);
        user.put("farmSize", farmSize);
        user.put("chickenNo", chickenNo);

        db.collection("Users")
                .document(userId + phoneNo)
                .set(user)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(SignUpActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                })
                .addOnFailureListener(e -> Toast.makeText(SignUpActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null){
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnectedOrConnecting();
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SignUpActivity.this, WelcomePage.class);
        startActivity(intent);
        finish();

    }
}
