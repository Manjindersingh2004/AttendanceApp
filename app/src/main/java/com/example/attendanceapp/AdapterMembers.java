package com.example.attendanceapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdapterMembers extends RecyclerView.Adapter<AdapterMembers.ViewHolder> {

    Context context;
    ArrayList<VerificationDataModel> arrayList=new ArrayList<>();
    String VERIFICATION="VERIFICATION";
    String COLLAGES="COLLAGES";
    ProgressBar progressBar;
    LinearLayout nothing;
    String name,email,phone,verify;

    public AdapterMembers(Context context, ProgressBar progressBar, LinearLayout nothing) {
        this.context = context;
        this.progressBar=progressBar;
        this.nothing=nothing;

        putItemIntoArraylist();
    }

    @NonNull
    @Override
    public AdapterMembers.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.member_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterMembers.ViewHolder holder, int position) {
        name=arrayList.get(position).Name;
        email=arrayList.get(position).Email;
        phone=arrayList.get(position).Mobile;
        verify=arrayList.get(position).Verified;

        holder.name.setText("Name  : "+name);
        holder.email.setText(email);
        holder.phone.setText("Mobile: "+phone);
        if(verify.equals("TRUE")){
            holder.verify.setText("Verified");
            holder.verify.setTextColor(context.getResources().getColor(R.color.green_));
            holder.tick.setBackgroundDrawable(context.getDrawable(R.drawable.check));
        }
        else{
            holder.verify.setText("Not Verified");
            holder.verify.setTextColor(context.getResources().getColor(R.color.red_));
            holder.tick.setBackgroundDrawable(context.getDrawable(R.drawable.uncheck));
        }

        holder.item.setOnClickListener(v -> {
            showDialog(holder.getAdapterPosition());
        });


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView name,email,phone,verify;
        AppCompatButton tick;
        CardView item;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name_recycler_view_member);
            email=itemView.findViewById(R.id.email_recycler_view_member);
            phone=itemView.findViewById(R.id.phone_recycler_view_member);
            verify=itemView.findViewById(R.id.verifiedMemberTxtItem);
            tick=itemView.findViewById(R.id.recycler_view_attendance_button_tick_member);
            item=itemView.findViewById(R.id.recycler_view_member_card);
        }
    }

    void putItemIntoArraylist(){
        FirebaseDatabase.getInstance().getReference().child(VERIFICATION).child(COLLAGES).child(TeacherDataStatic.CollageId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot childSnapshot : snapshot.getChildren()) {
                        VerificationDataModel dataModel = childSnapshot.getValue(VerificationDataModel.class);
                        arrayList.add(dataModel);
                    }
                    notifyDataSetChanged();
                    Toast.makeText(context, "Found", Toast.LENGTH_SHORT).show();
                }
                else{
                    nothing.setVisibility(View.VISIBLE);
                    Toast.makeText(context, "not Found", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    void showDialog(int position){
        String tittle,message,action,name,email,phone,verify;
        name=arrayList.get(position).Name;
        email=arrayList.get(position).Email;
        phone=arrayList.get(position).Mobile;
        verify=arrayList.get(position).Verified;
        tittle="Verification";
        if(verify.equals("TRUE")){
            action="Unverify";
        }else{
            action="Verify";
        }
        message="Do you want to "+action+" "+name+" ?";
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder
                .setTitle(tittle)
                .setMessage(message)
                .setPositiveButton(action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        verifyMember(position,email,verify);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    void verifyMember(int position, String email, String verify) {
        if(verify.equals("TRUE")){
            verify="FALSE";
        }
        else{
            verify="TRUE";
        }
        arrayList.get(position).Verified=verify;
        notifyDataSetChanged();
        FirebaseDatabase.getInstance().getReference().child(VERIFICATION).child(COLLAGES).child(TeacherDataStatic.CollageId).child(removeSpecialCharacters(email)).child("verified").setValue(verify);
    }

    public String removeSpecialCharacters(String str) {
        // Using regex to replace all characters except letters, digits, @, and .
        return str.replaceAll("[^a-zA-Z0-9]", "");
    }


}
