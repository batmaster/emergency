package com.example.application.emergency.activities.add;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.application.emergency.R;
import com.example.application.emergency.activities.MainActivity;
import com.example.application.emergency.activities.SummaryActivity;
import com.example.application.emergency.activities.list.ListActivity;
import com.example.application.emergency.services.EmergencyApplication;
import com.example.application.emergency.services.HTTPService;
import com.example.application.emergency.services.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * class แสดงผล activity หน้าเพิ่มการแจ้งเหตุ
 */
public class AddActivity extends AppCompatActivity {

    /** ประกาศตัวแปร และ component ที่ใช้ในหน้า **/
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

        /** ดึงค่า accident id เพื่อตรวจสอบว่า หน้านี้ถูกเปิดโดยการกดปุ่ม เพิ่ม หรือคลิกที่รายการการแจ้งเหตุ **/
        aid = getIntent().getIntExtra("aid", -1);

        /** ตั้งค่า component **/
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(2);
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

                if (aid == -1) {
                    final Dialog dialog = new Dialog(AddActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_number);
                    dialog.setCancelable(true);

                    final EditText editTextPhone = (EditText) dialog.findViewById(R.id.editTextPhone);
                    String phone = app.getPreferences().getString(Preferences.KEY_PHONE);
                    if (phone == null || phone.equals("")) {
                        phone = app.getPhoneNumber();
                    }
                    editTextPhone.setText(phone);

                    Button buttonCancel = (Button) dialog.findViewById(R.id.buttonCancel);
                    buttonCancel.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    Button buttonOK = (Button) dialog.findViewById(R.id.buttonOK);
                    buttonOK.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "กำลังเพิ่มเหตุการณ์", Toast.LENGTH_SHORT).show();

                            String title = detailFragment.getEditTextTitle().getText().toString();
                            int typeId = detailFragment.getSpinnerValue().get(detailFragment.getSpinner().getSelectedItemPosition());
                            double locationX = detailFragment.getMarker().getPosition().latitude;
                            double locationY = detailFragment.getMarker().getPosition().longitude;
                            final ArrayList<Uri> imageUris = imagesFragment.getImageUris();

                            String phone = editTextPhone.getText().toString();
                            app.getPreferences().putString(Preferences.KEY_PHONE, phone);

                            /** ประกาศ parameter สำหรับสื่อสาร และเรียกใช้ฟังก์ชั่นบน server **/
                            HashMap<String, String> params = new HashMap<String, String>();
                            params.put("function", "add_accident");
                            params.put("title", title);
                            params.put("type_id", String.valueOf(typeId));
                            params.put("location_x", String.valueOf(locationX));
                            params.put("location_y", String.valueOf(locationY));
                            params.put("phone", phone);

                            app.getHttpService().callPHP(params, new HTTPService.OnResponseCallback<JSONObject>() {
                                @Override
                                public void onResponse(boolean success, Throwable error, JSONObject data) {
                                    if (data != null) {
                                        try {
                                            String id = data.getString("id");
                                            app.getHttpService().upload(imageUris, id);

                                            Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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

                    dialog.show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "กำลังอัพเดตเหตุการณ์", Toast.LENGTH_SHORT).show();

                    String title = detailFragment.getEditTextTitle().getText().toString();
                    int typeId = detailFragment.getSpinnerValue().get(detailFragment.getSpinner().getSelectedItemPosition());
                    int status = detailFragment.getStatus();
                    double locationX = detailFragment.getMarker().getPosition().latitude;
                    double locationY = detailFragment.getMarker().getPosition().longitude;
                    final ArrayList<Uri> imageUris = imagesFragment.getImageUris();
                    String officerId = app.getPreferences().getString(Preferences.KEY_OFFICER_ID);
                    if (officerId == null) {
                        officerId = "";
                    }

                    /** ประกาศ parameter สำหรับสื่อสาร และเรียกใช้ฟังก์ชั่นบน server **/
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("function", "edit_accident");
                    params.put("aid", String.valueOf(aid));
                    params.put("officer_id", officerId);
                    params.put("title", title);
                    params.put("status", String.valueOf(status));
                    params.put("type_id", String.valueOf(typeId));
                    params.put("location_x", String.valueOf(locationX));
                    params.put("location_y", String.valueOf(locationY));
                    params.put("status", String.valueOf(status));

                    app.getHttpService().callPHP(params, new HTTPService.OnResponseCallback<JSONObject>() {
                        @Override
                        public void onResponse(boolean success, Throwable error, JSONObject data) {
                            if (data != null) {
                                try {
                                    String id = data.getString("id");
                                    app.getHttpService().upload(imageUris, id);

                                    Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }

            }
        });

        /** เปลี่ยนข้อความบนปุ่ม เพิ่ม เป็น อัพเดต หากหน้านี้ถูกเรียกโดยการคลิกรายการการแจ้งเหตุ **/
        if (aid != -1) {
            buttonAdd.setText("อัพเดต");
        }
    }

    public int getAid() {
        return aid;
    }

    /** ฟังก์ชั่นสำหรับ fragment ตั้งค่าตัวแปรให้ activity **/
    public void setDetailFragment(DetailFragment detailFragment) {
        this.detailFragment = detailFragment;
    }

    public void setImagesFragment(ImagesFragment imagesFragment) {
        this.imagesFragment = imagesFragment;
    }

    /** ตั้งค่าปุ่มเมนูในหน้า activity **/
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
            case R.id.menuSummary:
                startActivity(new Intent(getApplicationContext(), SummaryActivity.class));
                break;
            case R.id.menuClear:
                app.getPreferences().removeString(Preferences.KEY_PHONE);
                Toast.makeText(getApplicationContext(), "ลบหมายเลขโทรศัพท์เรียบร้อยแล้ว", Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();
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

    /** ฟังก์ชั่นของระบบแอนดรอยด์ สำหรับเรียกใช้หลังการกลับจาก process อื่น **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    /** เปลี่ยนหน้า หากกดปุ่ม back บนมือถือแอนดรอยด์ **/
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), ListActivity.class));
        finish();
    }
}
