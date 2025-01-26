package com.example.pddc.ui.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
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

    String selectedFarmSize, selectedChickenNo;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR );

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
        EditText etNumberOfFarms = findViewById(R.id.etNumberOfFarms);

        etFullName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        etHouseName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        etPhoneNo.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        etRegion.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        //Performing SignUp
        MaterialButton btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String houseName = etHouseName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phoneNo = etPhoneNo.getText().toString().trim();
            String region = etRegion.getText().toString().trim();
            String numberOfFarms = etNumberOfFarms.getText().toString().trim();
            String farmSize = selectedFarmSize.trim();
            String chickenNo = selectedChickenNo.trim();


            if (houseName.isEmpty() || fullName.isEmpty() || email.isEmpty() || phoneNo.isEmpty() || region.isEmpty() || numberOfFarms.isEmpty() || chickenNo.isEmpty() || farmSize.isEmpty()){
                showAlertDialog("Please fill all the required information.");
            }else if (isOnline()) {
                btnSignUp.setEnabled(false);
                btnSignUp.setText(R.string.signing_up);
            } else {
                btnSignUp.setEnabled(true);
                showAlertDialog("Please check your network and try again");
            }
        });
    }


    private void showAlertDialog(String message) {
        new AlertDialog.Builder(SignUpActivity.this)
                .setTitle("Error!")
                .setMessage(message)
                .setCancelable(true)
                .setNegativeButton("Ok", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void checkIfUserExists(String fullName, String houseName, String email, String phoneNo, String region, String farmSize, String chickenNo, String numberOfFarms) {
        CollectionReference usersRef = db.collection("Users");

        usersRef
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        showAlertDialog("An account with this email already exists!");
                    } else {
                        saveUserDetails(fullName, houseName, email, phoneNo, region, farmSize, chickenNo, numberOfFarms);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(SignUpActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveUserDetails(String fullName, String houseName, String email, String phoneNo, String region, String farmSize, String chickenNo, String numberOfFarms) {
        Map<String, Object> user = new HashMap<>();
        user.put("fullName", fullName);
        user.put("houseName", houseName);
        user.put("email", email);
        user.put("phoneNo", phoneNo);
        user.put("region", region);
        user.put("farmSize", farmSize);
        user.put("chickenNo", chickenNo);
        user.put("numberOfFarms", numberOfFarms);

        db.collection("Users")
                .add(user)
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
