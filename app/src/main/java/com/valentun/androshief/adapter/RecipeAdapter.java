package com.valentun.androshief.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.valentun.androshief.DTOs.RecipeDTO;
import com.valentun.androshief.R;

import java.util.ArrayList;


public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>{

    private ArrayList<RecipeDTO> data;
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
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        RecipeDTO item = data.get(position);
        holder.name.setText(item.getName());
        holder.description.setText(item.getDescription());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateData(ArrayList<RecipeDTO> viewModels) {
        data.clear();
        data.addAll(viewModels);
        notifyDataSetChanged();
    }


    public static class RecipeViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView name;
        TextView description;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            name = (TextView) itemView.findViewById(R.id.name);
            description = (TextView) itemView.findViewById(R.id.description);
        }
    }
}
