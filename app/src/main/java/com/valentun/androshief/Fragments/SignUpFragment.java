package com.valentun.androshief.Fragments;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.valentun.androshief.R;


public class SignUpFragment extends Fragment implements View.OnClickListener {

    private OnSignUpFragmentListener mListener;
    private View view;
    private AppCompatEditText inputEmail, inputPassword;
    private AppCompatButton register;
    private ProgressBar progressBar;

    public interface OnSignUpFragmentListener {

        void onSignUpButtonSelected(String email, String password, SignUpFragment fragment);

    }

    public SignUpFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        mListener = (OnSignUpFragmentListener) getActivity();

        inputEmail = (AppCompatEditText) view.findViewById(R.id.sign_up_name);
        inputPassword = (AppCompatEditText) view.findViewById(R.id.sign_up_password);

        progressBar = (ProgressBar) view.findViewById(R.id.sign_up_progress_bar);

        register = (AppCompatButton) view.findViewById(R.id.sign_up_submit);
        register.getBackground().setColorFilter(0xFFFF6D00, PorterDuff.Mode.MULTIPLY);
        register.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

//        progressBar.setVisibility(View.VISIBLE);
        register.setEnabled(false);

        mListener.onSignUpButtonSelected(email, password, this);
    }

    public void stopSignUp() {
//        progressBar.setVisibility(View.GONE);
        register.setEnabled(true);
    }
}
