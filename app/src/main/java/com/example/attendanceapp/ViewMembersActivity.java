package com.example.attendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewMembersActivity extends AppCompatActivity {

    AppCompatButton backBtn;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    ArrayList<VerificationDataModel> arrayList=new ArrayList<>();
    String VERIFICATION="VERIFICATION";
    String COLLAGES="COLLAGES";
    LinearLayout nothing;
    AdapterMembers adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_members);
        backBtn=findViewById(R.id.back_button_admin);
        progressBar=findViewById(R.id.progressBarMember);
        recyclerView=findViewById(R.id.recyclerViewMembers);
        nothing=findViewById(R.id.nothing_is_here_linear_layout_member);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter=new AdapterMembers(this,progressBar,nothing);
        recyclerView.setAdapter(adapter);
        backBtn.setOnClickListener(v -> {finish();});
    }

    void putItemIntoArraylist(){
        FirebaseDatabase.getInstance().getReference().child(VERIFICATION).child(COLLAGES).child(TeacherDataStatic.CollageId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot childSnapshot : snapshot.getChildren()) {
                        VerificationDataModel dataModel = childSnapshot.getValue(VerificationDataModel.class);
                        arrayList.add(dataModel);
                    }
                    Toast.makeText(getApplicationContext(), "Found", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "not Found", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}