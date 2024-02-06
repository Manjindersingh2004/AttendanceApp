package com.example.attendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CreateOrJoinGroupActivity extends AppCompatActivity {

    Button create,join;
    String USERS="USERS";
    String TEACHERS ="TEACHERS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_or_join_group);
        create=findViewById(R.id.Create_collage_btn___);
        join=findViewById(R.id.Join_collage_btn___);

        create.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),CollageCreateActivity.class));
        });
        join.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),JoinCollageActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().getReference().child(USERS).child(TEACHERS).child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    startActivity(new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK ));
                }
                else{
                    Toast.makeText(CreateOrJoinGroupActivity.this, "Snapshot not Exists", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}