package com.example.attendanceapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TableLayout;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

//recycler_select_share_mode
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BottomSheetDialogFragmentShareFile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BottomSheetDialogFragmentShareFile extends BottomSheetDialogFragment {


    RecyclerView recyclerView;
    ArrayList<String> share_list=new ArrayList<>();
    String filename;
    BottomSheetDialogFragmentShareFile fg;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BottomSheetDialogFragmentShareFile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BottomSheetDialogFragmentShareFile.
     */
    // TODO: Rename and change types and number of parameters
    public static BottomSheetDialogFragmentShareFile newInstance(String param1) {
        BottomSheetDialogFragmentShareFile fragment = new BottomSheetDialogFragmentShareFile();
        fragment.fg=fragment;
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            filename = getArguments().getString(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_bottom_sheet_dialog_share_file, container, false);

        recyclerView=view.findViewById(R.id.recycler_select_share_mode);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        share_list.add("Image File");
        share_list.add("Excel File");
        TableLayout tableLayout=null;
        ProgressBar progressBar=null;
        if (getActivity() instanceof ViewAttendanceInDetail) {
            ViewAttendanceInDetail activity = (ViewAttendanceInDetail) getActivity();
            tableLayout=activity.getTableLayout();
            progressBar=activity.getProgressBar();
        }
        AdapterShareModeSelection adapterGroupSelection=new AdapterShareModeSelection(getContext(),share_list,tableLayout,filename,fg,progressBar);
        recyclerView.setAdapter(adapterGroupSelection);
        // Inflate the layout for this fragment
        return view;
    }
}