package com.example.application.emergency.activities.officer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.application.emergency.R;
import com.example.application.emergency.activities.MainActivity;
import com.example.application.emergency.activities.SummaryActivity;
import com.example.application.emergency.activities.list.ListActivity;
import com.example.application.emergency.activities.list.ListModel;
import com.example.application.emergency.activities.list.ListViewAdapter;
import com.example.application.emergency.services.EmergencyApplication;
import com.example.application.emergency.services.HTTPService;
import com.example.application.emergency.services.Preferences;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * class แสดงผล activity หน้ารายการผู้ใช้
 */
public class OfficerActivity extends AppCompatActivity {

    /** ประกาศตัวแปร และ component ที่ใช้ในหน้า **/
    private EmergencyApplication app;

    private SearchView search;
    private ListView listView;

    private ListViewUserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_officer);

        app = (EmergencyApplication) getApplication();

        search = (SearchView) findViewById(R.id.search);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                load();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.equals("")) {
                    load();
                }
                return false;
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search.setIconified(false);
            }
        });

        listView = (ListView) findViewById(R.id.listView);
        load();
    }

    /** ตั้งค่าปุ่มเมนูในหน้า activity **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);

        if (app.getPreferences().getString(Preferences.KEY_USER_TYPE) == null) {
            menu.removeItem(R.id.menuLogout);
        }
        else {
            menu.removeItem(R.id.menuUser);
            menu.removeItem(R.id.menuLogin);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSummary:
                startActivity(new Intent(getApplicationContext(), SummaryActivity.class));
                break;
            case R.id.menuMain:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
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

    /** ฟังก์ชั่นสำหรับดาวน์โหลดรายการผู้ใช้จาก server **/
    private void load() {
        /** ประกาศ parameter สำหรับสื่อสาร และเรียกใช้ฟังก์ชั่นบน server **/
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("function", "get_users");
        params.put("search", search.getQuery().toString());
        app.getHttpService().callPHP(params, new HTTPService.OnResponseCallback<JSONObject>() {
            @Override
            public void onResponse(boolean success, Throwable error, JSONObject data) {
                if (data != null) {
                    try {
                        JSONArray a = data.getJSONArray("array");

                        ArrayList<UserModel> list = new ArrayList<UserModel>();
                        for (int i = 0; i < a.length(); i++) {
                            JSONObject o = a.getJSONObject(i);

                            list.add(new UserModel(
                                    o.getString("user_id"),
                                    o.getString("current_name"),
                                    o.getInt("type"),
                                    o.getString("last_use_date")
                            ));
                        }

                        adapter = new ListViewUserAdapter(getApplicationContext(), list, OfficerActivity.this);
                        listView.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}
