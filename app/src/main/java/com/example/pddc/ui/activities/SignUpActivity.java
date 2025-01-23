package com.example.pddc.ui.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
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

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR );


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

        MaterialButton btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String houseName = etHouseName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phoneNo = etPhoneNo.getText().toString().trim();

            if (validateInputs(fullName, houseName, email, phoneNo, etFullName, etHouseName, etEmail, etPhoneNo)) {
                checkIfUserExists(fullName, houseName, email, phoneNo);
            }
        });
    }

    private boolean validateInputs(String fullName, String houseName, String email, String phoneNo,
                                   EditText etFullName, EditText etHouseName, EditText etEmail, EditText etPhoneNo) {
        boolean isValid = true;

        if (fullName.isEmpty()) {
            setErrorHint(etFullName, "Please enter your Full Name!");
            isValid = false;
        }
        if (houseName.isEmpty()) {
            setErrorHint(etHouseName, "Please enter your Poultry House Name!");
            isValid = false;
        }
        if (email.isEmpty()) {
            setErrorHint(etEmail, "Please enter your Email!");
            isValid = false;
        }
        if (phoneNo.isEmpty()) {
            setErrorHint(etPhoneNo, "Please enter your Phone Number!");
            isValid = false;
        }

        if (!isValid) {
            showAlertDialog("Please fill all the required information.");
        }
        return isValid;
    }

    private void setErrorHint(EditText editText, String message) {
        SpannableString hint = new SpannableString(message);
        hint.setSpan(new ForegroundColorSpan(Color.RED), 0, hint.length(), 0);
        editText.setHint(hint);
    }

    private void showAlertDialog(String message) {
        new AlertDialog.Builder(SignUpActivity.this)
                .setTitle("Error!")
                .setMessage(message)
                .setCancelable(true)
                .setNegativeButton("Ok", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void checkIfUserExists(String fullName, String houseName, String email, String phoneNo) {
        CollectionReference usersRef = db.collection("Users");

        usersRef
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        showAlertDialog("An account with this email already exists!");
                    } else {
                        saveUserDetails(fullName, houseName, email, phoneNo);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(SignUpActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveUserDetails(String fullName, String houseName, String email, String phoneNo) {
        Map<String, Object> user = new HashMap<>();
        user.put("fullName", fullName);
        user.put("houseName", houseName);
        user.put("email", email);
        user.put("phoneNo", phoneNo);

        db.collection("Users")
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(SignUpActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                })
                .addOnFailureListener(e -> Toast.makeText(SignUpActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SignUpActivity.this, WelcomePage.class);
        startActivity(intent);
        finish();

    }
}
