package com.example.pddc.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.pddc.R;
import com.example.pddc.ui.adapters.ImageSliderAdapter;
import com.google.android.material.button.MaterialButton;

import java.util.Arrays;
import java.util.List;

public class WelcomePage extends AppCompatActivity {

    private ViewPager2 viewPager;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private int currentPage = 0;
    private final List<Integer> images = Arrays.asList(R.drawable.nurse_one, R.drawable.farmer_one, R.drawable.farmer_two, R.drawable.farmer_three);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        /* StatusBar color */
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new ImageSliderAdapter(images));

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (currentPage == images.size()) currentPage = 0;
                viewPager.setCurrentItem(currentPage++, true);
                handler.postDelayed(this, 5000);
            }
        };
        handler.postDelayed(runnable, 3000);



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
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}