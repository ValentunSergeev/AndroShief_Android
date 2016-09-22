package com.valentun.androshief;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.valentun.androshief.DTOs.RecipeDTO;
import com.valentun.androshief.adapter.RecipeAdapter;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button refreshButton;
    RecyclerView rv;
    TextView offlineText;
    AndroTask task;
    ArrayList<RecipeDTO> recipies = new ArrayList<>();
    RecipeAdapter adapter = new RecipeAdapter(recipies);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = (RecyclerView) findViewById(R.id.recycleView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        refreshButton = (Button) findViewById(R.id.refresh_data_button);
        refreshButton.setOnClickListener(this);

        offlineText = (TextView) findViewById(R.id.offline_button);

        if (!Helper.isOnline(this)) {
            rv.setVisibility(View.GONE);
            refreshButton.setVisibility(View.GONE);
            offlineText.setVisibility(View.VISIBLE);
        } else {
            task = new AndroTask();
            task.execute();
        }
    }

    @Override
    public void onClick(View view) {
        if (!Helper.isOnline(this)) {
            rv.setVisibility(View.GONE);
            refreshButton.setVisibility(View.GONE);
            offlineText.setVisibility(View.VISIBLE);
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
            adapter.updateData(result);
        }
    }
}
