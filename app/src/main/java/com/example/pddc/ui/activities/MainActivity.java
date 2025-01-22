package com.example.pddc.ui.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.pddc.R;
import com.example.pddc.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CALL = 1;
    private AppBarConfiguration mAppBarConfiguration;

    private Animation rotateOpen, rotateClose, fromBottom, toBottom;
    private Boolean clicked = false;

    private FloatingActionButton fabChat, fabCall, fabSupAgent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.pddc.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        Bundle extras = getIntent().getExtras();
        String farmName = null;
        String fullName = null;
        String email = null;
        if (extras != null) {
            fullName = extras.getString("fullName");
            farmName = extras.getString("farmName");
            email = extras.getString("email");
        }

//        passDataToFragments(fullName, farmName, email);

        // Initialize FloatingActionButtons
        fabChat = binding.appBarMain.fabChat;
        fabCall = binding.appBarMain.fabCall;
        fabSupAgent = binding.appBarMain.fab;

        // Initialize Animations
        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);

        // Set FloatingActionButton Click Listeners
        fabSupAgent.setOnClickListener(view -> onFabSupportAgentBtnClicked());
        fabCall.setOnClickListener(view -> makePhoneCall());
        fabChat.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, ChatWithAgent.class)));

        // Setup Navigation
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_Temperature, R.id.nav_Humidity, R.id.nav_Ammonia, R.id.nav_Diseases, R.id.nav_Settings, R.id.nav_Assistance, R.id.nav_aboutApp, R.id.nav_policy, R.id.nav_terms)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                navController.navigate(R.id.nav_home);
            } else if (id == R.id.nav_Temperature) {
                navController.navigate(R.id.nav_Temperature);
            } else if (id == R.id.nav_Humidity) {
                navController.navigate(R.id.nav_Humidity);
            } else if (id == R.id.nav_Ammonia) {
                navController.navigate(R.id.nav_Ammonia);
            } else if (id == R.id.nav_Diseases) {
                navController.navigate(R.id.nav_Diseases);
            } else if (id == R.id.nav_Settings) {
                navController.navigate(R.id.nav_Settings);
            } else if (id == R.id.nav_Assistance) {
                navController.navigate(R.id.nav_Assistance);
            } else if (id == R.id.nav_aboutApp) {
                navController.navigate(R.id.nav_aboutApp);
            } else if (id == R.id.nav_policy) {
                navController.navigate(R.id.nav_policy);
            } else if (id == R.id.nav_terms) {
                navController.navigate(R.id.nav_terms);
            } else if (id == R.id.nav_rate) {
                rateApp();
            } else if (id == R.id.nav_Share) {
                shareApp();
                return false; // Prevent drawer closure for this case
            }

            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

    }

    private void onFabSupportAgentBtnClicked() {
        setVisibility(clicked);
        setAnimation(clicked);
        setClickable(clicked);
        clicked = !clicked;
    }

    private void setVisibility(Boolean clicked) {
        if (!clicked) {
            fabChat.setVisibility(View.VISIBLE);
            fabCall.setVisibility(View.VISIBLE);
        } else {
            fabChat.setVisibility(View.INVISIBLE);
            fabCall.setVisibility(View.INVISIBLE);
        }
    }

//    private void passDataToFragments(String fullName, String farmName, String email){
//        HomeFragment homeFragment = new HomeFragment();
//        SettingsFragment settingsFragment = new SettingsFragment();
//
//        Bundle args = new Bundle();
//        args.putString("fullName", fullName);
//        args.putString("farmName", farmName);
//        args.putString("email", email);
//        homeFragment.setArguments(args);
//        settingsFragment.setArguments(args);
//
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.nav_home, homeFragment)
//                .commit();
//
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.nav_Settings, settingsFragment)
//                .commit();
//    }


    private void setAnimation(Boolean clicked) {
        if (!clicked) {
            fabChat.startAnimation(fromBottom);
            fabCall.startAnimation(fromBottom);
            fabSupAgent.startAnimation(rotateOpen);
        } else {
            fabChat.startAnimation(toBottom);
            fabCall.startAnimation(toBottom);
            fabSupAgent.startAnimation(rotateClose);
        }
    }

    private void setClickable(Boolean clicked) {
        fabChat.setClickable(!clicked);
        fabCall.setClickable(!clicked);
    }

    private void makePhoneCall() {
        String number = "255743900555";
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
            String dial = "tel:" + number;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            }else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void rateApp() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Are you interested in managing a smart Poultry House? Click the link below to download Poultry Disease Detection and Control (P D D C) App: https://sleiz.com/sleizwaredevelopment/PDDC");
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
