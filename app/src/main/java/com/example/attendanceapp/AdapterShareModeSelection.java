package com.example.attendanceapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
public class AdapterShareModeSelection extends  RecyclerView.Adapter<AdapterShareModeSelection.ViewHolder> {
    Context context;
    ArrayList<String> group_list = new ArrayList<>();
    TableLayout tableLayout;
    String fileName;
    ProgressBar progressBar;

    BottomSheetDialogFragmentShareFile fm;
    AdapterShareModeSelection(Context context, ArrayList<String> group_list,TableLayout tableLayout,String filename,BottomSheetDialogFragmentShareFile fm,ProgressBar progressBar) {
        this.context = context;
        this.group_list = group_list;
        this.fileName=filename;
        this.tableLayout=tableLayout;
        this.fm=fm;
        this.progressBar=progressBar;

    }


    class ViewHolder extends RecyclerView.ViewHolder{
        CardView card;
        TextView text;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card=itemView.findViewById(R.id.select_group_card);
            text=itemView.findViewById(R.id.group_name);
        }
    }

    @NonNull
    @Override
    public AdapterShareModeSelection.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.recycler_view_group_layout,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterShareModeSelection.ViewHolder holder, int position) {
        holder.text.setText(group_list.get(position).toString());
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fm!=null)
                    fm.dismiss();

                if(holder.getAdapterPosition()==0){
                    shareTableLayoutAsImageWithWhiteBackground(tableLayout,fileName);
                } else if (holder.getAdapterPosition()==1) {
                    convertTableLayoutToExcel(context,tableLayout,fileName);
                }


            }
        });
    }





    @Override
    public int getItemCount() {
        return group_list.size();
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
        File file = new File(context.getExternalCacheDir(), excelFileName+".xlsx");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
            fos.close();
            Toast.makeText(context, "Download Succes", Toast.LENGTH_LONG).show();


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
            if(progressBar!=null)
                progressBar.setVisibility(View.GONE);


        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Download failed", Toast.LENGTH_LONG).show();

        }
    }


    void shareTableLayoutAsImageWithWhiteBackground(TableLayout tableLayout,String filename) {
        // Create a Bitmap of the visible portion of the TableLayout with a white background
        Bitmap bitmap = Bitmap.createBitmap(tableLayout.getWidth(), tableLayout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Draw a white background
        canvas.drawColor(Color.WHITE);

        // Draw the TableLayout on the white background
        tableLayout.draw(canvas);

        // Save the Bitmap to a file
        File imageFile = new File(context.getExternalCacheDir(), filename+".png");
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

            // Create an Intent with action ACTION_SEND
            Intent intent = new Intent(Intent.ACTION_SEND);

            // Set the type of data to be sent
            intent.setType("image/png");

            // Attach the image file to the Intent
            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", imageFile);
            intent.putExtra(Intent.EXTRA_STREAM, uri);

            // Set the subject of the email (optional)
            intent.putExtra(Intent.EXTRA_SUBJECT, "Sharing Table as Image");

            // Set the body of the email (optional)
            intent.putExtra(Intent.EXTRA_TEXT, "Check out this "+filename);

            // Grant temporary read permission to the content URI
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Start the activity
            context.startActivity(Intent.createChooser(intent, "Share Table as Image"));
            if(progressBar!=null)
                progressBar.setVisibility(View.GONE);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception
        }
    }

}


