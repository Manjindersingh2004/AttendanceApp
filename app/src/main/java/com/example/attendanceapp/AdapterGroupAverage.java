package com.example.attendanceapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;

public class AdapterGroupAverage extends RecyclerView.Adapter<AdapterGroupAverage.ViewHolder> {


    Context context;
    ArrayList<String> group_list = new ArrayList<>();
    FragmentManager fm;
    OnItemClickListener listener;

    AdapterGroupAverage(Context context, ArrayList<String> group_list,FragmentManager fm,OnItemClickListener listener ) {
        this.context = context;
        this.group_list = group_list;
        this.fm=fm;
        this.listener=listener;
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

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context, ViewAttendanceActivity.class);
                i.putExtra("key",group_list.get(holder.getAdapterPosition()));
                i.putExtra("flag","0");
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });

        holder.item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPopupMenu(v, holder.getAdapterPosition());

                return false;
            }
        });


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
        CardView item;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            group=itemView.findViewById(R.id.group_progress_name);
            per=itemView.findViewById(R.id.percentage_group_progress);
            progressBar=itemView.findViewById(R.id.progress_bar);
            students=itemView.findViewById(R.id.group_progress_total_students);
            lectures=itemView.findViewById(R.id.group_progress_total_lecture);
            item=itemView.findViewById(R.id.group_progress_card);
        }
    }

    private void showPopupMenu(View view,int pos) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.delete_update_menu, popupMenu.getMenu());
        int id1=R.id.delete_item_menu,id2=R.id.update_item_menu;

        popupMenu.getMenu().getItem(0).setTitle("Delete Group");
        popupMenu.getMenu().getItem(1).setTitle("Rename Group");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getItemId()==id1){
                   removeGroupFromTable(group_list.get(pos));
                    group_list.remove(pos);
                    notifyItemRemoved(pos);
                    if (listener != null) {
                        listener.onItemClick();
                    }

                    return true;
                } else if (item.getItemId()==id2) {
                    BottomDialogRenameGroupFragment fg=BottomDialogRenameGroupFragment.newInstance(group_list.get(pos));
                    fg.show(fm,fg.getTag());
                    return true;
                }
                else{
                    return true;
                }
            }
        });

        popupMenu.show();
    }

    void removeGroupFromTable(String group){
        DataBaseHelper db= new DataBaseHelper(context);
        db.removeGroup(group);
    }

    void addItemToGroupList(){
        group_list=new ArrayList<>();
        DataBaseHelper db= new DataBaseHelper(context);
        group_list=db.fetchGroupTable();
        notifyDataSetChanged();
    }


    public interface OnItemClickListener {
        void onItemClick();
    }

}
