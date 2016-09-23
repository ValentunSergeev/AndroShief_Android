package com.valentun.androshief;

import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.valentun.androshief.DTOs.RecipeDTO;
import com.valentun.androshief.Fragments.IndexFragment;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    AndroTask task;
    FloatingActionButton fab;
    IndexFragment indexFragment;
    CoordinatorLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = (CoordinatorLayout) findViewById(R.id.fragment_container);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        indexFragment = new IndexFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, indexFragment);
        transaction.commit();


        if (!Helper.isOnline(this)) {
            Snackbar.make(layout, getResources().getString(R.string.offline_text),
                    Snackbar.LENGTH_LONG).show();
        } else {
            task = new AndroTask();
            task.execute();
        }
    }

    @Override
    public void onClick(View view) {
        if (!Helper.isOnline(this)) {
            Snackbar.make(layout, getResources().getString(R.string.offline_text),
                    Snackbar.LENGTH_LONG).show();
        } else {
            task = new AndroTask();
            task.execute();
        }
    }


    private class AndroTask extends AsyncTask<Void, Void, RecipeDTO[]> {
        @Override
        protected RecipeDTO[] doInBackground(Void... voids) {
            RestTemplate template = new RestTemplate();
            template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            ResponseEntity<RecipeDTO[]> responseEntity = template.getForEntity(Constants.URL.INDEX, RecipeDTO[].class);
            RecipeDTO[] data = responseEntity.getBody();
            return data;
        }

        @Override
        protected void onPostExecute(RecipeDTO[] recipeDTOs) {
            ArrayList<RecipeDTO> result = new ArrayList<>();
            for (int i = 0; i < recipeDTOs.length; i++) {
                result.add(recipeDTOs[i]);
            }
            indexFragment.refreshData(result);
        }
    }
}
