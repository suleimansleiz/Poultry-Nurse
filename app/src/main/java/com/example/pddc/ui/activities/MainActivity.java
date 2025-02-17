package com.example.pddc.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
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
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CALL = 1;
    private AppBarConfiguration mAppBarConfiguration;

    private Animation fromBottom;
    private Animation toBottom;
    private Boolean clicked = false;

    private MaterialCardView fabChat;
    private MaterialCardView fabCall;
    private FirebaseFirestore db;
    TextView tvPHouseName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());


        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        ImageButton btnMenu = binding.appBarMain.btnMenu;
        tvPHouseName = binding.appBarMain.tvPHouseName;
        setContentView(binding.getRoot());


        // Open Drawer when ImageButton is clicked
        btnMenu.setOnClickListener(v -> {
            if (!drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        //Passing User Credentials
        db = FirebaseFirestore.getInstance();

        SharedPreferences userCredPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        String farmerId = userCredPreferences.getString("farmerId", "");

        if (!farmerId.isEmpty()) {
            fetchUserDetails(farmerId);
        }
        tvPHouseName.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FarmDetails.class);
            startActivity(intent); //Navigating to Farm Details Page
        });

//        //showing the banner
//        showBanner();

        // Initialize FloatingActionButtons
        fabChat = binding.appBarMain.fabChat;
        fabCall = binding.appBarMain.fabCall;
        MaterialCardView fabSupAgent = binding.appBarMain.fab;

        // Initialize Animations
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
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
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
            } // Prevent drawer closure for this case


            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

    }

private void fetchUserDetails(String farmerId) {
    db.collection("Users").document(farmerId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String houseName = documentSnapshot.getString("houseName");

                    tvPHouseName.setText(houseName);
                } else {
                    Toast.makeText(MainActivity.this, "Error fetching Details", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(e -> Log.e("Firestore", "Error fetching data", e));
    }

//    public int getRemainingDays() {
//        SharedPreferences prefs = getSharedPreferences("AppTrialPrefs", MODE_PRIVATE);
//        long createdTime = prefs.getLong("account_created", 0);
//
//        if (createdTime == 0) return 30;
//
//        long currentTime = System.currentTimeMillis();
//        long elapsedTime = currentTime -createdTime;
//        long daysPassed = TimeUnit.MICROSECONDS.toDays(elapsedTime);
//
//        return (int) (30 - daysPassed);
//    }
//
//    private void showBanner(){
//        int daysLeft = getRemainingDays();
//        SharedPreferences prefs = getSharedPreferences("AppTrialPrefs", MODE_PRIVATE);
//        long lastDismissTime = prefs.getLong("banner_dismissed_Time", 0);
//        long currentTime = System.currentTimeMillis();
//
//        if ((daysLeft == 30 || daysLeft == 10 || daysLeft == 5 || daysLeft == 3 || daysLeft <= 0) && !isSameDay(lastDismissTime, currentTime)) {
//            callBanner(daysLeft);
//        }
//    }
//
//    private boolean isSameDay(long timestamp1, long timestamp2) {
//        Calendar cal1 = Calendar.getInstance();
//        cal1.setTimeInMillis(timestamp1);
//
//        Calendar cal2 = Calendar.getInstance();
//        cal2.setTimeInMillis(timestamp2);
//
//        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
//    }
//
//    @SuppressLint("DefaultLocale")
//    private void callBanner(int daysLeft){
//        MaterialCardView mcvBanner = findViewById(R.id.mcvBanner);
//        TextView tvContent = findViewById(R.id.tvContent);
//        ImageButton ibCloseBanner = findViewById(R.id.ibCloseBanner);
//
//        if (daysLeft > 0) {
//            tvContent.setText(String.format("Your Trial expires in %d days, pay early to continue enjoying Poultry Nurse.", daysLeft));
//        } else {
//            tvContent.setText(R.string.trial_expired);
//        }
//
//        Animation slideInAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_in);
//        mcvBanner.startAnimation(slideInAnim);
//        mcvBanner.setVisibility(View.VISIBLE);
//
//        ibCloseBanner.setOnClickListener(v -> {
//            Animation slideOutAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_out);
//            mcvBanner.startAnimation(slideOutAnim);
//            mcvBanner.setVisibility(View.GONE);
//        });
//    }

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


    private void setAnimation(Boolean clicked) {
        if (!clicked) {
            fabChat.startAnimation(fromBottom);
            fabCall.startAnimation(fromBottom);
        } else {
            fabChat.startAnimation(toBottom);
            fabCall.startAnimation(toBottom);
        }
    }

    private void setClickable(Boolean clicked) {
        fabChat.setClickable(!clicked);
        fabCall.setClickable(!clicked);
    }

    private void makePhoneCall() {
        String number = getString(R.string.agent_phone_no);
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

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
