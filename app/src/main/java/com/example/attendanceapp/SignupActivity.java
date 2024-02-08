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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {
    Button signup;
    AppCompatButton back;
    EditText emailEdit,passEdit,passConEdit,nameEdit,phoneEdit;
    ProgressBar progressBar;
    String USERS="USERS";
    String STUDENTS="STUDENTS";
    String TEACHERS="TEACHERS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sigin);
        back=findViewById(R.id.back_button_signup);
        signup=findViewById(R.id.Signup_btn);
        emailEdit=findViewById(R.id.emailEdt);
        passEdit=findViewById(R.id.passEdt);
        passConEdit=findViewById(R.id.passConEdt);
        progressBar=findViewById(R.id.progressBarSigup);
        nameEdit=findViewById(R.id.nameEdt);
        phoneEdit=findViewById(R.id.mobileEdt);

        back.setOnClickListener(v -> {
            finish();
        });

        signup.setOnClickListener(v -> {
            String email,password,passConferm,name,phone;
            email=emailEdit.getText().toString().trim();
            password=passEdit.getText().toString().trim();
            passConferm=passConEdit.getText().toString().trim();
            name=nameEdit.getText().toString().trim();
            phone=phoneEdit.getText().toString().trim();

            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";


            if(email.isEmpty()){
                emailEdit.setError("Email is empty");
                emailEdit.requestFocus();
            }
            else if(!Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$").matcher(email).matches()){
                emailEdit.setError("Invalid Email");
                emailEdit.requestFocus();
                emailEdit.setText(null);
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
            else if(name.isEmpty()){
                nameEdit.setError("Name is empty");
                nameEdit.requestFocus();
            }
            else if(phone.isEmpty()){
                phoneEdit.setError("Mobile is empty");
                phoneEdit.requestFocus();
            }
            else if(phone.length()!=10){
                phoneEdit.setError("Invalid Number");
                phoneEdit.requestFocus();
            }
            else{
                if(NetworkUtils.isNetworkAvailable(getApplicationContext())){
                    progressBar.setVisibility(View.VISIBLE);
                    FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(SignupActivity.this,new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SignupActivity.this, "Sucessfull", Toast.LENGTH_SHORT).show();
                                FirebaseUser user=firebaseAuth.getCurrentUser();
                                TeacherData data=new TeacherData(null,null,name.toUpperCase(),phone);
                                DatabaseReference refference= FirebaseDatabase.getInstance().getReference().child(USERS).child(TEACHERS);
                                refference.child(user.getUid().toString()).setValue(data).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            user.sendEmailVerification();
                                            startActivity(new Intent(getApplicationContext(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK ));
                                        }
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });

                            }
                            else{
                                try {
                                    throw task.getException();
                                }
                                catch(FirebaseAuthInvalidCredentialsException e){
                                    Toast.makeText(SignupActivity.this, "Invalid Email or Already in used ", Toast.LENGTH_LONG).show();
                                }
                                catch(FirebaseAuthUserCollisionException e){
                                    Toast.makeText(SignupActivity.this, "User is Already Registred with this email", Toast.LENGTH_LONG).show();
                                    emailEdit.setText("Already in use");
                                    emailEdit.requestFocus();
                                }
                                catch(Exception e){
                                    Toast.makeText(SignupActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
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