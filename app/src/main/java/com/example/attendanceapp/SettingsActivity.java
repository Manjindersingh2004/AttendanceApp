package com.example.attendanceapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {
    Button editBtn,signoutBtn,deleteBtn;
    AppCompatButton backBtn;
    EditText emailEdt,nameEdit,rollEdt,collageEdt;
    ProgressBar progressBar;
    String USERS="USERS";
    String STUDENTS="STUDENTS";
    TeacherData data;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        editBtn=findViewById(R.id.EditBtn);
        signoutBtn=findViewById(R.id.SignoutBtn);
        deleteBtn=findViewById(R.id.DeleteAccBtn);
        backBtn=findViewById(R.id.back_button_setings);
        emailEdt=findViewById(R.id.emailEdtSettings);
        nameEdit=findViewById(R.id.nameEditSettings);
        rollEdt=findViewById(R.id.rollEditSettings);
        collageEdt=findViewById(R.id.collageIdEditSettings);
        progressBar=findViewById(R.id.progressBarProfile);
        name=getIntent().getStringExtra("name");

        backBtn.setOnClickListener(v -> {
            finish();
        });

        signoutBtn.setOnClickListener(v -> {
            if(NetworkUtils.isNetworkAvailable(getApplicationContext())){
                new DataBaseHelper(getApplicationContext()).deleteDatabase();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }else{
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
        deleteBtn.setOnClickListener(v -> {
            if(NetworkUtils.isNetworkAvailable(getApplicationContext())){
                FirebaseDatabase.getInstance().getReference().child(USERS).child(STUDENTS).child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).removeValue();
                FirebaseAuth.getInstance().getCurrentUser().delete();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));

            }
            else{
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });

        editBtn.setOnClickListener(v -> {
           if(NetworkUtils.isNetworkAvailable(getApplicationContext())){
               if(editBtn.getText().equals("Edit")){
                   rollEdt.setEnabled(true);
                   collageEdt.setEnabled(true);
                   editBtn.setText("Save");
                   rollEdt.requestFocus();
               }
               else{
                   String rollno=rollEdt.getText().toString().trim();
                   String collageId=collageEdt.getText().toString().trim();

                   if(rollno.isEmpty()){
                       rollEdt.setError("Enter Roll number");
                       rollEdt.requestFocus();
                   }
                   else if(collageId.isEmpty()){
                       collageEdt.setError("Enter CollageId ");
                       collageEdt.requestFocus();
                   }
                   else{
//                       new MyFireBaseHelper(getApplicationContext()).isCollageRollExists(collageId, rollno, progressBar, new MyFireBaseHelper.OnCheckExists() {
//                           @Override
//                           public void onCheckExists(boolean isExists) {
//                               if(isExists){
//                                   progressBar.setVisibility(View.VISIBLE);
//                                   FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
//                                   StudentData data=new StudentData(rollno,collageId);
//                                   DatabaseReference refference= FirebaseDatabase.getInstance().getReference().child(USERS).child(STUDENTS);
//                                   refference.child(user.getUid().toString()).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                       @Override
//                                       public void onComplete(@NonNull Task<Void> task) {
//                                           if(task.isSuccessful()){
//                                               Toast.makeText(SettingsActivity.this, "Data is Updated", Toast.LENGTH_SHORT).show();
//                                               editBtn.setText("Edit");
//                                               rollEdt.setEnabled(false);
//                                               collageEdt.setEnabled(false);
//                                               fetchData();
//                                           }else{
//                                               Toast.makeText(SettingsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
//                                           }
//                                           progressBar.setVisibility(View.GONE);
//                                       }
//                                   });
//                               }
//                               else{
//                                   rollEdt.setError("Invalid Data");
//                                   collageEdt.setError("Invalid Data ");
//                                   rollEdt.setText(null);
//                                   collageEdt.setText(null);
//                                   rollEdt.requestFocus();
//                               }
//                           }
//                       });
                   }
               }

           }
           else{
               Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
           }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchData();

    }

    private void fetchData() {
//        progressBar.setVisibility(View.VISIBLE);
//        FirebaseDatabase.getInstance().getReference().child(USERS).child(STUDENTS).child(FirebaseAuth.getInstance().getUid().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()){
//                    data=snapshot.getValue(StudentData.class);
//                    if(data!=null){
//                        setUi();
//                    }else{
//                        progressBar.setVisibility(View.GONE);
//                    }
//                }else{
//                    FirebaseAuth.getInstance().getCurrentUser().delete();
//                    FirebaseAuth.getInstance().signOut();
//                    startActivity(new Intent(getApplicationContext(),LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
//                    Toast.makeText(SettingsActivity.this, "Account Deleted due to incorrect information", Toast.LENGTH_LONG).show();
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }

    private void setUi() {
//        emailEdt.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());
//        collageEdt.setText(data.CollageId);
//        rollEdt.setText(data.Rollno);
//        nameEdit.setText(name);
//        progressBar.setVisibility(View.GONE);
    }
}