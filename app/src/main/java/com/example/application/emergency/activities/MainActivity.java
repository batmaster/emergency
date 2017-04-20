package com.example.application.emergency.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.application.emergency.R;
import com.example.application.emergency.activities.add.AddActivity;
import com.example.application.emergency.activities.list.ListActivity;
import com.example.application.emergency.services.EmergencyApplication;
import com.example.application.emergency.services.Preferences;

public class MainActivity extends AppCompatActivity {

    private EmergencyApplication app;

    private Button buttonAdd;
    private Button buttonOfficer;
    private Button buttonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = (EmergencyApplication) getApplication();

        if (app.getPreferences().getString(Preferences.KEY_OFFICER_ID) != null) {
            Intent intent = new Intent(getApplicationContext(), ListActivity.class);
            startActivity(intent);
            finish();
        }

        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddActivity.class));
                finish();
            }
        });

        buttonOfficer = (Button) findViewById(R.id.buttonOfficer);
        buttonOfficer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });

        buttonList = (Button) findViewById(R.id.buttonList);
        buttonList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ListActivity.class));
                finish();
            }
        });

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

                buttonAdd.setEnabled(false);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 199);
            }
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 199) {
            boolean pass = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    buttonAdd.setEnabled(false);
                    break;
                }
            }
        }

    }

}
