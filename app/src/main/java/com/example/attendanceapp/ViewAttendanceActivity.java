package com.example.attendanceapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ViewAttendanceActivity extends AppCompatActivity implements AdapterViewAttendance.OnItemClickListener, AdapterGroupSelectionImportStudents.OnImportStudents {

    RecyclerView recyclerView;
    AppCompatButton addStudent;
    Button importStudents;
    LinearLayout nothingLayout;
    TextView Heading;
    String key,flag,date;// group name
    ArrayList<StudentDataModel> arrayList=new ArrayList<>();
    AdapterViewAttendance adapterViewAttendance;
    AdapterTakeAttendance adapterTakeAttendance;
    AdapterReTakeAttendance adapterReTakeAttendance;
    AppCompatButton backButton,takeAttendanceButton,viewDetailAttendance;
    ArrayList<String> group_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance);
        Intent i=getIntent();
        key=i.getStringExtra("key");// group name
        flag=i.getStringExtra("flag");// flag 0 for normal / 1 for take atendance/ 2 for retake attendance
        if(flag.equals("1") || flag.equals("2")){
            date=i.getStringExtra("date");
        }
        Heading=findViewById(R.id.Heading_view_attendance);
        backButton=findViewById(R.id.back_button_view_attendance);
        recyclerView=findViewById(R.id.recycler_view_attendance);
        takeAttendanceButton=findViewById(R.id.take_attendance_submit_button);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        viewDetailAttendance=findViewById(R.id.Detail_view_attendance_button);
        addStudent=findViewById(R.id.floating_button_add_Student);
        nothingLayout=findViewById(R.id.nothing_is_here_linear_layout_view_attendance);
        addStudent.setVisibility(View.GONE);
        importStudents=findViewById(R.id.importStudents_Btn);
        putValuesInArrayList();
        if(flag.equals("1")){
            // arrayList.add(new StudentDataModel(null,null,null,null,null,null));
            adapterTakeAttendance=new AdapterTakeAttendance(this,arrayList);
            recyclerView.setAdapter(adapterTakeAttendance);
            Heading.setText(key);
            takeAttendanceButton.setVisibility(View.VISIBLE);
            takeAttendanceButton.setText("Update Attendance");
        }
        else if(flag.equals("2")){
            adapterReTakeAttendance=new AdapterReTakeAttendance(this,arrayList,date);
            recyclerView.setAdapter(adapterReTakeAttendance);
            Heading.setText(key);
            takeAttendanceButton.setVisibility(View.VISIBLE);
            takeAttendanceButton.setText("Modify Attendance");
        }
        else{


            if(key.equals("Detained Students")){
                Heading.setText(key);
                adapterViewAttendance=new AdapterViewAttendance(this,key,"detained",getSupportFragmentManager(),this);
            }
            else{
                addStudent.setVisibility(View.VISIBLE);
//                Heading.setText("Group: "+key);
                Heading.setText(key);
                viewDetailAttendance.setVisibility(View.VISIBLE);
                adapterViewAttendance=new AdapterViewAttendance(this,key,"simple",getSupportFragmentManager(),this);
            }
            recyclerView.setAdapter(adapterViewAttendance);

        }
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        viewDetailAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent i = new Intent(ViewAttendanceActivity.this, ViewAttendanceInDetail.class);
                    i.putExtra("group", key);
                    startActivity(i);
                    finish();
            }
        });
        takeAttendanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(NetworkUtils.isNetworkAvailable(getApplicationContext())){
                   if(flag.equals("1")){
                       AdapterTakeAttendance adapter = (AdapterTakeAttendance) recyclerView.getAdapter();
                       if (adapter != null) {
                           adapter.updateAttendance(date); // Call the function
                       }
                   }
                   else if(flag.equals("2")) {
                       AdapterReTakeAttendance adapter = (AdapterReTakeAttendance) recyclerView.getAdapter();
                       if (adapter != null) {
                           adapter.updateAttendance(date); // Call the function
                       }
                   }
               }
               else{
                   Toast.makeText(ViewAttendanceActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
               }
            }
        });

        addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(NetworkUtils.isNetworkAvailable(getApplicationContext())){
                   BottomDialogAddStudentFragment fg=BottomDialogAddStudentFragment.newInstance(key);
                   fg.show(getSupportFragmentManager(),fg.getTag());
               }
               else {
                   Toast.makeText(ViewAttendanceActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
               }
            }
        });

        importStudents.setOnClickListener(v -> {
            if(NetworkUtils.isNetworkAvailable(getApplicationContext())){
                importStudents(key.toUpperCase().trim());
            }else{
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void importStudents(String key) {
        addItemToGroupList();
        if(group_list.size()>0){
            BottomSheetFragmentimportStudents fg=BottomSheetFragmentimportStudents.newInstance("8-"+key);//
            fg.show(getSupportFragmentManager(),fg.getTag());
        }else{
            Toast.makeText(this, "No Groups available", Toast.LENGTH_SHORT).show();
        }
    }


    void putValuesInArrayList() {
        DataBaseHelper db=new DataBaseHelper(this);
        if(key.equals("Detained Students"))
            arrayList=db.fetchDetainedStudentData();//to get detained student data
        else
            arrayList=db.fetchGroupData(key);//to get group student data

    }
    AdapterViewAttendance getAdapter(){
        return adapterViewAttendance;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //
        if(flag.equals("0") && !key.equals("Detained Students")){
            int flag=checkDataExists("1",key);
            if(flag==1){
                nothingLayout.setVisibility(View.GONE);
                viewDetailAttendance.setVisibility(View.VISIBLE);
                importStudents.setVisibility(View.GONE);
            } else{
                nothingLayout.setVisibility(View.VISIBLE);
                viewDetailAttendance.setVisibility(View.GONE);
                importStudents.setVisibility(View.VISIBLE);
            }

        }


    }

    Integer checkDataExists(String table,String group){
        DataBaseHelper db= new DataBaseHelper(getApplicationContext());
        return db.checkDataExists(table,group);
    }

    @Override
    public void onItemClick() {
        onResume();
    }


    void addItemToGroupList(){
        group_list=new ArrayList<>();
        DataBaseHelper db= new DataBaseHelper(this);
        group_list=db.fetchGroupTable();
        group_list.remove(key);
    }


    @Override
    public void onImportStudents() {
        getAdapter().putValuesInArrayList();
        onResume();
    }
}