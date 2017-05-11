package com.example.application.emergency.activities.list;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.application.emergency.R;
import com.example.application.emergency.activities.MainActivity;
import com.example.application.emergency.activities.SummaryActivity;
import com.example.application.emergency.activities.add.AddActivity;
import com.example.application.emergency.services.EmergencyApplication;
import com.example.application.emergency.services.Preferences;
import com.facebook.login.LoginManager;

/**
 * class แสดงผล activity หน้ารายการการแจ้งเหตุ
 */
public class ListActivity extends AppCompatActivity {

    /** ประกาศตัวแปร และ component ที่ใช้ในหน้า **/
    private EmergencyApplication app;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private Button buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        app = (EmergencyApplication) getApplication();

        /** ตั้งค่า component **/
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(3);
        final PagerAdapter pagerAdapter = new ListPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    buttonAdd.setVisibility(View.VISIBLE);
                }
                else {
                    buttonAdd.setVisibility(View.GONE);
                }

                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddActivity.class));
                finish();
            }
        });

        Toast.makeText(getApplicationContext(), "คลิกเหตุการณ์ค้างเพื่อแชร์", Toast.LENGTH_SHORT).show();
    }

    /** ตั้งค่าปุ่มเมนูในหน้า activity **/
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (app.getPreferences().getString(Preferences.KEY_USER_TYPE) == null) {
            menu.removeItem(R.id.menuLogout);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);

        if (app.getPreferences().getString(Preferences.KEY_USER_TYPE) == null) {
            menu.removeItem(R.id.menuLogout);
        }
        menu.removeItem(R.id.menuLogin);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSummary:
                startActivity(new Intent(getApplicationContext(), SummaryActivity.class));
                break;
            case R.id.menuLogout:
                LoginManager.getInstance().logOut();
                app.getPreferences().removeString(Preferences.KEY_USER_TYPE);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                break;
        }
        return true;
    }

    /** เปลี่ยนหน้า หากกดปุ่ม back บนมือถือแอนดรอยด์ **/
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}
