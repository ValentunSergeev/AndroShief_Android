package com.valentun.androshief.Adapters;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.valentun.androshief.DTOs.RecipeDTO;
import com.valentun.androshief.Fragments.ShowFragment;
import com.valentun.androshief.R;

import java.util.ArrayList;


public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private ArrayList<RecipeDTO> data;

    private FragmentManager fragmentManager;

    private ShowFragment showFragment;
    private static final int VIEW_TYPE_EMPTY_LIST_PLACEHOLDER = 0;
    private static final int VIEW_TYPE_OBJECT_VIEW = 1;
    public RecipeAdapter(ArrayList<RecipeDTO> data) {
        this.data = data;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        if (data.isEmpty()) {
            return VIEW_TYPE_EMPTY_LIST_PLACEHOLDER;
        } else {
            return VIEW_TYPE_OBJECT_VIEW;
        }
    }

    @Override
    public void onBindViewHolder(final RecipeViewHolder holder, int position) {
        RecipeDTO item = data.get(position);
        holder.name.setText(item.getName());
        holder.description.setText(item.getDescription());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecipeDTO item = data.get(holder.getAdapterPosition());
                showFragment = ShowFragment.newInstance(item.getName(), item.getDescription());
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, showFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }


    public static class RecipeViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView name;
        TextView description;

        public RecipeViewHolder(final View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            name = (TextView) itemView.findViewById(R.id.name);
            description = (TextView) itemView.findViewById(R.id.description);
        }
    }
}
