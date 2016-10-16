package com.valentun.androshief.Fragments;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.valentun.androshief.R;


public class SignInFragment extends Fragment implements View.OnClickListener {

    private OnSignInFragmentListener mListener;
    private View view;
    private AppCompatEditText inputEmail, inputPassword;
    private AppCompatButton signIn;

    public interface OnSignInFragmentListener {
        void onSignInButtonSelected(String email, String password);
    }

    public SignInFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        mListener = (OnSignInFragmentListener) getActivity();

        inputEmail = (AppCompatEditText) view.findViewById(R.id.sign_in_name);
        inputPassword = (AppCompatEditText) view.findViewById(R.id.sign_in_password);

        signIn = (AppCompatButton) view.findViewById(R.id.sign_in_submit);
        signIn.getBackground().setColorFilter(0xFF3F569B, PorterDuff.Mode.MULTIPLY);
        signIn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        mListener.onSignInButtonSelected(email, password);
    }
}
