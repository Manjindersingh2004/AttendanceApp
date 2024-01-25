package com.example.attendanceapp;

import android.content.Context;
import android.graphics.Color;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;

public class  AdapterViewAttendance extends RecyclerView.Adapter<AdapterViewAttendance.ViewHolder> {

    static ArrayList<StudentDataModel> arraylist=new ArrayList<>();
    Context context;
    String mode,key;
    Vibrator vibrator;
FragmentManager fm;
OnItemClickListener listener;


    AdapterViewAttendance(Context context,String key,  String mode, FragmentManager fm,OnItemClickListener listener){
        this.context=context;
        this.mode=mode;
        this.fm=fm;
        this.key=key;
        putValuesInArrayList();
        this.listener=listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.recycler_view_attendance_layout,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int color=R.color.c15;
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


        holder.attandanceCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(mode.equals("simple")){

                    vibrate(20);
                    int pos=holder.getAdapterPosition();
                    showPopupMenu(v,arraylist.get(pos).ROLL_NO,pos);


                }

                return false;
            }
        });
    }

    @Override
    public int getItemCount() {

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



    private void showPopupMenu(View view,String rollno,int pos) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.delete_update_menu, popupMenu.getMenu());
        int id1=R.id.delete_item_menu,id2=R.id.update_item_menu;

        popupMenu.getMenu().getItem(0).setTitle("Delete Student");
        popupMenu.getMenu().getItem(1).setTitle("Update Student");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

               if(item.getItemId()==id1){
                   removeStudentFromTable(rollno,arraylist.get(0).GROUP);
                   arraylist.remove(pos);
                   notifyItemRemoved(pos);
                   if(listener!=null){
                       listener.onItemClick();
                   }
                   return true;
               } else if (item.getItemId()==id2) {
                   BottomDialogUpdateStudentFragment fg=BottomDialogUpdateStudentFragment.newInstance(rollno,arraylist.get(pos).ROLL_NO,arraylist.get(pos).NAME,arraylist.get(pos).GROUP);
                   fg.show(fm, fg.getTag());
                   if(listener!=null){
                       listener.onItemClick();
                   }
                   //putValuesInArrayList();
                   return true;
               }
               else{
                   return true;
               }
            }
        });

        popupMenu.show();
    }

    void removeStudentFromTable(String rollno,String group){
        new DataBaseHelper(context).removeStudent(rollno,group);
    }

    void putValuesInArrayList() {
        ArrayList<StudentDataModel> newData;
        if (key.equals("Detained Students"))
            newData = new DataBaseHelper(context).fetchDetainedStudentData();
        else
            newData =new DataBaseHelper(context).fetchGroupData(key);

        // Update the existing arraylist with the new data
        arraylist.clear();
        arraylist.addAll(newData);

        // Notify the adapter that the data has changed
        notifyDataSetChanged();

    }

    public interface OnItemClickListener {
        void onItemClick();
    }

    private void vibrate(long milliseconds) {
        // Check if the device supports vibration
        if (vibrator != null && vibrator.hasVibrator()) {
            // Vibrate with the specified duration
            vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }
}
