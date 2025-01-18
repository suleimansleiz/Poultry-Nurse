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

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
        });

        EditText et_HouseName = findViewById(R.id.etHouseName);
        EditText et_Email = findViewById(R.id.etEmail);

        MaterialButton btn_Login = findViewById(R.id.btnLogin);
        btn_Login.setOnClickListener(v -> {
            String houseName = et_HouseName.getText().toString().trim();
            String email = et_Email.getText().toString().trim();
            if (houseName.isEmpty()){
                SpannableString houseNameHint = new SpannableString("Please enter your Poultry House Name!");
                houseNameHint.setSpan(new ForegroundColorSpan(Color.RED), 0, houseNameHint.length(),0);
                et_HouseName.setHint(houseNameHint);
            } if (email.isEmpty()) {
                SpannableString emailHint = new SpannableString("Please enter your Email!");
                emailHint.setSpan(new ForegroundColorSpan(Color.RED), 0, emailHint.length(),0);
                et_Email.setHint(emailHint);
            } if (houseName.isEmpty() || email.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Error!");
                builder.setMessage("Please fill all the required information.!");
                builder.setCancelable(true);
                builder.setNegativeButton("Ok", (dialog, which) -> dialog.dismiss()).show();
            } else {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
            }
        });
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