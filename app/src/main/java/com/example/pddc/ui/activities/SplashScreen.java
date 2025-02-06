package com.example.pddc.ui.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.pddc.R;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_splash_screen);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR );
        EdgeToEdge.enable(this);



        /* App Themes Globally */
        SharedPreferences preferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        int themeMode = preferences.getInt("ThemeMode", AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(themeMode);


        //Loading Login status
        handler.postDelayed(() -> {
            SharedPreferences userCredPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
            boolean isLoggedIn = userCredPreferences.getBoolean("isLoggedIn", false);

            Intent intent;
            if (isLoggedIn) {
                intent = new Intent(this, MainActivity.class);
            } else {
                intent = new Intent(this, WelcomePage.class);
            }
            startActivity(intent);
            finish();
        },3000);
    }
}