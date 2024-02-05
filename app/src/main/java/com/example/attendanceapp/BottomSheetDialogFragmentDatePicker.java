package com.example.attendanceapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BottomSheetDialogFragmentDatePicker#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BottomSheetDialogFragmentDatePicker extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final String ARG_GROUP = "param2";
    private static final String ARG_MODE = "MODE";

    // TODO: Rename and change types of parameters
    private String date="";
    private String group;
    private String mode;
    int validity=1;

    public BottomSheetDialogFragmentDatePicker() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment BottomSheetDialogFragmentDatePicker.
     */
    // TODO: Rename and change types and number of parameters
    public static BottomSheetDialogFragmentDatePicker newInstance(String group,String mode) {
        BottomSheetDialogFragmentDatePicker fragment = new BottomSheetDialogFragmentDatePicker();
        Bundle args = new Bundle();
        args.putString(ARG_GROUP, group);
        args.putString(ARG_MODE, mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            group = getArguments().getString(ARG_GROUP);
            mode = getArguments().getString(ARG_MODE);
        }
    }

    Button nextBtn;
    int Year, Day, Month;
    ArrayList<StudentDataModel> arrayList=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_bottom_sheet_dialog_date_picker, container, false);

        DatePicker datePicker = view.findViewById(R.id.date_pikker_fragment);
        nextBtn=view.findViewById(R.id.next_button_date_picker);
        Day =datePicker.getDayOfMonth();
        Month =datePicker.getMonth()+1;
        Year =datePicker.getYear();
        date= String.format("%04d-%02d-%02d", Year, Month, Day);
        //Toast.makeText(getContext(), date, Toast.LENGTH_SHORT).show();
        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date=String.format("%04d-%02d-%02d", year, monthOfYear+1, dayOfMonth);
                //Toast.makeText(getContext(), date, Toast.LENGTH_SHORT).show();
                if(monthOfYear> Month || year>Year ||(monthOfYear+1==Month&& dayOfMonth>Day))
                    validity=0;
                else
                    validity=1;
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mode.equals("take_attendance")){
                    onclickBtnForTakeAttendance();
                } else if (mode.equals("modify_attendance")) {

                } else if (mode.equals("delete_attendance")) {
                    onclickBtnForDeleteAttendance();
                } else if (mode.equals("retake_attendance")) {
                    onClickBtnForRetakeAttendance();
                } else if (mode.equals("filter_attendance")) {
                    onClickBtnForFilter();
                }

            }
        });
        // Inflate the layout for this fragment
        return view;    }

    private void onClickBtnForFilter() {
        int flag=checkDateExixst(date,group);
        if(date.length()>0 && flag==1 && validity==1){
            if (getActivity() instanceof ViewAttendanceInDetail) {
                ViewAttendanceInDetail activity = (ViewAttendanceInDetail) getActivity();
                activity.date(date);
            }
            dismiss();
        } else if (flag==0) {
            Toast.makeText(getContext(), "Attendance Not Exists", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getContext(), "Enter Valid Date", Toast.LENGTH_SHORT).show();
        }
    }

    private void onClickBtnForRetakeAttendance() {

        int flag=checkDateExixst(date,group);
        if(date.length()>0 && flag==1 && validity==1){
            Intent i=new Intent(getContext(), ViewAttendanceActivity.class);
            i.putExtra("key",group);
            i.putExtra("flag","2");
            i.putExtra("date",date);
            getContext().startActivity(i);
            dismiss();
        } else if (flag==0) {
            Toast.makeText(getContext(), "Attendance Not Exists", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getContext(), "Enter Valid Date", Toast.LENGTH_SHORT).show();
        }
    }

    int checkDateExixst(String date,String group) {
        DataBaseHelper db=new DataBaseHelper(getContext());
        return db.checkDateExist(date,group);
    }

    void onclickBtnForTakeAttendance(){
        int flag=checkDateExixst(date,group);
        if(date.length()>0 && flag==0 && validity==1){
            Intent i=new Intent(getContext(), ViewAttendanceActivity.class);
            i.putExtra("key",group);
            i.putExtra("flag","1");
            i.putExtra("date",date);
            getContext().startActivity(i);
            dismiss();
        } else if (flag==1) {
            Toast.makeText(getContext(), "Attendance Already Taken", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getContext(), "Enter Valid Date", Toast.LENGTH_SHORT).show();
        }
    }

    void onclickBtnForDeleteAttendance(){
       if(NetworkUtils.isNetworkAvailable(getContext())){
           int flag=checkDateExixst(date,group);
           if(date.length()>0 && flag==1 && validity==1){
               //here is 5 lines data to toast
               new DataBaseHelper(getContext()).removeAttendanceDateIntoFirebase(group.toUpperCase(), date.trim(), new DataBaseHelper.OnAttendanceDelete() {
                   @Override
                   public void onAttendanceDelete() {
                       DataBaseHelper db=new DataBaseHelper(getContext());
                       arrayList=db.fetchGroupData(group);
                       ArrayList<String> attendance=db.getAttendanceListFromAttendanceTableByDate(date,group,arrayList);
                       db.decrementAttendanceStudentTable(arrayList,attendance);
                       db.deleteRowByDateInAttendanceTable(date,group);
                       Toast.makeText(getContext(), "Attendance Deleted", Toast.LENGTH_SHORT).show();
                       new DataBaseHelper(getContext()).insertAttendanceOnline(group.toUpperCase());
                   }
               });

               dismiss();
           } else if (flag==0) {
               Toast.makeText(getContext(), "Attendance Not Exists", Toast.LENGTH_SHORT).show();
           }
           else{
               Toast.makeText(getContext(), "Enter Valid Date", Toast.LENGTH_SHORT).show();
           }
       }else {
           Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
       }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (mode.equals("delete_attendance"))
        {
            if (getActivity() instanceof MainActivity) {
                MainActivity activity = (MainActivity) getActivity();
                activity.onResume();
            }
        }
    }
}