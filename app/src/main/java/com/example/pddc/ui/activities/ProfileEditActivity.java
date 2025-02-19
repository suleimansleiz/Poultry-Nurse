package com.example.pddc.ui.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pddc.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileEditActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_edit);

        db = FirebaseFirestore.getInstance();

        SharedPreferences userCredPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        String farmerId = userCredPreferences.getString("farmerId", "");

        //initializing views
        EditText etNewHouseName = findViewById(R.id.etNewHouseName);
        MaterialButton btnEditProfile = findViewById(R.id.btnEditProfile);

        btnEditProfile.setOnClickListener(View -> {
            String newFarmName = etNewHouseName.getText().toString().trim();
            if (!farmerId.isEmpty() && !newFarmName.isEmpty()) {
                updateHouseName(farmerId, newFarmName);
            }else {
                Toast.makeText(this, "Fill in new Farm Name", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateHouseName(String farmerId, String newFarmName) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("houseName", newFarmName);

        db.collection("Users")
                .document(farmerId)
                .update(updateData)
                .addOnSuccessListener(e -> Toast.makeText(this, "House Name Updated Successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update House Name", Toast.LENGTH_SHORT).show());
    }
}