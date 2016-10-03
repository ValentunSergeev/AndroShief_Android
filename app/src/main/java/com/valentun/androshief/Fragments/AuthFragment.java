package com.valentun.androshief.Fragments;

import android.app.Fragment;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.valentun.androshief.R;


public class AuthFragment extends Fragment implements View.OnClickListener {

    private OnAuthFragmentListener mListener;
    private View view;
    private AppCompatEditText inputEmail, inputPassword;
    private AppCompatButton register, sign_in;

    public interface OnAuthFragmentListener {

        void onRegisterButtonSelected(String email, String password);

        void onSignInButtonSelected(String email, String password);

    }

    public AuthFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_auth, container, false);

        mListener = (OnAuthFragmentListener) getActivity();

        inputEmail = (AppCompatEditText) view.findViewById(R.id.create_recipe_name);
        inputPassword = (AppCompatEditText) view.findViewById(R.id.create_recipe_description);

        register = (AppCompatButton) view.findViewById(R.id.create_submit);
        register.getBackground().setColorFilter(0xFFFF6D00, PorterDuff.Mode.MULTIPLY);
        register.setOnClickListener(this);

        sign_in = (AppCompatButton) view.findViewById(R.id.auth_log_in);
        sign_in.getBackground().setColorFilter(0xFF3F569B, PorterDuff.Mode.MULTIPLY);
        sign_in.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        switch (view.getId()) {
            case R.id.create_submit:
                mListener.onRegisterButtonSelected(email, password);
                break;
            case R.id.auth_log_in:
                mListener.onSignInButtonSelected(email, password);
                break;
        }
    }
}
