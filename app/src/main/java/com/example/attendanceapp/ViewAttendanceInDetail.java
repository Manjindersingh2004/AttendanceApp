package com.example.attendanceapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ViewAttendanceInDetail extends AppCompatActivity {
    TableLayout tableLayout;
    TextView heading;
    AppCompatButton backbutton;
    Button exportButton;
    String group="hello";
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance_in_detail);
        tableLayout=findViewById(R.id.Table_layout);
        heading=findViewById(R.id.Heading_Table_view);
        backbutton=findViewById(R.id.back_button_table);
        exportButton=findViewById(R.id.exportButton_detail_activity);
        progressBar=findViewById(R.id.progress);

        Intent i=getIntent();
        group=i.getStringExtra("group");

//        heading.setText("Group: "+group);
        heading.setText(group);
        DataForTAble();

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check if the permission is not granted
                if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // Request the permission
                    requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},444);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    convertTableLayoutToExcel(getApplicationContext(),tableLayout,"Attendance_of_"+group+".xlsx");

                }
            }
        });
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    void DataForTAble(){
        ArrayList<StudentDataModel> arrayList=new ArrayList<>();
        ArrayList<ArrayList<String>> attendance=new ArrayList<>();
        ArrayList<String> dates=new ArrayList<>();
        DataBaseHelper db=new DataBaseHelper(getApplicationContext());
        dates=db.getDatesList(group);
        arrayList=db.fetchGroupData(group);
        for(int i=0;i<arrayList.size();i++){
            String rollno=arrayList.get(i).ROLL_NO;
            ArrayList<String> row=new ArrayList<>();
            row=db.getAttendanceAttendanceTAbleOfRollNo(group,rollno);
            attendance.add(row);
        }

        EnterDataInTable(arrayList,attendance,dates);
    }

    void EnterDataInTable(ArrayList<StudentDataModel> arrayList,ArrayList<ArrayList<String>> attendance,ArrayList<String> date) {
        String rollno,name;
        // Specify the path to the CSV file

        date.add(0,"Roll No");
        date.add(1,"Name");

        TableRow row_heading= new TableRow(this);
        for (int i = 0; i < date.size(); i++) {
            TextView cell = new TextView(this);
            cell.setTextSize(25);
//            cell.setBackgroundColor(getColor(R.color.blue_));
            cell.setPadding(10,5,10,5);
            cell.setBackgroundResource(R.drawable.border);
            cell.setText(date.get(i));
            row_heading.addView(cell);
        }
        tableLayout.addView(row_heading);


        for(int i=0;i<arrayList.size();i++){
                ArrayList<String> row_list=new ArrayList<>();
                row_list=attendance.get(i);
                rollno=arrayList.get(i).ROLL_NO;
                name=arrayList.get(i).NAME;

                row_list.add(0,rollno);
                row_list.add(1,name);

                TableRow row = new TableRow(this);
                for (int j = 0; j < row_list.size(); j++) {
                    TextView cell = new TextView(this);
                    if(row_list.get(j)==null)
                        cell.setText("Absent");
                    else
                        cell.setText(row_list.get(j));
                    cell.setTextSize(20);
                    cell.setBackgroundResource(R.drawable.border);
                    if(row_list.get(j).equals("Present"))
                        cell.setTextColor(getColor(R.color.green_));
                    else if (row_list.get(j).equals("Absent"))
                        cell.setTextColor(getColor(R.color.red_));
                    cell.setPadding(10,5,10,5);
                    cell.setGravity(Gravity.CENTER);
                    row.addView(cell);
                }
                tableLayout.addView(row);
                //add row
        }
    }

    void convertTableLayoutToExcel(Context context, TableLayout tableLayout, String excelFileName) {
        // Create a new Excel workbook
        Workbook workbook = new XSSFWorkbook();

        // Create a sheet in the workbook
        Sheet sheet = workbook.createSheet("Attendance Record");

        // Get the number of rows and columns in the TableLayout
        int numRows = tableLayout.getChildCount();
        int numCols = ((TableRow) tableLayout.getChildAt(0)).getChildCount();

        for (int i = 0; i < numRows; i++) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(i);

            Row row = sheet.createRow(i);

            for (int j = 0; j < numCols; j++) {
                TextView cellView = (TextView) tableRow.getChildAt(j);

                Cell cell = row.createCell(j);
                cell.setCellValue(cellView.getText().toString());
            }
        }



        File rootDir = new File(Environment.getExternalStorageDirectory(), "Attendance App/Group "+group);
        rootDir.mkdirs(); // Create the directory if it doesn't exist
        File file = new File(rootDir, excelFileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
            fos.close();
            Toast.makeText(getApplicationContext(), "Download Succes", Toast.LENGTH_LONG).show();

            progressBar.setVisibility(View.GONE);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Download failed", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 444) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permision Not Granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

}