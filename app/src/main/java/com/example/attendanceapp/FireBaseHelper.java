package com.example.attendanceapp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
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
    String COLLAGE_ID = "12345";
    FirebaseDatabase database;
    DatabaseReference databaseReferenceGroups,databaseReferenceStudents;

    String GROUPS = "GROUPS";
    String STUDENTS = "STUDENTS";
    String ATTENDANCE = "ATTENDANCE";
    String DATE="DATE";
    String NAME="NAME";

    FireBaseHelper(Context context){
        this.context=context;
        database=FirebaseDatabase.getInstance();
        databaseReferenceGroups=database.getReference(COLLAGES).child(COLLAGE_ID).child(GROUPS);

    }

//    -------------------------------------------------------------------------------- Group Management -------------------------------------------------------------------------------------------------------------------------------
    void addGroupToGroupTable(String group, String total_lec) {
        GroupDataModel groups = new GroupDataModel(total_lec);
        databaseReferenceGroups.child(group.toUpperCase()).setValue(groups).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Data was successfully written
                    Toast.makeText(context, "Group added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle the error
                    Toast.makeText(context, "Failed to add group", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed to add group", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void removeGroup(String group) {
        DatabaseReference groupRef = databaseReferenceGroups.child(group.toUpperCase());

        // Remove the group node
        groupRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Group removed successfully
                    Toast.makeText(context, "Group removed successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle the error
                    Toast.makeText(context, "Failed to remove group", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed to remove group", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void renameGroup(String key, String group) {
        DatabaseReference oldGroupRef = databaseReferenceGroups.child(key.toUpperCase());
        DatabaseReference newGroupRef = databaseReferenceGroups.child(group.toUpperCase());

        // Read the data from the old group
        oldGroupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get the data from the old group
                    GroupDataModel data = dataSnapshot.getValue(GroupDataModel.class);

                    // Write the data to the new group
                    newGroupRef.setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // New group created successfully, now remove the old group
                                oldGroupRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(context, "Group renamed successfully", Toast.LENGTH_SHORT).show();

                                        } else {
                                            // Handle the error
                                            Toast.makeText(context, "Failed to remove old group", Toast.LENGTH_SHORT).show();
                                           // callback.onGroupRename();
                                        }
                                    }
                                });
                            } else {
                                // Handle the error
                                Toast.makeText(context, "Failed to create new group", Toast.LENGTH_SHORT).show();
                                //callback.onGroupRename();
                            }
                        }
                    });
                } else {
                    // Handle the case where the old group doesn't exist
                    Toast.makeText(context, "Old group not found", Toast.LENGTH_SHORT).show();
                    //callback.onGroupRename();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
                Toast.makeText(context, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void fetchGroupTable(final GroupFetchCallback callback) {
        ArrayList<String> groupList = new ArrayList<>();

        // Attach a ValueEventListener to the groups reference
        databaseReferenceGroups.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if the data exists
                if (dataSnapshot.exists()) {
                    // Iterate over the child nodes to get group names
                    for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                        String groupName = groupSnapshot.getKey();
                        groupList.add(groupName);
                    }

                    // Sort the group names in ascending order
                    Collections.sort(groupList);

                    // Invoke the callback with the sorted group list
                    callback.onGroupFetch(groupList);
                } else {
                    // Invoke the callback with an empty list if no groups exist
                    callback.onGroupFetch(new ArrayList<>());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
                callback.onGroupFetch(new ArrayList<>());// You can also handle errors more gracefully
            }
        });
    }

    public interface GroupFetchCallback {
        void onGroupFetch(ArrayList<String> groupList);
    }
    public interface GroupRenameUiUpdateCallback {
        void onGroupRename();
    }

    Integer checkGroupExists(String group){
        final int[] exists = {0};
        fetchGroupTable(new GroupFetchCallback() {
            @Override
            public void onGroupFetch(ArrayList<String> groupList) {
                if(groupList.contains(group.toUpperCase())){
                    exists[0] = 1;
                }
            }
        });
        return exists[0];
    }


//------------------------------------------------------------------------------------ group Management------------------------------------------------------------------------------




}
