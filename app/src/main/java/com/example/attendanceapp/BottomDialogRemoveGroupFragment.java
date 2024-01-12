package com.example.attendanceapp;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BottomDialogRemoveGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

/*



 */
public class BottomDialogRemoveGroupFragment extends BottomSheetDialogFragment {
    AppCompatSpinner spinner;
    Button btn;
    String group;
    ArrayList<String> arrayList=new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BottomDialogRemoveGroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BottomDialogRemoveGroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BottomDialogRemoveGroupFragment newInstance(String param1, String param2) {
        BottomDialogRemoveGroupFragment fragment = new BottomDialogRemoveGroupFragment();
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
        View view=inflater.inflate(R.layout.fragment_bottom_dialog_remove_group, container, false);

        spinner=view.findViewById(R.id.remove_group_spinner);
        btn=view.findViewById(R.id.remove_group_button);
        addItemToGroupList();
        ArrayAdapter adapter= new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,arrayList);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                group=arrayList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                group=arrayList.get(0);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeGroupFromTable(group);
                dismiss();
                Toast.makeText(getContext(), group+" Removed Sucessfully", Toast.LENGTH_SHORT).show();
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
    void removeGroupFromTable(String group){
        DataBaseHelper db= new DataBaseHelper(getContext());
        db.removeGroup(group);
    }
    void addItemToGroupList(){
        DataBaseHelper db= new DataBaseHelper(getContext());
        arrayList=db.fetchGroupTable();
    }
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (getActivity() instanceof ManageDataActivity) {
            ManageDataActivity activity = (ManageDataActivity) getActivity();
            activity.updateManageScreen();
        }
    }
}