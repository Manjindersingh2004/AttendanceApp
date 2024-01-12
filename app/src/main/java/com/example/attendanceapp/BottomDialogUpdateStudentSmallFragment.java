package com.example.attendanceapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BottomDialogUpdateStudentSmallFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BottomDialogUpdateStudentSmallFragment extends BottomSheetDialogFragment {
    EditText Edtrollno;
    Button btn;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BottomDialogUpdateStudentSmallFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BottomDialogUpdateStudentSmallFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BottomDialogUpdateStudentSmallFragment newInstance(String param1, String param2) {
        BottomDialogUpdateStudentSmallFragment fragment = new BottomDialogUpdateStudentSmallFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_bottom_dialog_update_student_small, container, false);

        // Inflate the layout for this fragment
        Edtrollno=view.findViewById(R.id.Edit_text_Rollno_update);
        btn=view.findViewById(R.id.continue_update_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rollno=Edtrollno.getText().toString();
                ArrayList<StudentDataModel> arrayList=new ArrayList<>();
                DataBaseHelper db=new DataBaseHelper(getContext());
                if(!rollno.isEmpty())
                arrayList=db.fetchOneRow(rollno);
                if (rollno.isEmpty()) {
                    Edtrollno.setError("Cannot be empty");

                } else if(arrayList.isEmpty()){
                    Edtrollno.setError("Invalid Roll no");
                }
                else if(!rollno.isEmpty() && !arrayList.isEmpty()){

                    BottomDialogUpdateStudentFragment fg=BottomDialogUpdateStudentFragment.newInstance(rollno,arrayList.get(0).ROLL_NO,arrayList.get(0).NAME,arrayList.get(0).GROUP);
                    fg.show(getActivity().getSupportFragmentManager(), fg.getTag());
                    dismiss();
                }
            }
        });
        return view;
    }

    void removeStudentFromTable(String rollno){
        DataBaseHelper db= new DataBaseHelper(getContext());
        db.removeStudent(rollno);
    }
    private int checkRollnoExists(String rollno) {
        DataBaseHelper db =new DataBaseHelper(getContext());
        return db.checkRollnoExists(rollno);
    }
}