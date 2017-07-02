package com.example.application.emergency.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.application.emergency.R;
import com.example.application.emergency.activities.add.AddActivity;
import com.example.application.emergency.activities.list.ListActivity;
import com.example.application.emergency.activities.user.UserActivity;
import com.example.application.emergency.services.EmergencyApplication;
import com.example.application.emergency.services.HTTPService;
import com.example.application.emergency.services.Preferences;
import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

/**
 * class แสดงผล activity หน้าแรก
 */
public class MainActivity extends AppCompatActivity {

    /** ประกาศตัวแปร และ component ที่ใช้ในหน้า **/
    private EmergencyApplication app;

    private Button buttonAdd;
    private Button buttonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = (EmergencyApplication) getApplication();

        /** ตั้งค่า component **/
        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission(199)) {
                    goTo(AddActivity.class);
                }


            }
        });

        buttonList = (Button) findViewById(R.id.buttonList);
        buttonList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission(200)) {
                    goTo(ListActivity.class);
                }
            }
        });

        if (AccessToken.getCurrentAccessToken() != null) {
            checkIsBlocked();
        }
    }

    /** ฟังก์ชั่นของระบบแอนดรอยด์ สำหรับเรียกใช้หลังการกลับจาก process อื่น **/
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 199 || requestCode == 200) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "กรุณาอนุมัติการเข้าถึง", Toast.LENGTH_SHORT).show();
                    goToSettings();
                    break;
                }
            }
            goTo(requestCode == 199 ? AddActivity.class : ListActivity.class);
        }
    }

    /** ฟังก์ชั่นสำหรับตรวจสอบการอนุญาติใช้งาน กล้อง ตำแหน่งปัจจุบัน การเขียนอ่านไฟล์ลงในเครื่อง การอ่านข้อมูลโทรศัพท์ **/
    private boolean checkPermission(int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_PHONE_STATE)) {

                goToSettings();
                return false;
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,}, requestCode);
                return false;
            }
        }
        return true;
    }

    /** ฟังก์ชั่นสำหรับเปลี่ยนหน้าให้ผู้ใช้กดอนุญาติการให้ใช้งาน หากยังไม่ได้อนุญาติ **/
    private void goToSettings() {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(myAppSettings);
        finish();
    }

    /** ตั้งค่าปุ่มเมนูในหน้า activity **/
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);

        if (app.getPreferences().getString(Preferences.KEY_USER_TYPE) == null) {
            menu.removeItem(R.id.menuLogout);
            menu.removeItem(R.id.menuUser);
        }
        else {
            if (Integer.parseInt(app.getPreferences().getString(Preferences.KEY_USER_TYPE)) < 2) {
                menu.removeItem(R.id.menuUser);
            }

            menu.removeItem(R.id.menuLogin);
        }
        menu.removeItem(R.id.menuMain);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSummary:
                startActivity(new Intent(getApplicationContext(), SummaryActivity.class));
                break;
            case R.id.menuUser:
                goTo(UserActivity.class);
                break;
            case R.id.menuLogin:
                goTo(ListActivity.class);
                break;
            case R.id.menuLogout:
                LoginManager.getInstance().logOut();
                app.getPreferences().removeString(Preferences.KEY_USER_TYPE);
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        app.getFacebookCallbackManager().onActivityResult(requestCode, resultCode, data);
    }

    private void goTo(final Class c) {
        if (AccessToken.getCurrentAccessToken() == null) {
            LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile"));
            LoginManager.getInstance().registerCallback(app.getFacebookCallbackManager(), new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    final HashMap<String, String> params2 = new HashMap<String, String>();
                    params2.put("function", "check_user");
                    final String user_id = AccessToken.getCurrentAccessToken().getUserId();
                    params2.put("user_id", user_id);
                    new GraphRequest(
                            AccessToken.getCurrentAccessToken(), "/" + user_id, null, HttpMethod.GET, new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            try {
                                params2.put("current_name", response.getJSONObject().getString("name"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            app.getHttpService().callPHP(params2, new HTTPService.OnResponseCallback<JSONObject>() {
                                @Override
                                public void onResponse(boolean success, Throwable error, JSONObject data) {
                                    if (data != null) {
                                        try {
                                            app.getPreferences().putString(Preferences.KEY_USER_TYPE, data.getString("type"));

                                            if (data.getInt("status") == 0) {
                                                Toast.makeText(getApplicationContext(), "ผู้ใช้ " + Profile.getCurrentProfile().getName() + " ถูกระงับการใช้งาน กรุณาติดต่อเจ้าหน้าที่", Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                startActivity(new Intent(MainActivity.this, c));
                                                finish();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        }
                    }
                    ).executeAsync();
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException error) {
                    Log.d("fbb", error.getMessage());
                }
            });

        }
        else {
            final HashMap<String, String> params2 = new HashMap<String, String>();
            params2.put("function", "check_user");
            final String user_id = AccessToken.getCurrentAccessToken().getUserId();
            params2.put("user_id", user_id);
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(), "/" + user_id, null, HttpMethod.GET, new GraphRequest.Callback() {
                public void onCompleted(GraphResponse response) {
                    try {
                        params2.put("current_name", response.getJSONObject().getString("name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    app.getHttpService().callPHP(params2, new HTTPService.OnResponseCallback<JSONObject>() {
                        @Override
                        public void onResponse(boolean success, Throwable error, JSONObject data) {
                            if (data != null) {
                                try {
                                    app.getPreferences().putString(Preferences.KEY_USER_TYPE, data.getString("type"));

                                    if (data.getInt("status") == 0) {
                                        Toast.makeText(getApplicationContext(), "ผู้ใช้ " + Profile.getCurrentProfile().getName() + " ถูกระงับการใช้งาน กรุณาติดต่อเจ้าหน้าที่", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        startActivity(new Intent(MainActivity.this, c));
                                        finish();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            }
            ).executeAsync();
        }
    }

    private void checkIsBlocked() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("function", "check_user");
        params.put("user_id", AccessToken.getCurrentAccessToken().getUserId());
        params.put("current_name", Profile.getCurrentProfile().getName());
        app.getHttpService().callPHP(params, new HTTPService.OnResponseCallback<JSONObject>() {
            @Override
            public void onResponse(boolean success, Throwable error, JSONObject data) {
                if (data != null) {
                    try {
                        app.getPreferences().putString(Preferences.KEY_USER_TYPE, data.getString("type"));

                        if (data.getInt("status") == 0) {
                            Toast.makeText(getApplicationContext(), "ผู้ใช้ " + Profile.getCurrentProfile().getName() + " ถูกระงับการใช้งาน กรุณาติดต่อเจ้าหน้าที่", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "ไม่สามารถตรวจสอบสถานะผู้ใช้งาน กรุณาตรวจสอบการเชื่อมต่อ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
