package com.example.application.emergency;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.application.emergency.activities.MainActivity;

/**
 * Created by KONG on 13/2/2560.
 */

public class alarm_listActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_list);
    }
    public void BackPage3 (View view){
        Button btn_back = (Button)findViewById(R.id.button7);
        Intent intent = new Intent(alarm_listActivity.this,MainActivity.class);
        startActivity(intent);
    }
}
