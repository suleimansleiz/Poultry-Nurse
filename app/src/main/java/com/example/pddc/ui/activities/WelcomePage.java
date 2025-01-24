package com.example.pddc.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pddc.R;
import com.google.android.material.button.MaterialButton;

public class WelcomePage extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR );


        MaterialButton mbGoToLogin = findViewById(R.id.mbGoToLogin);
        MaterialButton mbGoToSignUp = findViewById(R.id.mbGoToSignUp);

        mbGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }

            private void goToLogin() {
                Intent intent = new Intent(WelcomePage.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        mbGoToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignup();
            }

            private void goToSignup() {
                Intent intent = new Intent(WelcomePage.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}