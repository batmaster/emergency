package com.example.application.emergency.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.application.emergency.R;
import com.example.application.emergency.activities.add.AddActivity;
import com.example.application.emergency.activities.list.ListActivity;
import com.example.application.emergency.services.EmergencyApplication;
import com.example.application.emergency.services.HTTPService;
import com.example.application.emergency.services.Preferences;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * class แสดงผล activity หน้าแรก
 */
public class MainActivity extends AppCompatActivity {

    /** ประกาศตัวแปร และ component ที่ใช้ในหน้า **/
    private EmergencyApplication app;

    private Button buttonAdd;
    private Button buttonOfficer;
    private Button buttonList;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy MMMM");
    private static final SimpleDateFormat SQLSDF = new SimpleDateFormat("yyyy-MM-01 00:00:00");

    private TextView textViewDate;
    private ImageView imageViewDatePicker;
    private DatePicker datePicker;

    private BarChart barChart;

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
                if (checkPermission()) {
                    startActivity(new Intent(MainActivity.this, AddActivity.class));
                    finish();
                }
            }
        });

        buttonOfficer = (Button) findViewById(R.id.buttonOfficer);
        buttonOfficer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        });

        buttonList = (Button) findViewById(R.id.buttonList);
        buttonList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()) {
                    startActivity(new Intent(MainActivity.this, ListActivity.class));
                    finish();
                }
            }
        });

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_datepicker);
        dialog.setCancelable(true);

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

                final Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, datePicker.getYear());
                calendar.set(Calendar.MONTH, datePicker.getMonth());

                loadChart(calendar);
            }
        });

        datePicker = (DatePicker) dialog.findViewById(R.id.datePicker);
        datePicker.findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
        datePicker.setMaxDate(new Date().getTime());

        textViewDate = (TextView) findViewById(R.id.textViewDate);

        Calendar calendar = Calendar.getInstance();
        textViewDate.setText(SDF.format(calendar.getTime()));


        imageViewDatePicker = (ImageView) findViewById(R.id.imageViewDatePicker);
        imageViewDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        barChart = (BarChart) findViewById(R.id.barChart);
        loadChart(calendar);

        /** เปลี่ยนหน้าไปหน้า list หากมีบันทึก id ของ officer ไว้ **/
        if (app.getPreferences().getString(Preferences.KEY_OFFICER_ID) != null) {
            buttonOfficer.setVisibility(View.GONE);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) buttonList.getLayoutParams();
            params.weight = 50f;
            buttonList.setLayoutParams(params);
        }
    }

    /** ฟังก์ชั่นของระบบแอนดรอยด์ สำหรับเรียกใช้หลังการกลับจาก process อื่น **/
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 199) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "กรุณาอนุมัติการเข้าถึง", Toast.LENGTH_SHORT).show();
                    goToSettings();
                    break;
                }
            }
        }
    }

    /** ฟังก์ชั่นสำหรับตรวจสอบการอนุญาติใช้งาน กล้อง ตำแหน่งปัจจุบัน การเขียนอ่านไฟล์ลงในเครื่อง การอ่านข้อมูลโทรศัพท์ **/
    private boolean checkPermission() {
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
                        Manifest.permission.READ_PHONE_STATE,}, 199);
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

    /** ฟังก์ชั่นสำหรับดาวน์โหลดรายการการแจ้งเหตุจาก server **/
    private void loadChart(final Calendar calendar) {
        textViewDate.setText(SDF.format(calendar.getTime()));

        /** ประกาศ parameter สำหรับสื่อสาร และเรียกใช้ฟังก์ชั่นบน server **/
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("function", "get_accident_types");
        app.getHttpService().callPHP(params, new HTTPService.OnResponseCallback<JSONObject>() {
            @Override
            public void onResponse(boolean success, Throwable error, JSONObject data) {
                if (data != null) {
                    try {
                        JSONArray a = data.getJSONArray("array");

                        final int maxtype = a.length();
                        final int[] colors = new int[maxtype];
                        final String[] types = new String[maxtype];
                        final SparseArray<Integer> typeOrders = new SparseArray<Integer>();

                        for (int i = 0; i < maxtype; i++) {
                            JSONObject o = a.getJSONObject(i);

                            colors[i] = Color.parseColor(o.getString("color"));
                            types[i] = o.getString("amount") + " " + o.getString("title").replace("อุบัติเหตุ", "");
                            typeOrders.put(Integer.parseInt(o.getString("id")), i);
                        }

                        /** ประกาศ parameter สำหรับสื่อสาร และเรียกใช้ฟังก์ชั่นบน server **/
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("function", "summary_accidents");
                        params.put("date", SQLSDF.format(calendar.getTime()));
                        app.getHttpService().callPHP(params, new HTTPService.OnResponseCallback<JSONObject>() {
                            @Override
                            public void onResponse(boolean success, Throwable error, JSONObject data) {
                                if (data != null) {
                                    try {
                                        JSONArray a = data.getJSONArray("array");

                                        int maxday = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);

                                        float[][] xy = new float[maxday][maxtype];

                                        for (int i = 0; i < a.length(); i++) {
                                            JSONObject o = a.getJSONObject(i);
                                            int d = Integer.parseInt(o.getString("date"));
                                            int t = typeOrders.get(Integer.parseInt(o.getString("type_id")));
                                            float am = Float.parseFloat(o.getString("amount"));

                                            xy[d][t] = am;
                                        }

                                        List<BarEntry> entries = new ArrayList<BarEntry>();
                                        for (int i = 0; i < maxday; i++) {
                                            entries.add(new BarEntry((i + 1), xy[i]));
                                        }

                                        BarDataSet barDataSet = new BarDataSet(entries, "");
                                        barDataSet.setColors(colors);
                                        barDataSet.setStackLabels(types);
                                        BarData barData = new BarData(barDataSet);
                                        barData.setValueFormatter(new IValueFormatter() {

                                            @Override
                                            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                                                if (value == 0) {
                                                    return "";
                                                }
                                                return String.format("%d", (int) value);
                                            }
                                        });
                                        barChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
                                            @Override
                                            public String getFormattedValue(float value, AxisBase axis) {
                                                return String.format("%02d", (int) value);
                                            }
                                        });
                                        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                                            @Override
                                            public void onValueSelected(Entry e, Highlight h) {

                                            }

                                            @Override
                                            public void onNothingSelected() {

                                            }
                                        });
                                        barChart.getDescription().setEnabled(false);
                                        barChart.setData(barData);
                                        barChart.invalidate();


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
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
}
