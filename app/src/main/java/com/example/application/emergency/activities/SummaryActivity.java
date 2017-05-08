package com.example.application.emergency.activities;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.application.emergency.R;
import com.example.application.emergency.services.EmergencyApplication;
import com.example.application.emergency.services.HTTPService;
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

public class SummaryActivity extends AppCompatActivity {

    /** ประกาศตัวแปร และ component ที่ใช้ในหน้า **/
    private EmergencyApplication app;

    private TextView textViewDate;
    private ImageView imageViewDatePicker;
    private DatePicker datePicker;

    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        app = (EmergencyApplication) getApplication();

        /** ตั้งค่า component **/
        final Dialog dialog = new Dialog(SummaryActivity.this);
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

                Calendar calendar = Calendar.getInstance();
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
        textViewDate.setText(app.SDF.format(calendar.getTime()));


        imageViewDatePicker = (ImageView) findViewById(R.id.imageViewDatePicker);
        imageViewDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        barChart = (BarChart) findViewById(R.id.barChart);
        loadChart(calendar);
    }

    /** ฟังก์ชั่นสำหรับดาวน์โหลดรายการการแจ้งเหตุจาก server **/
    private void loadChart(final Calendar calendar) {
        textViewDate.setText(app.SDF.format(calendar.getTime()));

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
                        params.put("date", app.SQLSDF.format(calendar.getTime()));
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
}
