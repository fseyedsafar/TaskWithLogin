package com.example.task.controller;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.task.R;
import com.example.task.model.Task;
import com.example.task.repository.TaskRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditTaskFragment extends DialogFragment {


    public static final String TAG_DATE_PICKER_FRAGMENT = "TagDatePickerFragment";
    public static final int REQUEST_CODE_DATE_PICKER = 0;
    public static final String ARG_CURRENT_PAGE_EDIT_TEXT = "argCurrentPageEditText";
    public static final String ARG_ID_EDIT_TASK = "argIdEditTask";
    public static final String BOUNDLE_EDIT_TASK_FRAGMENT_DATE = "boundleEditTaskFragmentDate";
    private EditText mTitle;
    private EditText mDescription;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mState;
    private UUID id;
    private int currentPage;
    private Task task;
    private Date mDate;
    private String mTemp = "";
    private String mStateRadioButton;
    private RadioGroup mRadioGroup;
    private RadioButton getmRadioButtonTask;
    private RadioButton mRadioButtonToDo;
    private RadioButton mRadioButtonDoing;
    private RadioButton mRadioButtonDone;

    public EditTaskFragment() {
        // Required empty public constructor
    }

    public static EditTaskFragment newInstance(int currentPage, UUID id) {

        Bundle args = new Bundle();

        args.putSerializable(ARG_CURRENT_PAGE_EDIT_TEXT, currentPage);
        args.putSerializable(ARG_ID_EDIT_TASK, id);

        EditTaskFragment fragment = new EditTaskFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null){
            mTemp = (String) savedInstanceState.get(BOUNDLE_EDIT_TASK_FRAGMENT_DATE);
        }

        currentPage = (int) getArguments().getSerializable(ARG_CURRENT_PAGE_EDIT_TEXT);
        id = (UUID) getArguments().getSerializable(ARG_ID_EDIT_TASK);
        task = TaskRepository.getInstance().getTask(id, currentPage);

        mStateRadioButton = task.getmStateRadioButton();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(R.layout.fragment_detail_task, null , false);

        initUI(view);
//            mDateButton.setText(mTemp);
        setUI();

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(new Date());
                datePickerFragment.setTargetFragment(EditTaskFragment.this, REQUEST_CODE_DATE_PICKER);
                datePickerFragment.show(getFragmentManager(), TAG_DATE_PICKER_FRAGMENT);
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setNegativeButton(R.string.Edit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        initUpdateTask();
                    }
                })
                .setPositiveButton(android.R.string.cancel, null)
                .setNeutralButton(R.string.Delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TaskRepository.getInstance().delete(TaskRepository.getInstance().getTask(id, currentPage), currentPage);
                    }
                })
                .setView(view)
                .create();
    }

    private void initUpdateTask() {
        if (!(mStateRadioButton.equals(getRadioButtonChecked()))){
            task.setmStateRadioButton(getRadioButtonChecked());
            TaskRepository.getInstance().insert(task, getRadioButtonChecked(), currentPage);
            TaskRepository.getInstance().delete(task, currentPage);
        }
        if (mStateRadioButton.equals(getRadioButtonChecked())){
            task.setmTitle(mTitle.getText().toString());
            task.setmDescription(mDescription.getText().toString());
            task.setmDate(mDate);
            task.setmState(mState.isChecked());

            TaskRepository.getInstance().update(task, currentPage);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(BOUNDLE_EDIT_TASK_FRAGMENT_DATE, mDateButton.getText().toString());
    }

    private String getRadioButtonChecked(){
        String mStateRadioButton = "";
        int selectedItem = mRadioGroup.getCheckedRadioButtonId();
        getmRadioButtonTask = mRadioGroup.findViewById(selectedItem);

        if (selectedItem != -1) {
            mStateRadioButton = getmRadioButtonTask.getText().toString();
        }
        return mStateRadioButton;
    }

    private void initUI(View view) {
        mTitle = view.findViewById(R.id.title_editText);
        mDescription = view.findViewById(R.id.describtion_editText);
        mDateButton = view.findViewById(R.id.date_button);
        mTimeButton = view.findViewById(R.id.time_button);
        mRadioGroup = view.findViewById(R.id.radio_group);
        mRadioButtonToDo = view.findViewById(R.id.radioButton_ToDo);
        mRadioButtonDoing = view.findViewById(R.id.radioButton_Doing);
        mRadioButtonDone = view.findViewById(R.id.radioButton_Done);
        mState = view.findViewById(R.id.state_checkBox);
    }

    private void setUI(){
        mTitle.setText(task.getmTitle());
        mDescription.setText(task.getmDescription());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = simpleDateFormat.format(task.getmDate());
        mDateButton.setText(dateString);

        switch (mStateRadioButton){
            case "ToDo":{
                mRadioButtonToDo.setChecked(true);
                break;
            }
            case "Doing":{
                mRadioButtonDoing.setChecked(true);
                break;
            }
            case "Done":{
                mRadioButtonDone.setChecked(true);
                break;
            }
            default:
        }
        mState.setChecked(task.getmState());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK || data == null){
            return;
        }
        if (requestCode == REQUEST_CODE_DATE_PICKER){
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_TASK_DATE);

            mDate = date;

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = simpleDateFormat.format(date);
            mDateButton.setText(dateString);
        }
    }
}
