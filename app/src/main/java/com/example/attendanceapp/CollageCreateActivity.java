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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CollageCreateActivity extends AppCompatActivity {
    Button createBtn;
    AppCompatButton back;
    EditText collageEdt,passEdit,passConEdit;
    ProgressBar progressBar;
    String USERS="USERS";
    String TEACHERS ="TEACHERS";
    String COLLAGES="COLLAGES";
    String PASSWORD="PASSWORD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collage_create);
        back=findViewById(R.id.back_button_CreateCollage);
        createBtn =findViewById(R.id.Create_collage_btn);
        collageEdt =findViewById(R.id.collageEdtCreateCollage);
        passEdit=findViewById(R.id.passEdtCreateCollage);
        passConEdit=findViewById(R.id.passConEdtCreateCollage);
        progressBar=findViewById(R.id.progressBarCreateCollage);

        back.setOnClickListener(v -> {
            finish();
        });

        createBtn.setOnClickListener(v -> {
            String collageid,password,passConferm;
            collageid= collageEdt.getText().toString().trim();
            password=passEdit.getText().toString().trim();
            passConferm=passConEdit.getText().toString().trim();

            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";


            if(collageid.isEmpty()){
                collageEdt.setError("Collageid is empty");
                collageEdt.requestFocus();
            }
            else if(collageid.length()<8 || collageid.length()>16){
                collageEdt.setError("8 to 16 digits only");
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
            else if(passConferm.isEmpty()){
                passConEdit.setError("Password is empty");
                passConEdit.requestFocus();
            }
            else if(!password.equals(passConferm)){
                passEdit.setError("Password not match");
                passEdit.requestFocus();
                passEdit.setText(null);
                passConEdit.setText(null);
            }
            else{
                if(NetworkUtils.isNetworkAvailable(getApplicationContext())){


                    new FireBaseHelper(getApplicationContext()).isCollageIdExists(collageid.toUpperCase().trim(), progressBar, new FireBaseHelper.OnCheckExists() {
                        @Override
                        public void onCheckExists(boolean isExists) {
                            if(isExists){
                                collageEdt.setError("Already Exists");
                                collageEdt.requestFocus();
                            }
                            else{
                                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                                TeacherData data=new TeacherData("TRUE",collageid);
                                DatabaseReference refference= FirebaseDatabase.getInstance().getReference().child(USERS).child(TEACHERS);
                                refference.child(user.getUid().toString()).setValue(data).addOnCompleteListener(CollageCreateActivity.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            FirebaseDatabase.getInstance().getReference().child(COLLAGES).child(collageid.toUpperCase().trim()).child(PASSWORD).setValue(password);
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK ));
                                        }
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
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
}