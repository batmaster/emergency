package com.example.application.emergency.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.application.emergency.R;
import com.example.application.emergency.acc;
import com.example.application.emergency.history_data;
import com.example.application.emergency.insert_em;
import com.example.application.emergency.processing;


public class WelcomeActivity extends Activity {

    private Button log_out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        log_out = (Button) findViewById(R.id.button);
        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });
    }



    public  void face2 (View view){
        Button btn_Next = (Button) findViewById(R.id.button3);
        Intent intent = new Intent(WelcomeActivity.this,processing.class);
        startActivity(intent);

    }
    public  void face3 (View view){
        Button btn_Next = (Button) findViewById(R.id.button4);
        Intent intent = new Intent(WelcomeActivity.this,history_data.class);
        startActivity(intent);

    }
    public  void face5 (View view){
        Button btn_Next = (Button) findViewById(R.id.button4);
        Intent intent = new Intent(WelcomeActivity.this,insert_em.class);
        startActivity(intent);

    }
    public  void face6 (View view){
        Button btn_Next = (Button) findViewById(R.id.button10);
        Intent intent = new Intent(WelcomeActivity.this,acc.class);
        startActivity(intent);

    }

}
