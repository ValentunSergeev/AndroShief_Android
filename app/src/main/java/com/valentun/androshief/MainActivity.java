package com.valentun.androshief;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.valentun.androshief.DTOs.RecipeDTO;
import com.valentun.androshief.Fragments.IndexFragment;
import com.valentun.androshief.Fragments.NewRecipeFragment;
import com.valentun.androshief.Fragments.ShowFragment;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements IndexFragment.OnIndexFragmentActionListener,
        NewRecipeFragment.OnCreateRecipeListener {


    private IndexFragment indexFragment;
    private NewRecipeFragment newRecipeFragment;

    private IndexTask indexTask;
    private CreateTask createTask;

    private MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

    private FragmentTransaction transaction;

    private CoordinatorLayout fragmentContainer;

    private final int FRAGMENT_CONTAINER_ID = R.id.fragment_container;

    private RecipeDTO PostRequest;
    private boolean isRefreshing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        String uid = intent.getStringExtra("Uid");
        String accessToken = intent.getStringExtra("Access-Token");
        String client = intent.getStringExtra("Client");

        setAuthHeaders(uid, accessToken, client);

        fragmentContainer = (CoordinatorLayout) findViewById(FRAGMENT_CONTAINER_ID);


        indexFragment = new IndexFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.add(FRAGMENT_CONTAINER_ID, indexFragment);
        transaction.commit();

        if (isOnline()) {
            indexTask = new IndexTask();
            indexTask.execute();
        }
    }


    @Override
    public void OnRefreshed() {
        if (isOnline()) {
            isRefreshing = true;
            indexTask = new IndexTask();
            indexTask.execute();
        } else {
            indexFragment.stopRefreshing();
        }
    }

    @Override
    public void NewFabSelected() {
        newRecipeFragment = new NewRecipeFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(FRAGMENT_CONTAINER_ID, newRecipeFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onRecipeCreateSubmitted(String name, String description) {
        PostRequest = new RecipeDTO(name, description);
        createTask = new CreateTask();
        createTask.execute();
    }

    private boolean isOnline() {
        if (Helper.isOnline(this)) {
            return true;
        } else {
            Snackbar.make(fragmentContainer, getResources().getString(R.string.offline_text),
                    Snackbar.LENGTH_LONG).show();
            return false;
        }
    }


    private void setAuthHeaders(String email, String accessToken, String client) {
        headers.clear();
        headers.add("Content-Type", "application/json");
        headers.add("Uid", email);
        headers.add("Client", client);
        headers.add("Access-Token", accessToken);
    }

    private class IndexTask extends AsyncTask<Void, Void, RecipeDTO[]> {

        @Override
        protected RecipeDTO[] doInBackground(Void... voids) {
            RestTemplate template = new RestTemplate();
            template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpEntity<String> entity = new HttpEntity<>("", headers);

            try {
                ResponseEntity<RecipeDTO[]> respEntity = template.exchange(Constants.URL.INDEX, HttpMethod.GET, entity, RecipeDTO[].class);
                RecipeDTO[] data = respEntity.getBody();
                return data;
            } catch (HttpClientErrorException e) {
                if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    Snackbar.make(fragmentContainer, getResources().getString(R.string.unauthorized_text),
                            Snackbar.LENGTH_LONG).show();
                    return new RecipeDTO[0];
                }
                return null;
            }
        }

        @Override
        protected void onPostExecute(RecipeDTO[] recipeDTOs) {
            ArrayList<RecipeDTO> result = new ArrayList<>();
            for (int i = 0; i < recipeDTOs.length; i++) {
                result.add(recipeDTOs[i]);
            }
            indexFragment.refreshData(result);
            if (isRefreshing) indexFragment.stopRefreshing();
        }

    }

    private class CreateTask extends AsyncTask<Void, Void, RecipeDTO> {

        @Override
        protected RecipeDTO doInBackground(Void... voids) {
            RestTemplate template = new RestTemplate();
            template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpEntity<RecipeDTO> entity = new HttpEntity<>(PostRequest, headers);
            try {
                ResponseEntity<RecipeDTO> respEntity = template.exchange(Constants.URL.CREATE, HttpMethod.POST, entity, RecipeDTO.class);
                RecipeDTO resp = respEntity.getBody();
                return resp;
            } catch (HttpClientErrorException e) {
                if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    Snackbar.make(fragmentContainer, getResources().getString(R.string.unauthorized_text),
                            Snackbar.LENGTH_LONG).show();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(RecipeDTO response) {
            if (response != null) {
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(FRAGMENT_CONTAINER_ID, ShowFragment.newInstance(response.getName(), response.getDescription()));
                transaction.commit();
            }
        }

    }
}
