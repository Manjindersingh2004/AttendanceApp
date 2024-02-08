package com.example.attendanceapp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class FireBaseHelper {
    Context context;
    String COLLAGES="COLLAGES";
    String COLLAGE_ID ;


    FireBaseHelper(Context context){
        this.context=context;
    }

    void isCollageIdExists(String collageId,ProgressBar progressBar, OnCheckExists onCheckExists) {
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference collagesRef = FirebaseDatabase.getInstance().getReference().child(COLLAGES).child(collageId);

        collagesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Data for the specified collageId and rollNo is available

                    //progressBar.setVisibility(View.GONE);

                    onCheckExists.onCheckExists(true);
                    progressBar.setVisibility(View.GONE);
                } else {
                    onCheckExists.onCheckExists(false);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Log.e("RetrieveDataActivity", "Database Error: " + databaseError.getMessage());
                progressBar.setVisibility(View.GONE);
                onCheckExists.onCheckExists(false);
            }
        });
    }
    interface OnCheckExists{
        default void onCheckExists(boolean isExists){

        }
    }





}
