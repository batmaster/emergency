package com.example.application.emergency.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.application.emergency.R;
import com.example.application.emergency.services.EmergencyApplication;
import com.example.application.emergency.services.HTTPService;
import com.example.application.emergency.services.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class LoginActivity extends AppCompatActivity {

    private EmergencyApplication app;

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonSignin;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        app = (EmergencyApplication) getApplication();

        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonSignin = (Button) findViewById(R.id.buttonSignin);

        buttonSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("function", "login");
                params.put("username", editTextUsername.getText().toString());
                params.put("password", editTextPassword.getText().toString());
                app.getHttpService().callPHP(params, new HTTPService.OnResponseCallback<JSONObject>() {
                    @Override
                    public void onResponse(boolean success, Throwable error, JSONObject data) {
                        if (data != null) {
                            try {
                                app.getPreferences().putString(Preferences.KEY_OFFICER_ID, data.getString("id"));
                                startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }
}


