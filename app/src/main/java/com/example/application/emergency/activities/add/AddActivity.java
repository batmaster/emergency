package com.example.application.emergency.activities.add;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.application.emergency.R;
import com.example.application.emergency.activities.list.ListActivity;
import com.example.application.emergency.activities.list.ListPagerAdapter;
import com.example.application.emergency.services.EmergencyApplication;
import com.example.application.emergency.services.HTTPService;
import com.example.application.emergency.services.Preferences;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AddActivity extends AppCompatActivity {

    private EmergencyApplication app;

    private DetailFragment detailFragment;
    private ImagesFragment imagesFragment;

    private int aid;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AddPagerAdapter pagerAdapter;

    private Button buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        app = (EmergencyApplication) getApplication();

        aid = getIntent().getIntExtra("aid", -1);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        viewPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new AddPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
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

                String title = detailFragment.getEditTextTitle().getText().toString();
                String detail = detailFragment.getEditTextDetail().getText().toString();
                int typeId = detailFragment.getSpinnerValue().get(detailFragment.getSpinner().getSelectedItemPosition());
                double locationX = detailFragment.getMarker().getPosition().longitude;
                double locationY = detailFragment.getMarker().getPosition().latitude;
                final ArrayList<Uri> imageUris = imagesFragment.getImageUris();

                String peopleId = app.getPreferences().getString(Preferences.KEY_OFFICER_ID);
                if (peopleId == null) {
                    TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                    peopleId = tManager.getDeviceId();
                }

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("function", "add_accident");
                params.put("title", title);
                params.put("detail", detail);
                params.put("type_id", String.valueOf(typeId));
                params.put("location_x", String.valueOf(locationX));
                params.put("location_y", String.valueOf(locationY));
                params.put("people_id", peopleId);

                app.getHttpService().callPHP(params, new HTTPService.OnResponseCallback<JSONObject>() {
                    @Override
                    public void onResponse(boolean success, Throwable error, JSONObject data) {
                        if (data != null) {
                            try {
                                String id = data.getString("id");
                                app.getHttpService().upload(imageUris, id);

                                startActivity(new Intent(getApplicationContext(), ListActivity.class));
                                finish();


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

//                app.getHttpService().upload(imageUris);

            }
        });
    }

    public int getAid() {
        return aid;
    }

    public void setDetailFragment(DetailFragment detailFragment) {
        this.detailFragment = detailFragment;
    }

    public void setImagesFragment(ImagesFragment imagesFragment) {
        this.imagesFragment = imagesFragment;
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
            case R.id.menuLogout:
                Toast.makeText(this, "Menu Item 1 selected", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
