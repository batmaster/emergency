package com.bstudio.emergency.activities.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.bstudio.application.emergency.R;
import com.bstudio.emergency.activities.MainActivity;
import com.bstudio.emergency.activities.SummaryActivity;
import com.bstudio.emergency.services.EmergencyApplication;
import com.bstudio.emergency.services.Preferences;
import com.facebook.login.LoginManager;

/**
 * class แสดงผล activity หน้ารายการผู้ใช้
 */
public class UserActivity extends AppCompatActivity {

    /** ประกาศตัวแปร และ component ที่ใช้ในหน้า **/
    private EmergencyApplication app;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        app = (EmergencyApplication) getApplication();

        /** ตั้งค่า component **/
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(2);
        final PagerAdapter pagerAdapter = new UserPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}
