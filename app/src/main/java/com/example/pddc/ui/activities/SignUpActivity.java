package com.example.pddc.ui.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pddc.R;
import com.google.android.material.button.MaterialButton;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        TextView tvT_oLogin = findViewById(R.id.tvToLogin);
        tvT_oLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        EditText et_FullName = findViewById(R.id.etFullName);
        EditText et_HouseName = findViewById(R.id.etHouseName);
        EditText et_Email = findViewById(R.id.etEmail);

        MaterialButton btn_SignUp = findViewById(R.id.btnSignUp);
        btn_SignUp.setOnClickListener(v -> {
            String fullName = et_FullName.getText().toString().trim();
            String houseName = et_HouseName.getText().toString().trim();
            String email = et_Email.getText().toString().trim();
            if (fullName.isEmpty()){
                SpannableString fullNameHint = new SpannableString("Please enter your Full Name!");
                fullNameHint.setSpan(new ForegroundColorSpan(Color.RED), 0, fullNameHint.length(),0);
                et_FullName.setHint(fullNameHint);
            } if (houseName.isEmpty()){
                SpannableString houseNameHint = new SpannableString("Please enter your Poultry House Name!");
                houseNameHint.setSpan(new ForegroundColorSpan(Color.RED), 0, houseNameHint.length(),0);
                et_HouseName.setHint(houseNameHint);
            } if (email.isEmpty()) {
                SpannableString emailHint = new SpannableString("Please enter your Email!");
                emailHint.setSpan(new ForegroundColorSpan(Color.RED), 0, emailHint.length(),0);
                et_Email.setHint(emailHint);
            }  if (fullName.isEmpty() || houseName.isEmpty() || email.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                builder.setTitle("Error!");
                builder.setMessage("Please fill all the required information.!");
                builder.setCancelable(true);
                builder.setNegativeButton("Ok", (dialog, which) -> dialog.dismiss()).show();
            } else {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}