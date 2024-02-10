package com.example.attendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class SplashScreenActivity extends AppCompatActivity {
    LinearLayout layout;
    String USERS="USERS";
    String TEACHERS ="TEACHERS";
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        layout=findViewById(R.id.linearLayout);
        progressBar=findViewById(R.id.progressBarSplash);
        scaleAnimation(layout);
        if(!NetworkUtils.isNetworkAvailable(getApplicationContext()))
            Toast.makeText(SplashScreenActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        checkData();
    }

    private void scaleAnimation(View view) {
        // Define the scale animation
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                1.3f, // Start scale X
                1f, // End scale X
                1.3f, // Start scale Y
                1f, // End scale Y
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot X
                Animation.RELATIVE_TO_SELF, 0.5f // Pivot Y
        );

        // Set the duration of the animation in milliseconds
        scaleAnimation.setDuration(1500);

        // Set the fill mode
        scaleAnimation.setFillAfter(true);

        // Set the animation listener if needed
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation start
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Animation end
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeat
            }
        });

        // Start the animation
        view.startAnimation(scaleAnimation);
    }

    void checkData(){
        progressBar.setVisibility(View.VISIBLE);
       if(FirebaseAuth.getInstance().getCurrentUser()!=null){
           FirebaseDatabase.getInstance().getReference().child(USERS).child(TEACHERS).child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   if(snapshot.exists()){
                       TeacherData data=snapshot.getValue(TeacherData.class);
                       if(data.CollageId!=null) {
                           TeacherDataStatic.CollageId= data.getCollageId();
                           TeacherDataStatic.Name= data.getName();
                           TeacherDataStatic.Mobile=data.getMobile();
                           TeacherDataStatic.Admin= data.getAdmin();
                           if(data.Admin.equals("FALSE")) verification();
                           else startActivity(new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));

                       }else{
                           startActivity(new Intent(getApplicationContext(), CreateOrJoinGroupActivity.class));
                           finish();
                       }

                   }
                   else{
                       startActivity(new Intent(getApplicationContext(), CreateOrJoinGroupActivity.class));
                       finish();
                   }
                   progressBar.setVisibility(View.GONE);

               }
               @Override
               public void onCancelled(@NonNull DatabaseError error) {

               }
           });
       }
       else {
           progressBar.setVisibility(View.GONE);
           startActivity(new Intent(getApplicationContext(), LoginActivity.class));
           finish();
       }
    }

    private void verification() {
        String VERIFICATION="VERIFICATION";
        String COLLAGES="COLLAGES";
        String Teachers="TEACHERS";
        DatabaseReference refference= FirebaseDatabase.getInstance().getReference().child(USERS).child(Teachers);
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference().child(VERIFICATION).child(COLLAGES).child(TeacherDataStatic.CollageId).child(removeSpecialCharacters(user.getEmail())).addListenerForSingleValueEvent(new ValueEventListener() {
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public String removeSpecialCharacters(String str) {
        // Using regex to replace all characters except letters, digits, @, and .
        return str.replaceAll("[^a-zA-Z0-9]", "");
    }

}