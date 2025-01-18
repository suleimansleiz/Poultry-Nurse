package com.example.pddc.ui.activities;

import static com.example.pddc.R.id.tvArrowBack_Note_in;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.pddc.R;

public class InnerNotification extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_notification);

        String topName = getIntent().getStringExtra("PAGE_HEADER");
        String name = getIntent().getStringExtra("NAME");
        String description = getIntent().getStringExtra("DESCRIPTION");

        TextView tv_ToolBar = findViewById(R.id.tvToolBar);
        TextView tv_NoteHeader = findViewById(R.id.tvNoteHeader);
        TextView tv_NoteBody = findViewById(R.id.tvNoteBody);

        tv_ToolBar.setText(topName);
        tv_NoteHeader.setText(name);
        tv_NoteBody.setText(description);


        TextView tv_ArrowBack_Note_in = findViewById(tvArrowBack_Note_in);
        tv_ArrowBack_Note_in.setOnClickListener(v -> {
            Intent intent = new Intent(InnerNotification.this, Notifications.class);
            startActivity(intent);
            finishAffinity();
        });
    }
}