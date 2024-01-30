package com.example.attendanceapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
public class AdapterGroupSelection extends  RecyclerView.Adapter<AdapterGroupSelection.ViewHolder> {
    Context context;
    ArrayList<String> group_list = new ArrayList<>();
    String flag;
    BottomSheetFragment bottomSheetFragment;

    AdapterGroupSelection(Context context, ArrayList<String> group_list,String flag,BottomSheetFragment bottomSheetFragment ) {
        this.context = context;
        this.group_list = group_list;
        this.flag=flag;
        this.bottomSheetFragment=bottomSheetFragment;
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
    public AdapterGroupSelection.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.recycler_view_group_layout,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterGroupSelection.ViewHolder holder, int position) {
        holder.text.setText(group_list.get(position).toString());
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag=checkDataExists("1",holder.text.getText().toString());
                if(flag==1){
                    if(bottomSheetFragment!=null)
                        bottomSheetFragment.dismiss();
                    onClickGroupCard(group_list.get(holder.getAdapterPosition()).toString());
                }
                else
                    Toast.makeText(context, "Please Add Students", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void onClickGroupCard(String group) {
        if(flag=="2"){
            // reset attendance
            DataBaseHelper db =new DataBaseHelper(context);
            db.reSetAttendance(group);
            db.deleteAttendanceRows(group);
            Toast.makeText(context, "Attendance of "+group+" is Reset", Toast.LENGTH_SHORT).show();

            new DataBaseHelper(context).removeGroupIntoFirebase(group.toUpperCase());


        } else if (flag=="1") {
            //take attendance
            AppCompatActivity appCompatActivity = (AppCompatActivity) context;// chat gpt
            BottomSheetDialogFragmentDatePicker fg=BottomSheetDialogFragmentDatePicker.newInstance(group,"take_attendance");
            fg.show(appCompatActivity.getSupportFragmentManager(),fg.getTag());

        } else if (flag=="3") {
            //delete attendance
            AppCompatActivity appCompatActivity = (AppCompatActivity) context;// chat gpt
            BottomSheetDialogFragmentDatePicker fg=BottomSheetDialogFragmentDatePicker.newInstance(group,"delete_attendance");
            fg.show(appCompatActivity.getSupportFragmentManager(),fg.getTag());

        } else if (flag=="4") {
            //retake attendance
            AppCompatActivity appCompatActivity = (AppCompatActivity) context;// chat gpt
            BottomSheetDialogFragmentDatePicker fg=BottomSheetDialogFragmentDatePicker.newInstance(group,"retake_attendance");
            fg.show(appCompatActivity.getSupportFragmentManager(),fg.getTag());

        } else if (flag=="7") {
            //download attendance
            Intent i=new Intent(context, ViewAttendanceInDetail.class);
            i.putExtra("group",group);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } else{
            Intent i=new Intent(context, ViewAttendanceActivity.class);
            i.putExtra("key",group);
            i.putExtra("flag",flag);
            context.startActivity(i);
        }

    }



    @Override
    public int getItemCount() {
        return group_list.size();
    }

    Integer checkDataExists(String table,String group){
        DataBaseHelper db= new DataBaseHelper(context);
        return db.checkDataExists(table,group);
    }

}


