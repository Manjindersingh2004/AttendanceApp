package com.example.attendanceapp;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
 * Use the {@link BottomDialogUpdateStudentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BottomDialogUpdateStudentFragment extends BottomSheetDialogFragment {
    EditText Edtrollno,Edtname;
    ArrayList<String> group_list =new ArrayList<>();
    Button btn;
    String rollno,name,group;
    private String key,EXrollno,EXname,EXgroup;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_key = "key";
    private static final String ARG_EXrollno = "EXrollno";
    private static final String ARG_Exname = "Exname";
    private static final String ARG_EXgroup = "EXgroup";



    // TODO: Rename and change types of parameters

    public BottomDialogUpdateStudentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BottomDialogUpdateStudentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BottomDialogUpdateStudentFragment newInstance(String key,String EXrollno,String EXname,String EXgroup) {
        BottomDialogUpdateStudentFragment fragment = new BottomDialogUpdateStudentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_key,key );
        args.putString(ARG_EXrollno,EXrollno );
        args.putString(ARG_Exname,EXname);
        args.putString(ARG_EXgroup,EXgroup );
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            key = getArguments().getString(ARG_key);
            EXrollno = getArguments().getString(ARG_EXrollno);
            EXname = getArguments().getString(ARG_Exname);
            EXgroup = getArguments().getString(ARG_EXgroup);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_bottom_dialog_update_student, container, false);
        // Inflate the layout for this fragment
        Edtrollno=view.findViewById(R.id.update_roll_number);
        Edtname=view.findViewById(R.id.update_name);

        btn=view.findViewById(R.id.update_btn2);

        Edtrollno.setText(EXrollno);
        Edtname.setText(EXname);

        group=EXgroup;

        addItemToArrayList();
//        ArrayAdapter arrayAdapter=new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,group_list);
//        spinner.setAdapter(arrayAdapter);
//
//        spinner.setSelection(findIndexOfArray(EXgroup));
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                group=group_list.get(position);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                group="";
//            }
//        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rollno=Edtrollno.getText().toString();
                name=Edtname.getText().toString();
                int exist=0;
                if(!rollno.isEmpty() && !rollno.equals(key))
                    exist=checkRollnoExists(rollno,group);
                if(name.isEmpty()){
                    Edtname.setError("cannot be empty");
                }
                if(rollno.isEmpty()){
                    Edtrollno.setError("cannot be empty");
                }
                if(exist==1){
                    Edtrollno.setError("Roll no Already Exist");
                }
                else if(exist==0 && !name.isEmpty() && !rollno.isEmpty()){
                    updateDataInTable(key,rollno,name,group);
                    dismiss();
                }

            }
        });

        return view;    }

    void updateDataInTable(String key, String rollno, String name, String group) {
        DataBaseHelper db=new DataBaseHelper(getContext());
        db.updateStudentData(key,rollno,name,group,"0","0");
        Toast.makeText(getContext(), "Data of "+key+" updated successfully", Toast.LENGTH_SHORT).show();
    }
    int findIndexOfArray(String group) {

        for (int i = 0; i < group_list.size(); i++) {
            if (group_list.get(i).equals(group)) {
                return i;
            }
        }

        return 0;
    }
    void addItemToArrayList(){
        DataBaseHelper db= new DataBaseHelper(getContext());
        group_list =db.fetchGroupTable();
    }
    private int checkRollnoExists(String rollno,String group) {
        DataBaseHelper db =new DataBaseHelper(getContext());
        return db.checkRollnoExists(rollno,group);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (getActivity() instanceof ViewAttendanceActivity) {
            ViewAttendanceActivity activity = (ViewAttendanceActivity) getActivity();
            activity.getAdapter().putValuesInArrayList();
            activity.onResume();
        }
    }
}