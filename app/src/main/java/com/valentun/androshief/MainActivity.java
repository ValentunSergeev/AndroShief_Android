package com.valentun.androshief;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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
        NewRecipeFragment.OnCreateRecipeListener, NavigationView.OnNavigationItemSelectedListener {


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

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUserAttributes();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeNavDriver(toolbar);

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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                break;
            case R.id.nav_explore:
                break;
            case R.id.nav_my_recipes:
                break;
            case R.id.nav_settings:
                break;
            case R.id.nav_log_out:
                SharedPreferences sPref = getSharedPreferences(Constants.APP_PREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.clear();
                ed.apply();

                Intent intent = new Intent(this, AuthActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case R.id.nav_share:
                break;
            case R.id.nav_send:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    private void initializeNavDriver(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        View headerLayout = navigationView.getHeaderView(0);
        TextView email = (TextView) headerLayout.findViewById(R.id.nav_head_email);
        email.setText(intent.getStringExtra("Uid"));

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initializeUserAttributes() {
        intent = getIntent();

        String uid = intent.getStringExtra("Uid");
        String accessToken = intent.getStringExtra("Access-Token");
        String client = intent.getStringExtra("Client");

        setAuthHeaders(uid, accessToken, client);
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