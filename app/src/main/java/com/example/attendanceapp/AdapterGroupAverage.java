package com.example.attendanceapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;

public class AdapterGroupAverage extends RecyclerView.Adapter<AdapterGroupAverage.ViewHolder> {


    Context context;
    ArrayList<String> group_list = new ArrayList<>();

    AdapterGroupAverage(Context context, ArrayList<String> group_list ) {
        this.context = context;
        this.group_list = group_list;
    }

    @NonNull
    @Override
    public AdapterGroupAverage.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.group_progress_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterGroupAverage.ViewHolder holder, int position) {

        holder.group.setText(group_list.get(position));
        DataBaseHelper db=new DataBaseHelper(context);
        ArrayList<Integer> progressData=db.getAverageGroupAttendance(group_list.get(position));
        int students=progressData.get(0);
        int lecture=progressData.get(1);
        int progress=progressData.get(2);
        holder.per.setText(progress+"%");
        holder.progressBar.setProgress(progress);
        holder.students.setText("Students: "+students);
        holder.lectures.setText("Lecture: "+lecture);


        if(progress<75){
            holder.progressBar.setIndicatorColor(ContextCompat.getColor(context,R.color.red_));
        }
        else{
            holder.progressBar.setIndicatorColor(ContextCompat.getColor(context,R.color.green_));
        }
    }

    @Override
    public int getItemCount() {
        return group_list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        TextView group,per,students,lectures;
        //ProgressBar progressBar;

        CircularProgressIndicator progressBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            group=itemView.findViewById(R.id.group_progress_name);
            per=itemView.findViewById(R.id.percentage_group_progress);
            progressBar=itemView.findViewById(R.id.progress_bar);
            students=itemView.findViewById(R.id.group_progress_total_students);
            lectures=itemView.findViewById(R.id.group_progress_total_lecture);
        }
    }
}
