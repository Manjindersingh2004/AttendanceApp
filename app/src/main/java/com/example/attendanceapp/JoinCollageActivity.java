package com.example.attendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class JoinCollageActivity extends AppCompatActivity {
    Button joinBtn;
    AppCompatButton back;
    EditText collageEdt,passEdit;
    ProgressBar progressBar;
    String USERS="USERS";
    String Teachers="TEACHERS";
    String COLLAGES="COLLAGES";
    String PASSWORD="PASSWORD";
    String VERIFICATION="VERIFICATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_collage);
        back=findViewById(R.id.back_button_JoinCollage);
        joinBtn =findViewById(R.id.Join_collage_btn);
        collageEdt =findViewById(R.id.collageEdtJoinCollage);
        passEdit=findViewById(R.id.passEdtJoinCollage);
        progressBar=findViewById(R.id.progressBarJoinCollage);

        DatabaseReference refference= FirebaseDatabase.getInstance().getReference().child(USERS).child(Teachers);
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

        back.setOnClickListener(v -> {
            finish();
        });

        joinBtn.setOnClickListener(v -> {
            String collageid,password;
            collageid= collageEdt.getText().toString().trim();
            password=passEdit.getText().toString().trim();

            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";


            if(collageid.isEmpty()){
                collageEdt.setError("Collageid is empty");
                collageEdt.requestFocus();
            }
            else if(password.isEmpty()){
                passEdit.setError("Password is empty");
                passEdit.requestFocus();
            }
            else if(password.length()<8 || password.length()>16){
                passEdit.setError("Required 8 to 16 digits");
                passEdit.requestFocus();
            }
            else{
                if(NetworkUtils.isNetworkAvailable(getApplicationContext())){


                    new FireBaseHelper(getApplicationContext()).isCollageIdExists(collageid.toUpperCase().trim(), progressBar, new FireBaseHelper.OnCheckExists() {
                        @Override
                        public void onCheckExists(boolean isExists) {
                            if(isExists){
                                FirebaseDatabase.getInstance().getReference().child(COLLAGES).child(collageid.toUpperCase().trim()).child(PASSWORD).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        String OriginalPassword=snapshot.getValue().toString();
                                        if(password.equals(OriginalPassword)){
                                            FirebaseDatabase.getInstance().getReference().child(VERIFICATION).child(COLLAGES).child(collageid).child(removeSpecialCharacters(user.getEmail())).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if(snapshot.exists()){
                                                        VerificationDataModel verData=snapshot.getValue(VerificationDataModel.class);
                                                        if(verData.Verified.equals("TRUE")){
                                                            startActivity(new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK ));
                                                        }else{
                                                            startActivity(new Intent(getApplicationContext(), VerificationScreen.class));
                                                            finish();
                                                        }

                                                    }else{
                                                        refference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                if(snapshot.exists()){
                                                                    TeacherData data=snapshot.getValue(TeacherData.class);
                                                                    data.CollageId=collageid;
                                                                    data.Admin="FALSE";
                                                                    refference.child(user.getUid().toString()).setValue(data).addOnCompleteListener(JoinCollageActivity.this, new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful()){
                                                                                VerificationDataModel verData=new VerificationDataModel(data.Name,data.Mobile,user.getEmail(),"FALSE");
                                                                                FirebaseDatabase.getInstance().getReference().child(VERIFICATION).child(COLLAGES).child(collageid).child(removeSpecialCharacters(user.getEmail())).setValue(verData).addOnCompleteListener(JoinCollageActivity.this, new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if(task.isSuccessful()){
                                                                                            startActivity(new Intent(getApplicationContext(), VerificationScreen.class));
                                                                                            finish();
                                                                                        }else{
                                                                                            Toast.makeText(JoinCollageActivity.this, "error", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                        progressBar.setVisibility(View.GONE);
                                                                                    }
                                                                                });
                                                                            }

                                                                        }
                                                                    });
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {
                                                                progressBar.setVisibility(View.GONE);
                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }else{
                                            passEdit.setError("Invalid Password");
                                            passEdit.requestFocus();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(JoinCollageActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                            }
                            else{
                                collageEdt.setError("Invalid Collage Id");
                                collageEdt.requestFocus();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public String removeSpecialCharacters(String str) {
        // Using regex to replace all characters except letters, digits, @, and .
        return str.replaceAll("[^a-zA-Z0-9]", "");
    }
}