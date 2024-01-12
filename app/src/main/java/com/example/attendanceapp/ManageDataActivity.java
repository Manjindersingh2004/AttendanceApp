package com.example.attendanceapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ManageDataActivity extends AppCompatActivity {
    LinearLayout addStudent,removeStudent,updateStudent,addGroup,removeGroup,renameGroup;
    LinearLayout manageStudentLayout,deleteAllDataLayout;
    ArrayList<String> arrayList=new ArrayList<>();
    AppCompatButton backButton;
    TextView line1,line3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_data);

        addStudent=findViewById(R.id.add_students_layout);
        removeStudent=findViewById(R.id.remove_students_layout);
        updateStudent=findViewById(R.id.update_students_layout);
        addGroup=findViewById(R.id.add_groups_layout);
        removeGroup=findViewById(R.id.remove_group_layout);
        renameGroup=findViewById(R.id.rename_group_layout);
        backButton=findViewById(R.id.back_button_manage_data);

        deleteAllDataLayout=findViewById(R.id.delete_all_data_layout);


        addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                putItemInArrayList();
                if(arrayList.size()>0)
                {
                    BottomDialogAddStudentFragment fg=new BottomDialogAddStudentFragment();
                    fg.show(getSupportFragmentManager(),fg.getTag());
                    //openDialogForAddStudent();
                }
                else
                    Toast.makeText(getApplicationContext(), "Please Manage Group First", Toast.LENGTH_SHORT).show();
            }

        });
        removeStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer flag=checkDataExists("1"," ");
                if(flag==1)
                {
                    BottomDialogRemoveStudentFragment fg=new BottomDialogRemoveStudentFragment();//

                    fg.show(getSupportFragmentManager(),fg.getTag());
                }
                //openDialogForRemoveStudent();
                else
                    Toast.makeText(getApplicationContext(), "Data Not Exixts", Toast.LENGTH_SHORT).show();
            }

        });
        updateStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer flag=checkDataExists("1"," ");
                if(flag==1){
                    //openDialogForUpdateData();
                    BottomDialogUpdateStudentSmallFragment fg=new BottomDialogUpdateStudentSmallFragment();//
                    fg.show(getSupportFragmentManager(),fg.getTag());
                }

                else
                    Toast.makeText(getApplicationContext(), "Data Not Exixts", Toast.LENGTH_SHORT).show();
            }

        });
        addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomDialogAddGroupFragment fg= new BottomDialogAddGroupFragment();
                fg.show(getSupportFragmentManager(),fg.getTag());
            }
        });
        removeGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToGroupList();
                if(arrayList.size()>0){
                    BottomDialogRemoveGroupFragment fg=new BottomDialogRemoveGroupFragment();
                    fg.show(getSupportFragmentManager(),fg.getTag());
                    //openDialogForRemoveGroup();
                }
                else
                    Toast.makeText(getApplicationContext(), "No Group Exists", Toast.LENGTH_SHORT).show();
            }
        });
        renameGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToGroupList();
                if(arrayList.size()>0){
                    BottomDialogRenameGroupFragment fg=new BottomDialogRenameGroupFragment();
                    fg.show(getSupportFragmentManager(),fg.getTag());
                    //openDialogForRenameGroup();
                }
                else
                    Toast.makeText(getApplicationContext(), "No Group Exists", Toast.LENGTH_SHORT).show();
            }
        });


        deleteAllDataLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemToGroupList();
                if(arrayList.size()>0){
                    for(String i : arrayList){
                        removeGroupFromTable(i);//---------------------------
                        updateManageScreen();
                        Toast.makeText(ManageDataActivity.this, "All data is deleted", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateManageScreen();
    }


    void putItemInArrayList(){
        DataBaseHelper db=new DataBaseHelper(this);
        arrayList=db.fetchGroupTable();
    }
    Integer checkDataExists(String table,String group){
        DataBaseHelper db= new DataBaseHelper(this);
        return db.checkDataExists(table,group);
    }
    void addItemToGroupList(){
        DataBaseHelper db= new DataBaseHelper(this);
        arrayList=db.fetchGroupTable();
    }

    void updateManageScreen(){
        putItemInArrayList();
        manageStudentLayout=findViewById(R.id.manage_student_section_linear_layout);
        deleteAllDataLayout=findViewById(R.id.delete_all_data_layout);
        line1=findViewById(R.id.line_patation_1);
        line3=findViewById(R.id.line_patation_3);
        int flag=checkDataExists("1"," ");
        if(arrayList.size()>0) {
            //groups exists
            manageStudentLayout.setVisibility(View.VISIBLE);
            removeGroup.setVisibility(View.VISIBLE);
            renameGroup.setVisibility(View.VISIBLE);
            deleteAllDataLayout.setVisibility(View.VISIBLE);

            line1.setVisibility(View.VISIBLE);
            line3.setVisibility(View.VISIBLE);

        }
        else{
            //groups not exists
            manageStudentLayout.setVisibility(View.GONE);
            removeGroup.setVisibility(View.GONE);
            renameGroup.setVisibility(View.GONE);
            deleteAllDataLayout.setVisibility(View.GONE);

            line1.setVisibility(View.GONE);
            line3.setVisibility(View.GONE);
        }
        if(flag==1){
            //students exists
            removeStudent.setVisibility(View.VISIBLE);
            updateStudent.setVisibility(View.VISIBLE);

        }
        else{
            //student not exists
            removeStudent.setVisibility(View.GONE);
            updateStudent.setVisibility(View.GONE);

        }
    }

    void removeGroupFromTable(String group){
        DataBaseHelper db= new DataBaseHelper(getApplicationContext());
        db.removeGroup(group);
    }



}