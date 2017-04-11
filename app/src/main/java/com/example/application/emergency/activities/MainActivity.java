package com.example.application.emergency.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.application.emergency.LoginActivity;
import com.example.application.emergency.R;
import com.example.application.emergency.alarm_listActivity;
import com.example.application.emergency.event_type;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void admin (View view) {
        Button btn_Next = (Button) findViewById(R.id.button2);
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
    }
    public  void notify (View view){
        Button btn_Next = (Button) findViewById(R.id.button5);
        Intent intent = new Intent(MainActivity.this,alarm_listActivity.class);
        startActivity(intent);

    }
    public  void fast (View view){
        Button btn_Next = (Button) findViewById(R.id.button6);
        Intent intent = new Intent(MainActivity.this,event_type.class);
        startActivity(intent);

    }





}
