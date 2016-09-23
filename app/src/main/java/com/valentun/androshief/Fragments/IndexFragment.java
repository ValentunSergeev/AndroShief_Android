package com.valentun.androshief.Fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.valentun.androshief.DTOs.RecipeDTO;
import com.valentun.androshief.R;
import com.valentun.androshief.adapter.RecipeAdapter;

import java.util.ArrayList;


public class IndexFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;

    private ArrayList<RecipeDTO> recipies = new ArrayList<>();

    private RecipeAdapter adapter = new RecipeAdapter(recipies);



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.index_fragment, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return view;
    }

    public void refreshData(ArrayList<RecipeDTO> data) {
        recipies.clear();
        recipies.addAll(data);
        adapter.notifyDataSetChanged();
    }

}
