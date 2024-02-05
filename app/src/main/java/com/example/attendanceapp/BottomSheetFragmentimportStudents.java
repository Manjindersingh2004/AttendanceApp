package com.example.attendanceapp;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BottomSheetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BottomSheetFragmentimportStudents extends BottomSheetDialogFragment {
    RecyclerView recyclerView;
    ArrayList<String> group_list;
    TextView heading;

    BottomSheetFragmentimportStudents fg;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String FLAG = "flag";

    // TODO: Rename and change types of parameters
    private String flag;

    static AdapterGroupSelection adapterGroupSelection;


    public BottomSheetFragmentimportStudents() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param flag Parameter 1.
     * @return A new instance of fragment BottomSheetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BottomSheetFragmentimportStudents newInstance(String flag ) {
        BottomSheetFragmentimportStudents fragment = new BottomSheetFragmentimportStudents();
        fragment.fg=fragment;
        Bundle args = new Bundle();
        args.putString(FLAG, flag);
        fragment.setArguments(args);
        return fragment;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            flag = getArguments().getString(FLAG);
        }




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_bottom_sheet, container, false);
        heading=view.findViewById(R.id.heading_title_group_selection);
        setHeading();
        recyclerView=view.findViewById(R.id.recycler_select_group);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        addItemToGroupList();
        AdapterGroupSelectionImportStudents adapterGroupSelection=new AdapterGroupSelectionImportStudents(getContext(),group_list,flag,fg);
        recyclerView.setAdapter(adapterGroupSelection);
        // Inflate the layout for this fragment
        return view;
    }

    private void setHeading() {
        if(flag=="2"){
            // reset attendance
            heading.setText("Reset Attendance");

        } else if (flag=="1") {
            //take attendance
            heading.setText("Take Attendance");


        } else if (flag=="3") {
            //delete attendance
            heading.setText("Delete Attendance");


        } else if (flag=="4") {
            //retake attendance
            heading.setText("Modify Attendance");


        } else if (flag=="7") {
            //download attendance
            heading.setText("Export Attendance");

        } else if (String.valueOf(flag.charAt(0)).equals("8")) {
            // import Students
            heading.setText("Import Students");
        }
    }

    void addItemToGroupList(){
        DataBaseHelper db= new DataBaseHelper(getContext());
        group_list=db.fetchGroupTable();
        if(String.valueOf(flag.charAt(0)).equals("8")) {
            String key="";
            String[] parts = flag.split("-");
            if (parts.length == 2) {
                key = parts[1];
            }
            group_list.remove(key);
        }
    }
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (flag.equals("2"))
        {
            if (getActivity() instanceof MainActivity) {
                MainActivity activity = (MainActivity) getActivity();
                activity.onResume();
            }
        }
    }

}