package com.valentun.androshief;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.valentun.androshief.Adapters.PageAdapter;
import com.valentun.androshief.DTOs.User;
import com.valentun.androshief.Fragments.SignInFragment;
import com.valentun.androshief.Fragments.SignUpFragment;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class AuthActivity extends AppCompatActivity implements SignUpFragment.OnSignUpFragmentListener, SignInFragment.OnSignInFragmentListener {

    private CoordinatorLayout fragmentContiner;

    private final int FRAGMENT_CONTAINER_ID = R.id.auth_container;

    private MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

    private String pass, uid;
    private SharedPreferences sPref;

    private RegisterTask registerTask;
    private SignInTask signInTask;

    private SignUpFragment signUpFragment;
    private SignInFragment signInFragment;

    private TabLayout tabLayout;
    private Toolbar toolbar;
    private ProgressDialog progress;

    private User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        sPref = getPreferences(MODE_PRIVATE);
        String savedEmail = sPref.getString("EMAIL", "");
        String savedPassword = sPref.getString("PASSWORD", "");

        if (!savedEmail.equals("") && isOnline()) {
            uid = savedEmail;
            pass = savedPassword;
            signInTask = new SignInTask();
            signInTask.execute();
            progress = ProgressDialog.show(this, "Signing in",
                    "Searching your account in our database...", true);
        }
        fragmentContiner = (CoordinatorLayout) findViewById(FRAGMENT_CONTAINER_ID);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Sign up"));
        tabLayout.addTab(tabLayout.newTab().setText("Log in"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PageAdapter adapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    @Override
    public void onSignUpButtonSelected(String email, String password, SignUpFragment fragment) {
        signUpFragment = fragment;
        if (isOnline()) {
            uid = email;
            pass = password;
            registerTask = new RegisterTask();
            registerTask.execute();
            progress = ProgressDialog.show(this, "Signing up",
                    "We are registering you. Please wait a bit...", true);
        } else {
            signUpFragment.stopSignUp();
        }
    }

    @Override
    public void onSignInButtonSelected(String email, String password, SignInFragment fragment) {
        signInFragment = fragment;
        if (isOnline()) {
            uid = email;
            pass = password;
            signInTask = new SignInTask();
            signInTask.execute();
            progress = ProgressDialog.show(this, "Signing in",
                    "Searching your account in our database...", true);
        } else {
            signInFragment.stopLogIn();
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
        progress.dismiss();

        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("EMAIL", uid);
        ed.putString("PASSWORD", pass);
        ed.apply();

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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                        signUpFragment.stopSignUp();
                    }
                });
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            if (isRegistred) {
                signInTask = new SignInTask();
                signInTask.execute();
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progress.dismiss();
                    if (signUpFragment != null) signUpFragment.stopSignUp();
                    if (signInFragment != null) signInFragment.stopLogIn();
                }
            });
            return null;
        }
    }
}
