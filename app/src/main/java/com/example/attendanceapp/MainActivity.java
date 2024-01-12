package com.example.attendanceapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    CardView viewAttendanceCard,detainedListCard,takeAttendanceCard,resetAttendance,modifyAttendance,deleteAttendance;
    AppCompatButton manageButton;
    ArrayList<String> arrayList;

    LinearLayout attendanceSection,nothingHere;
    RecyclerView recyclerView;
    AdapterGroupAverage adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewAttendanceCard=findViewById(R.id.view_attendance_card);
        detainedListCard=findViewById(R.id.detained_list_card);
        takeAttendanceCard=findViewById(R.id.take_attendance_card);
        manageButton=findViewById(R.id.floating_buttonHomePage);
        resetAttendance=findViewById(R.id.reset_attendance_card);
        modifyAttendance=findViewById(R.id.modify_attendance_card);
        deleteAttendance=findViewById(R.id.delete_attendance_card);
        addItemToGroupList();
        recyclerView=findViewById(R.id.recycler_view_group_progress);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new AdapterGroupAverage(this,arrayList);
        recyclerView.setAdapter(adapter);

        manageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(getApplicationContext(),ManageDataActivity.class);
                startActivity(i);

            }
        });
        viewAttendanceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag=checkDataExists("2"," ");
                if(flag==1){
                    BottomSheetFragment fg=BottomSheetFragment.newInstance("0");//
                    fg.show(getSupportFragmentManager(),fg.getTag());//
                }
                else{
                    Toast.makeText(MainActivity.this, "Please Add Students", Toast.LENGTH_SHORT).show();
                }

            }
        });
        detainedListCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag=checkDetainedStudentExist();
                if(flag==1){
                    Intent i=new Intent(MainActivity.this, ViewAttendanceActivity.class);
                    i.putExtra("key","Detained Students");
                    i.putExtra("flag","0");
                    startActivity(i);
                }
                else{
                    Toast.makeText(MainActivity.this, "No Detained Students", Toast.LENGTH_SHORT).show();
                }
            }
        });
        takeAttendanceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag=checkDataExists("2"," ");
                if(flag==1){
                    BottomSheetFragment fg=BottomSheetFragment.newInstance("1");//
                    //adapterGroupSelection=new AdapterGroupSelection(fg);
                    fg.show(getSupportFragmentManager(),fg.getTag());
                }
                else{
                    Toast.makeText(MainActivity.this, "Please Add Students", Toast.LENGTH_SHORT).show();
                }
            }
        });

        resetAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToGroupList();
                if(arrayList.size()>0){
                    BottomSheetFragment fg=BottomSheetFragment.newInstance("2");//
                    fg.show(getSupportFragmentManager(),fg.getTag());
                    //openDialogForRemoveGroup();
                }
                else
                    Toast.makeText(getApplicationContext(), "No Groups Found", Toast.LENGTH_SHORT).show();

            }
        });

        modifyAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemToGroupList();
                if(arrayList.size()>0){
                    BottomSheetFragment fg=BottomSheetFragment.newInstance("4");//
                    fg.show(getSupportFragmentManager(),fg.getTag());
                }
            }
        });

        deleteAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetFragment fg=BottomSheetFragment.newInstance("3");
                fg.show(getSupportFragmentManager(),fg.getTag());
            }
        });


    }

    Integer checkDataExists(String table,String group){
        DataBaseHelper db= new DataBaseHelper(this);
        return db.checkDataExists(table,group);
    }
    Integer checkDetainedStudentExist(){
        DataBaseHelper db= new DataBaseHelper(this);
        return db.checkDetainedStudentExist();
    }

    @Override
    protected void onResume() {
        super.onResume();
        addItemToGroupList();
        adapter=new AdapterGroupAverage(this,arrayList);
        recyclerView.setAdapter(adapter);
        attendanceSection=findViewById(R.id.student_attendance_section_linear_layout);
        nothingHere=findViewById(R.id.nothing_is_here_linear_layout);
        Integer flag=checkDataExists("2"," ");
        if(flag==1){
            nothingHere.setVisibility(View.GONE);
            attendanceSection.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        } else{
            attendanceSection.setVisibility(View.GONE);
            nothingHere.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 444) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
                Toast.makeText(this, "permision granted", Toast.LENGTH_SHORT).show();


            } else {
                // Permission was denied
                Toast.makeText(this, "Required to export file", Toast.LENGTH_SHORT).show();
            }
        }
    }
    void addItemToGroupList(){
        arrayList=new ArrayList<>();
        DataBaseHelper db= new DataBaseHelper(this);
        arrayList=db.fetchGroupTable();
    }
}