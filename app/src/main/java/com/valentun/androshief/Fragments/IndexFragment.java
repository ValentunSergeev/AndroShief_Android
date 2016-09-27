package com.valentun.androshief.Fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.valentun.androshief.DTOs.RecipeDTO;
import com.valentun.androshief.Helper;
import com.valentun.androshief.R;
import com.valentun.androshief.adapter.RecipeAdapter;

import java.util.ArrayList;


public class IndexFragment extends Fragment implements View.OnClickListener {

    private View view;
    private RecyclerView recyclerView;

    private ArrayList<RecipeDTO> recipies = new ArrayList<>();
    private RecipeAdapter adapter = new RecipeAdapter(recipies);
    private OnFabSelectedListener listener;

    private FloatingActionButton refreshFab, newFab;
    private CoordinatorLayout layout;

    public interface OnFabSelectedListener {
        void IndexFabSelected();

        void NewFabSelected();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.index_fragment, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        layout = (CoordinatorLayout) view.findViewById(R.id.index_container);

        refreshFab = (FloatingActionButton) view.findViewById(R.id.fab);
        refreshFab.setOnClickListener(this);

        newFab = (FloatingActionButton) view.findViewById(R.id.new_fab);
        newFab.setOnClickListener(this);

        listener = (OnFabSelectedListener) getActivity();

        adapter.setFragmentManager(getActivity().getFragmentManager());

        return view;
    }

    public void refreshData(ArrayList<RecipeDTO> data) {
        recipies.clear();
        recipies.addAll(data);
        adapter.notifyDataSetChanged();
    }

    public void addItemToData (RecipeDTO item) {
        recipies.add(item);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        if (!Helper.isOnline(this.getActivity())) {
            Snackbar.make(layout, getResources().getString(R.string.offline_text),
                    Snackbar.LENGTH_LONG).show();
        } else {
            switch (view.getId()) {
                case R.id.fab:
                    listener.IndexFabSelected();
                case R.id.new_fab:
                    listener.NewFabSelected();
            }
        }
    }
}
