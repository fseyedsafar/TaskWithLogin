package com.example.task.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.task.R;
import com.example.task.model.Person;
import com.example.task.repository.PersonRepository;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    public static final String TAG_SIGN_UP_FRAGMENT = "tagSignUpFragment";
    public static final int REQUEST_CODE_LOG_IN_FRAGMENT = 0;
    public static final String EXTRA_LOGIN_FRAGMENT_ID = "extraLoginFragmentId";
    private EditText mUserEditText;
    private EditText mPassEditText;
    private Button mButtonLogIn;
    private Button mButtonSignUp;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        
        Bundle args = new Bundle();
        
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        init(view);

        mButtonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUserEditText.getText().toString().equals("") || mPassEditText.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "please sign up", Toast.LENGTH_SHORT).show();
                }
                else {
                    int result;
                    result = PersonRepository.getInstance().search(mUserEditText.getText().toString(), mPassEditText.getText().toString());
                    switch (result) {
                        case 1: {
                            Person person = PersonRepository.getInstance().getPerson(mUserEditText.getText().toString(), mPassEditText.getText().toString());
                            UUID id = person.getmID();

                            Intent intent = TaskPagerActivity.newIntent(getActivity());
                            intent.putExtra(EXTRA_LOGIN_FRAGMENT_ID, id);
                            startActivity(intent);
                            break;
                        }
                        case 2: {
                            Toast.makeText(getActivity(), "Your Password Is Error", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        case 3: {
                            Toast.makeText(getActivity(), "Your User Is Error", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        default: {
                            Toast.makeText(getActivity(), "Please Sign Up", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }
            }
        });

        mButtonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpFragment signUpFragment = SignUpFragment.newInstance(mUserEditText.getText().toString(), mPassEditText.getText().toString());
                signUpFragment.setTargetFragment(LoginFragment.this, REQUEST_CODE_LOG_IN_FRAGMENT);
                signUpFragment.show(getFragmentManager(), TAG_SIGN_UP_FRAGMENT);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK || data == null){
            return;
        }
        if (requestCode == REQUEST_CODE_LOG_IN_FRAGMENT){
            mUserEditText.setText((String) data.getSerializableExtra(SignUpFragment.EXTRA_SIGN_UP_FRAGMENT_USER));
            mPassEditText.setText((String) data.getSerializableExtra(SignUpFragment.EXTRA_SIGN_UP_FRAGMENT_PASS));
        }
    }

    private void init(View view) {
        mUserEditText = view.findViewById(R.id.textUser_login_fragment);
        mPassEditText = view.findViewById(R.id.textPass_login_fragment);
        mButtonLogIn = view.findViewById(R.id.buttonLogin_login_fragment);
        mButtonSignUp = view.findViewById(R.id.buttonSignUp_login_fragment);
    }

}
