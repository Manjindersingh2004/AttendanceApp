package com.example.attendanceapp;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BottomDialogRenameGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

/*



 */
public class BottomDialogRenameGroupFragment extends BottomSheetDialogFragment {
//    AppCompatSpinner spinner;
    EditText Edtgroup;
    Button btn;
    String group,newGroup;
    ArrayList<String> arrayList=new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BottomDialogRenameGroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BottomDialogRenameGroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BottomDialogRenameGroupFragment newInstance(String param1) {
        BottomDialogRenameGroupFragment fragment = new BottomDialogRenameGroupFragment();
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
        View view=inflater.inflate(R.layout.fragment_bottom_dialog_rename_group, container, false);

        Edtgroup=view.findViewById(R.id.Edit_text_rename_group);
        btn=view.findViewById(R.id.rename_group_button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newGroup=Edtgroup.getText().toString();
                int exist=checkGroupExists(newGroup);
                if(newGroup.isEmpty()){
                    Edtgroup.setError("Please Enter Group");
                }
                else if (exist==1) {
                    Edtgroup.setError(" Already Exists");
                }
                else if(!newGroup.isEmpty() && exist==0){
                    renameGroup(group,newGroup);
                    Toast.makeText(getContext(), group+" Renamed Sucessfully", Toast.LENGTH_SHORT).show();


                    dismiss();
                }
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    void renameGroup(String key,String group){
        DataBaseHelper db= new DataBaseHelper(getContext());
        db.renameGroup(key,group);



        new DataBaseHelper(getContext()).renameGroupAttendnceInFireBase(key,group);
    }
    private int checkGroupExists(String group) {
        DataBaseHelper db=new DataBaseHelper(getContext());
        return db.checkGroupExists(group);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        if (getActivity() instanceof MainActivity) {
            MainActivity activity=(MainActivity) getActivity();
            activity.getAdapter().addItemToGroupList();
        }
    }
}