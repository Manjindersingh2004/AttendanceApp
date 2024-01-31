package com.example.attendanceapp;


import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BottomDialogAddStudentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BottomDialogAddStudentFragment extends BottomSheetDialogFragment {
    EditText Edtname,Edtrollno;
    Button addStudent;

    String group;
    ArrayList<String> arrayList=new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BottomDialogAddStudentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BottomDialogAddStudentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BottomDialogAddStudentFragment newInstance(String param1) {
        BottomDialogAddStudentFragment fragment = new BottomDialogAddStudentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            group = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_bottom_dialog_add_student, container, false);

        Edtname=view.findViewById(R.id.Edit_Text_name);
        Edtrollno=view.findViewById(R.id.Edit_text_Rollno_update);
        addStudent=view.findViewById(R.id.add_student_button);


        addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(NetworkUtils.isNetworkAvailable(getContext())){
                   String name,rollno;
                   int exists=0;
                   name=Edtname.getText().toString();
                   rollno=Edtrollno.getText().toString();
                   if(!rollno.isEmpty())
                       exists=checkRollnoExists(rollno);
                   if(name.isEmpty())
                   {
                       Edtname.setError("Name cannot be empty");
                   }
                   if(rollno.isEmpty()){
                       Edtrollno.setError("Roll no cannot be empty");
                   }
                   if(exists==1){
                       Edtrollno.setError("Roll number Already Exists");
                   }
                   else if(!name.isEmpty() && !rollno.isEmpty() && exists==0){
                       addStudentDataToTable(rollno,name,group);
                       dismiss();
                   }
               }
               else{
                   Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
               }
            }
        });
        // Inflate the layout for this fragment
        return view;    }

    void addStudentDataToTable(String rollno,String name,String group){
           DataBaseHelper db=new DataBaseHelper(getContext());
           String atendlec,totallec,percentage;
           atendlec="0";
           totallec="0";
           percentage="0";
           db.addNewStudents(rollno,name,group,atendlec,totallec,percentage);
           Toast.makeText(getContext(), name+" is sucessfully added", Toast.LENGTH_SHORT).show();


           new DataBaseHelper(getContext()).addStudentAttendanceFireBase(rollno,name,group);

    }

    private int checkRollnoExists(String rollno) {
        DataBaseHelper db =new DataBaseHelper(getContext());
        return db.checkRollnoExists(rollno,group);
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (getActivity() instanceof ViewAttendanceActivity) {
            ViewAttendanceActivity activity = (ViewAttendanceActivity) getActivity();
            activity.getAdapter().putValuesInArrayList();
            activity.onResume();
        }
    }

}