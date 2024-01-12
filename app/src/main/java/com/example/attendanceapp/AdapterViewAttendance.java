package com.example.attendanceapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;

public class  AdapterViewAttendance extends RecyclerView.Adapter<AdapterViewAttendance.ViewHolder> {

    ArrayList<StudentDataModel> arraylist=new ArrayList<>();
    Context context;

    AdapterViewAttendance(Context context, ArrayList<StudentDataModel> arraylist){
        this.context=context;
        this.arraylist=arraylist;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.recycler_view_attendance_layout,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int color=getColor(position);
        holder.attandanceCard.setCardBackgroundColor(ContextCompat.getColor(context, color));

        holder.name.setText(arraylist.get(position).NAME.toUpperCase());
        holder.rollno.setText(arraylist.get(position).ROLL_NO);
        holder.group.setText(arraylist.get(position).GROUP);
        holder.per.setText((arraylist.get(position).PERCENTAGE)+"%");
        holder.progress.setProgress(Integer.parseInt(arraylist.get(position).PERCENTAGE));
        if(Integer.parseInt(arraylist.get(position).PERCENTAGE)<75){
            holder.progress.setIndicatorColor(ContextCompat.getColor(context,R.color.red_));
        }
        else{
            holder.progress.setIndicatorColor(ContextCompat.getColor(context,R.color.green_));
        }
        holder.ratio.setText((arraylist.get(position).ATEND_LEC)+"/"+arraylist.get(position).TOTAL_LEC);
    }

    @Override
    public int getItemCount() {
        if(arraylist.size()<1)
            Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
        return arraylist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView name,rollno,ratio,group,per;
        CardView attandanceCard;
        CircularProgressIndicator progress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name_recycler_view);
            rollno=itemView.findViewById(R.id.roll_no_recycler_view);
            per=itemView.findViewById(R.id.percentage_group_progress_view_attendance);
            ratio=itemView.findViewById(R.id.atend_vs_total_lec_recycler_view);
            group=itemView.findViewById(R.id.group_recycler_view);
            attandanceCard=itemView.findViewById(R.id.recycler_view_attendance_layout);
            progress=itemView.findViewById(R.id.progress_bar_view_attendance);
        }
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
