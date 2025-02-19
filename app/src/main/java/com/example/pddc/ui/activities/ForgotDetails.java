package com.example.pddc.ui.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pddc.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ForgotDetails extends AppCompatActivity {

    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_details);

        db = FirebaseFirestore.getInstance();

        //Initializing Views
        EditText etPhoneNo = findViewById(R.id.etPhoneNo);
        MaterialButton btnConfirm = findViewById(R.id.btnConfirm);

        btnConfirm.setOnClickListener(View -> {
            String phoneNo = etPhoneNo.getText().toString().trim();

            if (!phoneNo.isEmpty()) {
                confirmAccountExists(phoneNo);
            }else {
                Toast.makeText(this, "Please fill in a valid phone number", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmAccountExists(String phoneNo){
        CollectionReference usersRef = db.collection("Users");
        usersRef
                .whereEqualTo("phoneNo", phoneNo)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()){
                        db.collection("Users")
                                .document("P25-Farm-" + phoneNo)
                                .get()
                                .addOnSuccessListener(documentSnapShot -> {
                                    String farm_Name = documentSnapShot.getString("houseName");

                                    showSuccessDialog(farm_Name);
                                });
                    } else {
                        showFailureDialog();
                    }
                });
    }

    private void showSuccessDialog(String farm_Name) {
        LinearLayout llRandomDialog = findViewById(R.id.llRandomDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.random_dialog, llRandomDialog);

        TextView tvParticularText = view.findViewById(R.id.tvParticularText);
        TextView tvOkContent = view.findViewById(R.id.tvOkContent);

        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotDetails.this);
        builder.setView(view)
                .setCancelable(true);

        tvParticularText.setText(String.format("Your Farm Name is '%s'. You may use it to Login to your Poultry Nurse app.", farm_Name));
        AlertDialog dialog = builder.create();

        tvOkContent.setOnClickListener(v -> {
            dialog.dismiss();
            recreate();
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();
    }

    private void showFailureDialog() {
        LinearLayout llRandomDialog = findViewById(R.id.llRandomDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.random_dialog, llRandomDialog);

        TextView tvParticularText = view.findViewById(R.id.tvParticularText);
        TextView tvOkContent = view.findViewById(R.id.tvOkContent);

        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotDetails.this);
        builder.setView(view)
                .setCancelable(true);

        tvParticularText.setText(R.string.phone_no_does_not_exist);
        AlertDialog dialog = builder.create();

        tvOkContent.setOnClickListener(v -> dialog.dismiss());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();
    }
}