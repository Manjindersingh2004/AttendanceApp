package com.example.attendanceapp;

import android.content.Context;
import android.graphics.Color;
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


        holder.attandanceCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(mode.equals("simple")){

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
                   removeStudentFromTable(rollno);
                   arraylist.remove(pos);
                   notifyItemRemoved(pos);
                   if(listener!=null){
                       listener.onItemClick();
                   }
                   return true;
               } else if (item.getItemId()==id2) {
                   BottomDialogUpdateStudentFragment fg=BottomDialogUpdateStudentFragment.newInstance(rollno,arraylist.get(0).ROLL_NO,arraylist.get(0).NAME,arraylist.get(0).GROUP);
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

    void removeStudentFromTable(String rollno){
        new DataBaseHelper(context).removeStudent(rollno);
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
}
