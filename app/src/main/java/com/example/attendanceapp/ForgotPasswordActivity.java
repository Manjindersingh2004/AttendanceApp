package com.example.attendanceapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.regex.Pattern;

public class ForgotPasswordActivity extends AppCompatActivity {
    Button reset;
    AppCompatButton back;
    EditText emailEdit;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        back=findViewById(R.id.back_button_Forgot);
        emailEdit=findViewById(R.id.emailEdtForgot);
        progressBar=findViewById(R.id.progressBarForgot);
        reset=findViewById(R.id.Reset_Button_Forgot);

        back.setOnClickListener(v -> {
            finish();
        });

        reset.setOnClickListener(v -> {
            String email;
            email = emailEdit.getText().toString().trim();
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";


            if (email.isEmpty()) {
                emailEdit.setError("Email is empty");
                emailEdit.requestFocus();
            } else if (!Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$").matcher(email).matches()) {
                emailEdit.setError("Invalid Email");
                emailEdit.requestFocus();
                emailEdit.setText(null);
            } else {
               if(NetworkUtils.isNetworkAvailable(getApplicationContext())){
                   progressBar.setVisibility(View.VISIBLE);
                   FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(ForgotPasswordActivity.this, new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if(task.isSuccessful()){
                               showAlertDialog();
                               Toast.makeText(ForgotPasswordActivity.this, "Check Your Email", Toast.LENGTH_SHORT).show();
                           }else{
                               try{
                                   throw task.getException();
                               }
                               catch(FirebaseAuthInvalidUserException e){
                                   emailEdit.setError("User Do not Exists");
                                   emailEdit.requestFocus();
                               }
                               catch (Exception e){
                                   Toast.makeText(ForgotPasswordActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
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
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Email is Sent")
                .setMessage("Open Email App to Reset PAssword")
                .setPositiveButton("open", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_EMAIL).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        dialog.dismiss();
                        finish();
                    }
                })
                .show();
    }
}