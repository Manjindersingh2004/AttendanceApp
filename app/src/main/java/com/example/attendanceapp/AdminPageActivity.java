package com.example.attendanceapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;

public class AdminPageActivity extends AppCompatActivity {

    AppCompatButton backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);
        backBtn=findViewById(R.id.back_button_admin);

        backBtn.setOnClickListener(v -> {finish();});
    }
}