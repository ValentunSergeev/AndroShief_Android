package com.valentun.androshief.Adapters;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.DisplayMetrics;
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

    private DisplayMetrics dm;

    private ShowFragment showFragment;

    public RecipeAdapter(ArrayList<RecipeDTO> data, DisplayMetrics dm) {
        this.data = data;
        this.dm = dm;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecipeViewHolder holder, int position) {
        RecipeDTO item = data.get(position);
        holder.name.setText(item.getName());
        holder.description.setText(item.getDescription());

        String Base64Image = item.getMainPhoto().substring(23);

        byte[] decodedBytes = Base64.decode(Base64Image, 0);
        Bitmap image = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

        double coefficient = (dm.widthPixels - 24 * dm.density)/image.getWidth();

        Bitmap resized = Bitmap.createScaledBitmap(image, (int) (image.getWidth() * coefficient) + 1,
                                                          (int) (image.getHeight() * coefficient) + 1, true);
        holder.imageView.setImageBitmap(resized);


        holder.fab.setOnClickListener(new View.OnClickListener() {
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

    public void setDm(DisplayMetrics dm) {
        this.dm = dm;
    }


    public static class RecipeViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView name;
        TextView description;
        FloatingActionButton fab;
        AppCompatImageView imageView;

        public RecipeViewHolder(final View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            name = (TextView) itemView.findViewById(R.id.name);
            fab = (FloatingActionButton) itemView.findViewById(R.id.item_fab);
            description = (TextView) itemView.findViewById(R.id.description);
            imageView = (AppCompatImageView) itemView.findViewById(R.id.item_main_photo);
        }
    }
}
