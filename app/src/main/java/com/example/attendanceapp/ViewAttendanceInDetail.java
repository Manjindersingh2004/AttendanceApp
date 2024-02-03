package com.example.attendanceapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
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
    TextView heading,search;
    AppCompatButton backbutton;
    Button exportButton,share,find;
    String group="hello";
    ProgressBar progressBar;

    int pos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance_in_detail);
        tableLayout=findViewById(R.id.Table_layout);
        heading=findViewById(R.id.Heading_Table_view);
        backbutton=findViewById(R.id.back_button_table);
        exportButton=findViewById(R.id.exportButton_detail_activity);
        progressBar=findViewById(R.id.progress);
        search=findViewById(R.id.more_button_table);
        Intent i=getIntent();
        group=i.getStringExtra("group");

//        heading.setText("Group: "+group);
        heading.setText(group);
        DataForTAble(1);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v,0);
                //EnterDataInTableOneColumn(arrayList,attendance,dates,dates.size()-1);
            }
        });

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check if the permission is not granted
//                if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    // Request the permission
//                    requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},444);
//                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    BottomSheetDialogFragmentShareFile fg= BottomSheetDialogFragmentShareFile.newInstance("Attendance_of_"+group);
                    fg.show(getSupportFragmentManager(),fg.getTag());
                    //convertTableLayoutToExcel(getApplicationContext(),tableLayout,"Attendance_of_"+group+".xlsx");
                    //shareTableLayoutAsImageWithWhiteBackground(tableLayout);
