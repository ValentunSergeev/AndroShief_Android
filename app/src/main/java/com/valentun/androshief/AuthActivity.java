package com.valentun.androshief;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.valentun.androshief.DTOs.User;
import com.valentun.androshief.Fragments.AuthFragment;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class AuthActivity extends AppCompatActivity implements AuthFragment.OnAuthFragmentListener {

    private CoordinatorLayout fragmentContiner;

    private final int FRAGMENT_CONTAINER_ID = R.id.auth_container;

    private AuthFragment authFragment;

    private FragmentTransaction transaction;

    private MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

    private String pass, uid;

    private RegisterTask registerTask;
    private SignInTask signInTask;
    private User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        fragmentContiner = (CoordinatorLayout) findViewById(FRAGMENT_CONTAINER_ID);

        authFragment = new AuthFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.add(FRAGMENT_CONTAINER_ID, authFragment);
        transaction.commit();
    }

    @Override
    public void onRegisterButtonSelected(String email, String password) {
        if (isOnline()) {
            uid = email;
            pass = password;
            registerTask = new RegisterTask();
            registerTask.execute();
        }
    }

    @Override
    public void onSignInButtonSelected(String email, String password) {
        if (isOnline()) {
            uid = email;
            pass = password;
            signInTask = new SignInTask();
            signInTask.execute();
        }
    }

    private boolean isOnline() {
        if (Helper.isOnline(this)) {
            return true;
        } else {
            Snackbar.make(fragmentContiner, getResources().getString(R.string.offline_text),
                    Snackbar.LENGTH_LONG).show();
            return false;
        }
    }

    private void LoggedIn(String uid, String accessToken, String client) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("Uid", uid);
        intent.putExtra("Access-Token", accessToken);
        intent.putExtra("Client", client);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void setCreditHeaders(String email, String password) {
        headers.clear();
        headers.add("Content-Type", "application/json");
        headers.add("email", email);
        headers.add("password", password);
    }

    private class RegisterTask extends AsyncTask<Void, Void, Void> {

        private boolean isRegistred = false;

        @Override
        protected Void doInBackground(Void... voids) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            setCreditHeaders(uid, pass);

            HttpEntity<String> entity = new HttpEntity<>("", headers);

            try {
                restTemplate.exchange(Constants.URL.REGISTER, HttpMethod.POST, entity, User.class);
                isRegistred = true;
            } catch (org.springframework.web.client.HttpClientErrorException e) {
                Snackbar.make(fragmentContiner, e.getMessage(),
                        Snackbar.LENGTH_LONG).show();
                Log.d("ss", e.getResponseBodyAsString());
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            if (isRegistred) {
                onSignInButtonSelected(uid, pass);
            }
        }
    }

    private class SignInTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            setCreditHeaders(uid, pass);

            HttpEntity<String> entity = new HttpEntity<>("", headers);

            try {
                ResponseEntity<User> respEntity = restTemplate.exchange(Constants.URL.SIGN_IN, HttpMethod.POST, entity, User.class);
                user = respEntity.getBody();
                MultiValueMap<String, String> respHeaders = respEntity.getHeaders();
                LoggedIn(respHeaders.getFirst("Uid"), respHeaders.getFirst("Access-Token"), respHeaders.getFirst("Client"));
            } catch (org.springframework.web.client.HttpClientErrorException e) {
                Snackbar.make(fragmentContiner, e.getMessage(),
                        Snackbar.LENGTH_LONG).show();
            }
            return null;
        }
    }
}
