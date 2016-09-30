package com.valentun.androshief;

import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;

import com.valentun.androshief.DTOs.RecipeDTO;
import com.valentun.androshief.Fragments.IndexFragment;
import com.valentun.androshief.Fragments.NewRecipeFragment;
import com.valentun.androshief.Fragments.ShowFragment;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements IndexFragment.OnFabSelectedListener,
        NewRecipeFragment.OnCreateRecipeListener {

    private final int FRAGMENT_CONTAINER_ID = R.id.fragment_container;

    private IndexTask indexTask;
    private CreateTask createTask;
    private FragmentTransaction transaction;
    private IndexFragment indexFragment;
    private NewRecipeFragment newRecipeFragment;


    private SwipeRefreshLayout layout;

    private RecipeDTO PostRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = (SwipeRefreshLayout) findViewById(R.id.fragment_container);

        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isOnline()) {
                    indexTask = new IndexTask();
                    indexTask.execute();
                } else {
                    layout.setRefreshing(false);
                }
            }
        });


        indexFragment = new IndexFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.add(FRAGMENT_CONTAINER_ID, indexFragment);
        transaction.commit();

        if (isOnline()) {
            indexTask = new IndexTask();
            indexTask.execute();
        }
    }

    private boolean isOnline() {
        if (Helper.isOnline(this)) {
            return true;
        } else {
            Snackbar.make(layout, getResources().getString(R.string.offline_text),
                    Snackbar.LENGTH_LONG).show();
            return false;
        }
    }

    @Override
    public void NewFabSelected() {
        newRecipeFragment = new NewRecipeFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.remove(indexFragment);
        transaction.add(FRAGMENT_CONTAINER_ID, newRecipeFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onRecipeCreateSubmitted(String name, String description) {
        PostRequest = new RecipeDTO(name, description);
        createTask = new CreateTask();
        createTask.execute();
    }

    private class IndexTask extends AsyncTask<Void, Void, RecipeDTO[]> {
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
            layout.setRefreshing(false);
        }
    }

    private class CreateTask extends AsyncTask<Void, Void, RecipeDTO> {
        @Override
        protected RecipeDTO doInBackground(Void... voids) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Content-Type", "application/json");

            HttpEntity<RecipeDTO> entity = new HttpEntity<>(PostRequest, headers);

            ResponseEntity<RecipeDTO> respEntity = restTemplate.exchange(Constants.URL.CREATE, HttpMethod.POST, entity, RecipeDTO.class);

            RecipeDTO resp = respEntity.getBody();
            return resp;
        }

        @Override
        protected void onPostExecute(RecipeDTO response) {
            indexFragment.addItemToData(response);
            transaction = getFragmentManager().beginTransaction();
            transaction.remove(newRecipeFragment);
            transaction.add(FRAGMENT_CONTAINER_ID, ShowFragment.newInstance(response.getName(), response.getDescription()));
            transaction.commit();
        }
    }
}
