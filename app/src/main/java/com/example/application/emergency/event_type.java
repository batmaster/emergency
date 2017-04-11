package com.example.application.emergency;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class event_type extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_type);
    }

    public  void notify2 (View view){
        Button btn_Next = (Button) findViewById(R.id.button13);
        Intent intent = new Intent(event_type.this,notification.class);
        startActivity(intent);

    }
}
