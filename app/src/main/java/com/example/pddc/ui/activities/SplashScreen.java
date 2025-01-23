package com.example.pddc.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pddc.R;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR );
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

    handler.postDelayed(() -> {
        Intent intent = new Intent(SplashScreen.this, WelcomePage.class);
        startActivity(intent);
        finish();
    },3000);
    }
}