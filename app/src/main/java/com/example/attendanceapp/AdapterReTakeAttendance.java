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

public class AdapterReTakeAttendance extends RecyclerView.Adapter<AdapterReTakeAttendance.ViewHolder> {

    ArrayList<StudentDataModel> arraylist=new ArrayList<>();
    ArrayList<String> attendance=new ArrayList<>();
    ArrayList<String> pastAttendance=new ArrayList<>();
    Context context;
    String date;


    AdapterReTakeAttendance(Context context, ArrayList<StudentDataModel> arraylist,String date){
        this.context=context;
        this.arraylist=arraylist;
        this.date=date;
        attendance=fetchAttendanceRow();
        pastAttendance=fetchAttendanceRow();
    }

    private ArrayList<String> fetchAttendanceRow() {
        DataBaseHelper db=new DataBaseHelper(context);
        return db.getAttendanceListFromAttendanceTableByDate(date,arraylist.get(0).GROUP,arraylist);
    }

    AdapterReTakeAttendance(){
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

        int color=getColor(position);
        holder.updateAttendanceButton.setCardBackgroundColor(ContextCompat.getColor(context, color));


        // Update the button background based on the value in the attendance list
        if (attendance.get(position).equals("0")) {
            holder.attendance_tick_button.setBackground(ContextCompat.getDrawable(context, R.drawable.uncheck));
        } else {
            holder.attendance_tick_button.setBackground(ContextCompat.getDrawable(context, R.drawable.check));
        }

        holder.attendance_tick_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

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
    void manageAtendanceIcon(int pos,final View buttonView){
        if(attendance.get(pos).equals("0")){
            buttonView.setBackground(ContextCompat.getDrawable(context,R.drawable.check));
            attendance.set(pos,"1");
            //Toast.makeText(context, "2: "+pos+" present", Toast.LENGTH_SHORT).show();

        }
        else {
            buttonView.setBackground(ContextCompat.getDrawable(context,R.drawable.uncheck));
            attendance.set(pos,"0");
            //Toast.makeText(context, "2: "+pos+" absent", Toast.LENGTH_SHORT).show();
        }
        String a="";
        for(int i=0;i<attendance.size();i++)
            a+=attendance.get(i)+" ";
        Toast.makeText(context, a+"", Toast.LENGTH_SHORT).show();
    }
    void updateAttendance(String date){
        DataBaseHelper db=new DataBaseHelper(context);
        db.updateStudentTableAttendanceRetake(arraylist,pastAttendance,attendance);//--------
        db.deleteRowByDateInAttendanceTable(date,arraylist.get(0).GROUP);//--------
        db.entryInAttendanceTable(arraylist,attendance,date);
        Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show();
        ((Activity)context).finish();
    }

    int getColor(int pos){
        int mod=pos%8;
        return R.color.c15;
//        if(mod==0)
//            return R.color.c0;
//        else if(mod==1)
//            return R.color.c1;
//        else if(mod==2)
//            return R.color.c2;
//        else if(mod==3)
//            return R.color.c3;
//        else if(mod==4)
//            return R.color.c4;
//        else if(mod==5)
//            return R.color.c5;
//        else if(mod==6)
//            return R.color.c6;
//        else if(mod==7)
//            return R.color.c7;
//        else if(mod==8)
//            return R.color.c8;
//        else if(mod==9)
//            return R.color.c9;
//        else if(mod==10)
//            return R.color.c10;
//        else
//            return R.color.c11;

    }

}