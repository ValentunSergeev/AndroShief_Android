package com.valentun.androshief.Fragments;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.valentun.androshief.Adapters.RecipeAdapter;
import com.valentun.androshief.DTOs.RecipeDTO;
import com.valentun.androshief.Helper;
import com.valentun.androshief.R;

import java.util.ArrayList;


public class IndexFragment extends Fragment implements View.OnClickListener {

    private View view;
    private RecyclerView recyclerView;

    private ArrayList<RecipeDTO> recipies = new ArrayList<>();
    private DisplayMetrics dm = new DisplayMetrics();
    private RecipeAdapter adapter = new RecipeAdapter(recipies, dm);
    private OnIndexFragmentActionListener listener;

    private FloatingActionButton newFab;
    private SwipeRefreshLayout layout;

    public interface OnIndexFragmentActionListener {
        void NewFabSelected();

        void OnRefreshed();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();


        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        adapter.setDm(dm);

        view = inflater.inflate(R.layout.index_fragment, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        listener = (OnIndexFragmentActionListener) getActivity();

        layout = (SwipeRefreshLayout) view.findViewById(R.id.index_refresh);

        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listener.OnRefreshed();
            }
        });

        newFab = (FloatingActionButton) view.findViewById(R.id.new_fab);
        newFab.setOnClickListener(this);

        adapter.setFragmentManager(getActivity().getFragmentManager());

        return view;
    }

    public void refreshData(ArrayList<RecipeDTO> data) {
        recipies.clear();
        recipies.addAll(data);
        adapter.notifyDataSetChanged();
    }

    public void stopRefreshing() {
        layout.setRefreshing(false);
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
            listener.NewFabSelected();
        }
    }
}
