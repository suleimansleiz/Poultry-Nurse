package com.example.pddc.ui.activities;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.pddc.R;
import com.example.pddc.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.pddc.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, Notifications.class)));


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_Temperature, R.id.nav_Humidity, R.id.nav_Ammonia, R.id.nav_Diseases, R.id.nav_Settings, R.id.nav_Assistance, R.id.nav_aboutApp, R.id.nav_policy, R.id.nav_terms)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    navController.navigate(R.id.nav_home);
                }
                if (id == R.id.nav_Temperature) {
                    navController.navigate(R.id.nav_Temperature);
                }
                if (id == R.id.nav_Humidity) {
                    navController.navigate(R.id.nav_Humidity);
                }
                if (id == R.id.nav_Ammonia) {
                    navController.navigate(R.id.nav_Ammonia);
                }
                if (id == R.id.nav_Diseases) {
                    navController.navigate(R.id.nav_Diseases);
                }
                if (id == R.id.nav_Settings) {
                    navController.navigate(R.id.nav_Settings);
                }
                if (id == R.id.nav_Assistance) {
                    navController.navigate(R.id.nav_Assistance);
                }
                if (id == R.id.nav_aboutApp) {
                    navController.navigate(R.id.nav_aboutApp);
                }
                if (id == R.id.nav_policy) {
                    navController.navigate(R.id.nav_policy);
                }
                if (id == R.id.nav_terms) {
                    navController.navigate(R.id.nav_terms);
                }
                if (id == R.id.nav_rate) {
                    rateApp();
                }
                if (id == R.id.nav_Share) {
                    shareApp();
                    return false;
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }

            private void rateApp() {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + getPackageName())));
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                }
            }

            private void shareApp() {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this awesome Poultry House System: https://sleiz.com/sleizwaredevelopment/PDDC");
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}