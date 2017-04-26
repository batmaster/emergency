package com.example.application.emergency.activities.list;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.application.emergency.R;
import com.example.application.emergency.activities.LoginActivity;
import com.example.application.emergency.activities.MainActivity;
import com.example.application.emergency.activities.add.AddActivity;
import com.example.application.emergency.services.EmergencyApplication;
import com.example.application.emergency.services.Preferences;

public class ListActivity extends AppCompatActivity {

    private EmergencyApplication app;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private Button buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        app = (EmergencyApplication) getApplication();

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter pagerAdapter = new ListPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (app.getPreferences().getString(Preferences.KEY_PHONE) == null ) {
            menu.removeItem(R.id.menuClear);
        }
        if (app.getPreferences().getString(Preferences.KEY_OFFICER_ID) == null ) {
            menu.removeItem(R.id.menuLogout);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuClear:
                app.getPreferences().removeString(Preferences.KEY_PHONE);
                Toast.makeText(getApplicationContext(), "ลบหมายเลขโทรศัพท์เรียบร้อยแล้ว", Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();

                finish();
                startActivity(new Intent(getApplicationContext(), ListActivity.class));
                break;
            case R.id.menuLogout:
                app.getPreferences().removeString(Preferences.KEY_OFFICER_ID);
                app.getPreferences().removeString(Preferences.KEY_PHONE);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                break;
        }
        return true;
    }
}
