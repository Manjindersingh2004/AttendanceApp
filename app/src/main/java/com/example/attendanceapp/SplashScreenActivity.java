package com.example.attendanceapp;

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
import android.widget.TextView;

public class SplashScreenActivity extends AppCompatActivity {
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        layout=findViewById(R.id.linearLayout);
        scaleAnimation(layout);
        new DataBaseHelper(getApplicationContext()).downloadBackup(getApplicationContext());



        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, 2000);
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
        scaleAnimation.setDuration(1000);

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
}