package com.example.pddc.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pddc.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    FirebaseFirestore db;
    String farmerId, userId = "P25-Farm";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        db = FirebaseFirestore.getInstance(); // Initialize Firestore

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


        EditText et_HouseName = findViewById(R.id.etHouseName);
        EditText et_PhoneNo = findViewById(R.id.etPhoneNo);
        MaterialButton btn_Login = findViewById(R.id.btnLogin);

        et_HouseName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        et_PhoneNo.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);


        /*
           Performing login
         **/

        btn_Login.setOnClickListener(v -> {
            String houseName = et_HouseName.getText().toString().trim();
            String phoneNo = et_PhoneNo.getText().toString().trim();

            if (houseName.isEmpty() || phoneNo.isEmpty()) {
                showAlertDialog("Error!", "Please fill all the required fields.");
            } else if(isOnline()) {
                btn_Login.setEnabled(false);
                btn_Login.setText(R.string.signing_in);
                authenticateUser(houseName, phoneNo);
                farmerId = userId + "-" + phoneNo;
                SharedPreferences userCredPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = userCredPreferences.edit();
                editor.putBoolean("isLoggedIn", true);
                editor.putString("farmerId", farmerId);
                editor.apply();

            }else {
                btn_Login.setEnabled(true);
                btn_Login.setText(R.string.sign_in);
                showAlertDialog("Error!", "Please check your network and try again.");
            }
        });
    }

    // Check Firestore for user details
    private void authenticateUser(String houseName, String phoneNo) {
       CollectionReference collRefs = db.collection("Users");
        collRefs
                .whereEqualTo("houseName", houseName)
                .whereEqualTo("phoneNo", phoneNo)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            // User not found
                            showAlertDialog("Login Failed!", "User details not found. Please sign up first.");
                        }
                });

    }

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null){
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnectedOrConnecting();
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(LoginActivity.this, WelcomePage.class);
        startActivity(intent);
        finish();
    }
}