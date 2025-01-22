package com.example.pddc.ui.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pddc.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = FirebaseFirestore.getInstance(); // Initialize Firestore

        TextView tv_ToSignUp = findViewById(R.id.tvToSignUp);
        tv_ToSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
        TextView tv_ForgetDetails = findViewById(R.id.tvForgetDetails);
        tv_ForgetDetails.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("Apologies!");
            builder.setMessage("Please contact Developer or Sign Up for new account.");
            builder.setCancelable(true);
            builder.setNegativeButton("Ok", (dialog, which) -> dialog.dismiss()).show();
        });

        TextView tv_Skip = findViewById(R.id.tvSkip);
        tv_Skip.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });


        EditText et_HouseName = findViewById(R.id.etHouseName);
        EditText et_PhoneNo = findViewById(R.id.etPhoneNo);
        MaterialButton btn_Login = findViewById(R.id.btnLogin);

        btn_Login.setOnClickListener(v -> {
            String houseName = et_HouseName.getText().toString().trim();
            String phoneNo = et_PhoneNo.getText().toString().trim();

            if (houseName.isEmpty() || phoneNo.isEmpty()) {
                showAlertDialog("Error!", "Please fill all the required fields.");
            } else {
                authenticateUser(houseName, phoneNo);
            }
        });
    }

    // Check Firestore for user details
    private void authenticateUser(String houseName, String phoneNo) {
        db.collection("Users")
                .whereEqualTo("farmName", houseName)
                .whereEqualTo("phoneNo", phoneNo)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        if (task.getResult().isEmpty()) {
                            // Extract user data
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            String fullName = document.getString("fullName");
                            String farmName = document.getString("farmName");
                            String email = document.getString("email");

//                            saveUserSession(fullName, farmName, email);

                            // Redirect to MainActivity
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("fullName", fullName);
                            intent.putExtra("farmName", farmName);
                            intent.putExtra("email", email);
                            startActivity(intent);

                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            // User not found
                            showAlertDialog("Login Failed!", "User details not found. Please sign up first.");
                        }
                    }else {
                         showAlertDialog("Error!", "Failed to connect to the database. Please check your connection.");
                    }
                });

    }


//    private void saveUserSession(String fullName, String farmName, String email) {
//        // Save user session data in SharedPreferences
//        getSharedPreferences("UserSession", MODE_PRIVATE)
//                .edit()
//                .putString("fullName", fullName)
//                .putString("farmName", farmName)
//                .putString("email", email)
//                .apply();
//    }

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}