package com.example.attendanceapp;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BottomDialogAddGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class BottomDialogAddGroupFragment extends BottomSheetDialogFragment {
    EditText Edtgroup;
    Button btn;
    String group;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BottomDialogAddGroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BottomDialogAddGroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BottomDialogAddGroupFragment newInstance(String param1, String param2) {
        BottomDialogAddGroupFragment fragment = new BottomDialogAddGroupFragment();
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
        View view=inflater.inflate(R.layout.fragment_bottom_dialog_add_group, container, false);


        Edtgroup=view.findViewById(R.id.Edit_text_group);
        btn=view.findViewById(R.id.add_group_button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               group=removeSpecialCharacters(Edtgroup.getText().toString().toUpperCase());
                int exist=checkGroupExists(group);
                if(group.isEmpty()){
                    Edtgroup.setError("Please Enter Group");
                }  else if (exist==1) {
                    Edtgroup.setError("Group Already Exixts");
                }
                else if(!group.isEmpty() && exist==0){
                    addNewGroup(group);
                    Toast.makeText(getContext(), group+" is Added succesfully", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }
        });
        // Inflate the layout for this fragment
        return view;    }

    private boolean checkAllowed(String group) {

        return true;
    }

    private int checkGroupExists(String group) {
        DataBaseHelper db=new DataBaseHelper(getContext());
        return db.checkGroupExists(group);
    }
    void addNewGroup(String group){
        DataBaseHelper db= new DataBaseHelper(getContext());
        db.addGroupToGroupTable(group,"0");
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity) getActivity();
            activity.getAdapter().addItemToGroupList();
            activity.onResume();
          //  activity.recyclerView.smoothScrollToPosition(activity.getAdapter().group_list.indexOf(group));
        }
    }
    private static String removeSpecialCharacters(String input) {
        // Use regular expression to replace characters that are not alphabets, spaces, or numbers
        return input.replaceAll("[^a-zA-Z0-9\\s]", "_");
    }
}