package com.example.attendanceapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterTakeAttendance extends RecyclerView.Adapter<AdapterTakeAttendance.ViewHolder> {

    ArrayList<StudentDataModel> arraylist=new ArrayList<>();
    ArrayList<String> attendance;
    Context context;


    AdapterTakeAttendance(Context context, ArrayList<StudentDataModel> arraylist){
        this.context=context;
        this.arraylist=arraylist;
        attendance=new ArrayList<>(arraylist.size());
        for (int i = 0; i < arraylist.size(); i++) {
            attendance.add("0");
        }
    }
    AdapterTakeAttendance(){
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.recycler_view_take_attendance_layout,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(arraylist.get(holder.getAdapterPosition()).NAME.toUpperCase());
        holder.rollno.setText(arraylist.get(holder.getAdapterPosition()).ROLL_NO);

        int color=R.color.c15;
        holder.updateAttendanceButton.setCardBackgroundColor(ContextCompat.getColor(context, color));


        // Update the button background based on the value in the attendance list
        if (attendance.get(position).equals("0")) {
            holder.attendance_tick_button.setBackground(ContextCompat.getDrawable(context, R.drawable.uncheck));
        } else {
            holder.attendance_tick_button.setBackground(ContextCompat.getDrawable(context, R.drawable.check));
        }

        holder.updateAttendanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclickItem(holder);
            }
        });
        holder.attendance_tick_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclickItem(holder);
            }
        });

    }

    private void onclickItem(ViewHolder holder) {
        int clickedPosition = holder.getAdapterPosition();

        // Toggle the attendance state in the list
        if (attendance.get(clickedPosition).equals("0")) {
            attendance.set(clickedPosition, "1");
        } else {
            attendance.set(clickedPosition, "0");
        }

        // Update the view for the clicked button
        if (attendance.get(clickedPosition).equals("0")) {
            holder.attendance_tick_button.setBackground(ContextCompat.getDrawable(context, R.drawable.uncheck));
        } else {
            holder.attendance_tick_button.setBackground(ContextCompat.getDrawable(context, R.drawable.check));
        }
    }

    @Override
    public int getItemCount() {
        //Toast.makeText(context, arraylist.size()+"", Toast.LENGTH_SHORT).show();
        return arraylist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView name,rollno;
        AppCompatButton attendance_tick_button;
        CardView updateAttendanceButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name_recycler_view);
            rollno=itemView.findViewById(R.id.roll_no_recycler_view);
            attendance_tick_button=itemView.findViewById(R.id.recycler_view_attendance_button_tick);
            updateAttendanceButton=itemView.findViewById(R.id.recycler_view_attendance_card);
        }
    }

    void updateAttendance(String date){
        DataBaseHelper db=new DataBaseHelper(context);
        db.incrementAttendanceStudentTable(arraylist,attendance);
        db.entryInAttendanceTable(arraylist,attendance,date);
        Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show();

        new DataBaseHelper(context).insertAttendanceOnline(arraylist.get(0).GROUP);

        ((Activity)context).finish();
    }

}
