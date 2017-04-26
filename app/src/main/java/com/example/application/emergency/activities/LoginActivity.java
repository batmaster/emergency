package com.example.application.emergency.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.application.emergency.R;
import com.example.application.emergency.activities.list.ListActivity;
import com.example.application.emergency.services.EmergencyApplication;
import com.example.application.emergency.services.HTTPService;
import com.example.application.emergency.services.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * class แสดงผล activity หน้าเข้าสู่ระบบ
 */
public class LoginActivity extends AppCompatActivity {

    /** ประกาศตัวแปร และ component ที่ใช้ในหน้า **/
    private EmergencyApplication app;

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonSignin;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        app = (EmergencyApplication) getApplication();

        /** เปลี่ยนหน้าไปหน้า list หากมีบันทึก id ของ officer ไว้ **/
        if (app.getPreferences().getString(Preferences.KEY_OFFICER_ID) != null) {
            Intent intent = new Intent(getApplicationContext(), ListActivity.class);
            startActivity(intent);
            finish();
        }

        /** ตั้งค่า component **/
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonSignin = (Button) findViewById(R.id.buttonSignin);

        buttonSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /** ประกาศ parameter สำหรับสื่อสาร และเรียกใช้ฟังก์ชั่นบน server **/
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("function", "check_user");
                params.put("username", editTextUsername.getText().toString());
                params.put("password", editTextPassword.getText().toString());
                app.getHttpService().callPHP(params, new HTTPService.OnResponseCallback<JSONObject>() {
                    @Override
                    public void onResponse(boolean success, Throwable error, JSONObject data) {
                        if (data != null) {
                            try {
                                /** เปลี่ยนหน้าไปหน้า list หากเข้าสู่ระบบสำเร็จ **/
                                app.getPreferences().putString(Preferences.KEY_OFFICER_ID, data.getString("id"));
                                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                                startActivity(intent);
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }

    /** เปลี่ยนหน้า หากกดปุ่ม back บนมือถือแอนดรอยด์ **/
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}