//                }
            }
        });
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    void DataForTAble(int mode){
        tableLayout.removeAllViews();
        ArrayList<StudentDataModel>  arrayList=new ArrayList<>();
        ArrayList<ArrayList<String>> attendance=new ArrayList<>();
        ArrayList<String>  dates=new ArrayList<>();



        DataBaseHelper db=new DataBaseHelper(getApplicationContext());
        dates=db.getDatesList(group);
        arrayList=db.fetchGroupData(group);
        for(int i=0;i<arrayList.size();i++){
            String rollno=arrayList.get(i).ROLL_NO;
            ArrayList<String> row=new ArrayList<>();
            row=db.getAttendanceAttendanceTAbleOfRollNo(group,rollno);
            attendance.add(row);
        }

        if(mode==1)
        EnterDataInTable(arrayList,attendance,dates);
        else
            EnterDataInTableOneColumn(arrayList,attendance,dates,pos);
    }

    void EnterDataInTable(ArrayList<StudentDataModel> arrayList,ArrayList<ArrayList<String>> attendance,ArrayList<String> date) {
        String rollno,name;

        // Specify the path to the CSV file


        date.add(0,"Roll No");
        date.add(1,"Name");

        TableRow row_heading= new TableRow(this);
        for (int i = 0; i < date.size(); i++) {
            TextView cell = new TextView(this);
            cell.setTextSize(18);
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
                    cell.setTextSize(15);
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



//        File rootDir = new File(Environment.getExternalStorageDirectory(), "Attendance App/Group "+group);
//        rootDir.mkdirs(); // Create the directory if it doesn't exist
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), excelFileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
            fos.close();
            Toast.makeText(getApplicationContext(), "Download Succes", Toast.LENGTH_LONG).show();


            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);

            // Create an Intent with action ACTION_SEND
            Intent intent = new Intent(Intent.ACTION_SEND);

            // Set the type of data to be sent
            intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            // Attach the file to the Intent using the FileProvider Uri
            intent.putExtra(Intent.EXTRA_STREAM, uri);

            // Set the subject of the email (optional)
            intent.putExtra(Intent.EXTRA_SUBJECT, "Sharing Excel File");

            // Set the body of the email (optional)
            intent.putExtra(Intent.EXTRA_TEXT, "Check out this Excel file!");

            // Grant temporary read permission to the content URI
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Start the activity
            context.startActivity(Intent.createChooser(intent, "Share Excel File").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));


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


    void EnterDataInTableOneColumn(ArrayList<StudentDataModel> arrayList,ArrayList<ArrayList<String>> attendance,ArrayList<String> date,int pos) {

        String rollno,name;


        // Specify the path to the CSV file

        date.add(0,"Roll No");
        date.add(1,"Name");
        date.add(2,date.get(pos+2).toString());
        TableRow row_heading= new TableRow(this);
        for (int i = 0; i < 3; i++) {
            TextView cell = new TextView(this);
            cell.setTextSize(18);
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
            String att=row_list.get(pos).toString();
            row_list.add(0,rollno);
            row_list.add(1,name);
            row_list.add(2,att);
            TableRow row = new TableRow(this);
            for (int j = 0; j <3; j++) {
                TextView cell = new TextView(this);
                if(row_list.get(j)==null)
                    cell.setText("Absent");
                else
                    cell.setText(row_list.get(j));
                cell.setTextSize(15);
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

    private void showPopupMenu(View view,int pos) {
        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.filter_menu_layout, popupMenu.getMenu());
        int id1=R.id.All_dates_item_menu,id2=R.id.selective_date_item_menu;


        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getItemId()==id1){
                    DataForTAble(1);
                    return true;
                } else if (item.getItemId()==id2) {
                    BottomSheetDialogFragmentDatePicker fg=BottomSheetDialogFragmentDatePicker.newInstance(group,"filter_attendance");
                    fg.show(getSupportFragmentManager(),fg.getTag());
                    return true;
                }
                else{
                    return true;
                }
            }
        });

        popupMenu.show();
    }

    void date(String date){
        pos=0;
        String[] parts = date.split("-");
        if (parts.length == 3) {
            String year = parts[0];
            String month = parts[1];
            String day = parts[2];
            int month_num = Integer.parseInt(month);
            switch (month_num) {
                case 1:
                    month = " Jan,";
                    break;
                case 2:
                    month = " Feb,";
                    break;
                case 3:
                    month = " Mar,";
                    break;
                case 4:
                    month = " Apr,";
                    break;
                case 5:
                    month = " May,";
                    break;
                case 6:
                    month = " June";
                    break;
                case 7:
                    month = " Jul,";
                    break;
                case 8:
                    month = " Aug,";
                    break;
                case 9:
                    month = " Sep,";
                    break;
                case 10:
                    month = " Oct,";
                    break;
                case 11:
                    month = " Nov,";
                    break;
                case 12:
                    month = " Dec,";
                    break;
                default:
                    month = " Invalid,";
            }
            // Reformat the date in "DD-MM-YYYY" format
            date = day + month + year;
        }
        pos=new DataBaseHelper(getApplicationContext()).getDatesList(group).indexOf(date);

        Toast.makeText(this, pos+"", Toast.LENGTH_SHORT).show();
        DataForTAble(0);
    }




    void shareTableLayoutAsImageWithWhiteBackground(TableLayout tableLayout) {
        // Create a Bitmap of the visible portion of the TableLayout with a white background
        Bitmap bitmap = Bitmap.createBitmap(tableLayout.getWidth(), tableLayout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Draw a white background
        canvas.drawColor(Color.WHITE);

        // Draw the TableLayout on the white background
        tableLayout.draw(canvas);

        // Save the Bitmap to a file
        File imageFile = new File(getExternalCacheDir(), "table_image.png");
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

            // Create an Intent with action ACTION_SEND
            Intent intent = new Intent(Intent.ACTION_SEND);

            // Set the type of data to be sent
            intent.setType("image/png");

            // Attach the image file to the Intent
            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", imageFile);
            intent.putExtra(Intent.EXTRA_STREAM, uri);

            // Set the subject of the email (optional)
            intent.putExtra(Intent.EXTRA_SUBJECT, "Sharing Table as Image");

            // Set the body of the email (optional)
            intent.putExtra(Intent.EXTRA_TEXT, "Check out this table!");

            // Grant temporary read permission to the content URI
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Start the activity
            startActivity(Intent.createChooser(intent, "Share Table as Image"));

        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception
        }
    }
    TableLayout getTableLayout(){
        return tableLayout;
    }
    ProgressBar getProgressBar(){
        return progressBar;
    }
}