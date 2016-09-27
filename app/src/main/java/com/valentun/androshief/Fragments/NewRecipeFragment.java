package com.valentun.androshief.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.valentun.androshief.R;


public class NewRecipeFragment extends Fragment implements View.OnClickListener {

    private OnCreateRecipeListener mListener;
    private View view;
    private AppCompatEditText editName, editDescription;
    private AppCompatButton createButton;

    public interface OnCreateRecipeListener {
        void onRecipeCreateSubmitted(String name, String description);
    }
    public NewRecipeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_new_recipe, container, false);

        editName = (AppCompatEditText) view.findViewById(R.id.create_recipe_name);
        editDescription = (AppCompatEditText) view.findViewById(R.id.create_recipe_description);

        createButton = (AppCompatButton) view.findViewById(R.id.create_submit);
        createButton.setOnClickListener(this);

        mListener = (OnCreateRecipeListener) getActivity();

        return view;
    }

    @Override
    public void onClick(View view) {
        String name = editName.getText().toString();
        String description = editDescription.getText().toString();
        mListener.onRecipeCreateSubmitted(name, description);
    }

}
