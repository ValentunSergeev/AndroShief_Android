package com.valentun.androshief.Fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.valentun.androshief.R;


public class ShowFragment extends Fragment {

    private TextView name, description;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_show, container, false);

        name = (TextView) view.findViewById(R.id.name_show);
        description = (TextView) view.findViewById(R.id.description_show);

        name.setText(getArguments().getString("name"));
        description.setText(getArguments().getString("description"));

        return view;
    }

    public static ShowFragment newInstance(String name, String description) {
        ShowFragment showFragment = new ShowFragment();

        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("description", description);
        showFragment.setArguments(args);

        return showFragment;
    }
}
