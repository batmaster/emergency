package com.example.application.emergency.activities.add;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.application.emergency.R;
import com.example.application.emergency.activities.list.ListActivity;
import com.example.application.emergency.services.EmergencyApplication;
import com.example.application.emergency.services.HTTPService;
import com.example.application.emergency.services.Preferences;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * class แสดงผล fragment หน้าแสดงรายระเอียดรายการแจ้งเหตุ
 */
public class DetailFragment extends Fragment {

    /** ประกาศตัวแปร และ component ที่ใช้ในหน้า **/
    private static EmergencyApplication app;

    private int aid;

    private EditText editTextTitle;
    private EditText editTextDetail;
    private Spinner spinner;
    private ArrayList<Integer> spinnerValue;
    private LinearLayout layoutStatus;
    private Spinner spinnerStatus;
    private Button buttonDelete;

    private MapFragment mapFragment;

    private Marker marker;

    private static DetailFragment fragment;

    public static DetailFragment getInstance() {
        if (fragment == null) {
            fragment = new DetailFragment();
        }
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        app = (EmergencyApplication) getActivity().getApplication();

        /** ดึงค่า accident id เพื่อตรวจสอบว่า หน้านี้ถูกเปิดโดยการกดปุ่ม เพิ่ม หรือคลิกที่รายการการแจ้งเหตุ **/
        aid = ((AddActivity)getActivity()).getAid();
        ((AddActivity)getActivity()).setDetailFragment(this);

        /** ตั้งค่า component **/
        View v = inflater.inflate(R.layout.fragment_add_detail, container, false);

        editTextTitle = (EditText) v.findViewById(R.id.editTextTitle);
        editTextDetail = (EditText) v.findViewById(R.id.editTextDetail);

        spinner = (Spinner) v.findViewById(R.id.spinner);
        spinnerValue = new ArrayList<Integer>();

        layoutStatus = (LinearLayout) v.findViewById(R.id.layoutStatus);
        spinnerStatus = (Spinner) v.findViewById(R.id.spinnerStatus);

        buttonDelete = (Button) v.findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /** ประกาศ parameter สำหรับสื่อสาร และเรียกใช้ฟังก์ชั่นบน server **/
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("function", "remove_accident");
                params.put("aid", String.valueOf(aid));
                app.getHttpService().callPHP(params, new HTTPService.OnResponseCallback<JSONObject>() {
                    @Override
                    public void onResponse(boolean success, Throwable error, JSONObject data) {
                        if (data != null) {
                            startActivity(new Intent(getActivity().getApplicationContext(), ListActivity.class));
                            getActivity().finish();
                        }
                    }
                });
            }
        });

        mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);

        /** ซ่่อน component ที่ไม่ได้ใช้งาน ขณะผู้ใช้เพิ่มรายการใหม่ **/
        if (aid == -1) {
            layoutStatus.setVisibility(View.GONE);
            buttonDelete.setVisibility(View.GONE);

            /** ประกาศ parameter สำหรับสื่อสาร และเรียกใช้ฟังก์ชั่นบน server **/
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("function", "get_accident_types");
            app.getHttpService().callPHP(params, new HTTPService.OnResponseCallback<JSONObject>() {
                @Override
                public void onResponse(boolean success, Throwable error, JSONObject data) {
                    if (data != null) {
                        try {
                            JSONArray a = data.getJSONArray("array");

                            ArrayList<String> spinnerArray = new ArrayList<String>();

                            for (int i = 0; i < a.length(); i++) {
                                JSONObject o = a.getJSONObject(i);

                                spinnerValue.add(o.getInt("id"));
                                spinnerArray.add(o.getString("title"));

                            }

                            spinner.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, spinnerArray));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            /** ตั้งค่าแผนที่ และตำแหน่งปัจจุบัน **/
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(final GoogleMap googleMap) {

                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        googleMap.setMyLocationEnabled(true);
                    } else {
                        Toast.makeText(getContext(), "ต้องการการอนุญาติ", Toast.LENGTH_LONG).show();
                        return;
                    }

                    googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            if (marker != null) {
                                marker.setPosition(latLng);
                            }
                            else {
                                marker = googleMap.addMarker(new MarkerOptions().position(latLng).title("สถานที่เกิดเหตุ"));
                            }
                        }
                    });

                    googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                        @Override
                        public void onMyLocationChange(Location location) {
                            final LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                            marker = googleMap.addMarker(new MarkerOptions().position(ll).title("สถานที่เกิดเหตุ"));
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 17f), 1000, null);
                            googleMap.setOnMyLocationChangeListener(null);
                        }
                    });
                }
            });
        }
        /** ซ่่อน component ที่ไม่ได้ใช้งาน หากเป็นการแก้ไขรายการ **/
        else {
            /** ประกาศ parameter สำหรับสื่อสาร และเรียกใช้ฟังก์ชั่นบน server **/
            HashMap<String, String> params2 = new HashMap<String, String>();
            params2.put("function", "get_accident");
            params2.put("aid", String.valueOf(aid));
            app.getHttpService().callPHP(params2, new HTTPService.OnResponseCallback<JSONObject>() {
                @Override
                public void onResponse(boolean success, Throwable error, JSONObject data) {
                    if (data != null) {
                        try {
                            JSONArray a = data.getJSONArray("array");
                            final JSONObject o = a.getJSONObject(0);

                            editTextTitle.setText(o.getString("title"));
                            editTextDetail.setText(o.getString("detail"));
                            spinnerStatus.setSelection(o.getInt("status"));

                            /** ประกาศ parameter สำหรับสื่อสาร และเรียกใช้ฟังก์ชั่นบน server **/
                            HashMap<String, String> params = new HashMap<String, String>();
                            params.put("function", "get_accident_types");
                            app.getHttpService().callPHP(params, new HTTPService.OnResponseCallback<JSONObject>() {
                                @Override
                                public void onResponse(boolean success, Throwable error, JSONObject data) {
                                    if (data != null) {
                                        try {
                                            JSONArray a = data.getJSONArray("array");

                                            ArrayList<String> spinnerArray = new ArrayList<String>();

                                            for (int i = 0; i < a.length(); i++) {
                                                JSONObject o = a.getJSONObject(i);

                                                spinnerValue.add(o.getInt("id"));
                                                spinnerArray.add(o.getString("title"));

                                            }

                                            spinner.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, spinnerArray));

                                            for (int i = 0; i < spinnerValue.size(); i++) {
                                                if (spinnerValue.get(i) == o.getInt("type_id")) {
                                                    spinner.setSelection(i);
                                                    break;
                                                }
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });

                            /** อ่านตำแหน่งจากการแจ้งเหตุ **/
                            final LatLng ll = new LatLng(o.getDouble("location_x"), o.getDouble("location_y"));
                            /** ตั้งค่าแผนที่ และตำแหน่งในการแจ้งเหตุ **/
                            mapFragment.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(final GoogleMap googleMap) {

                                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                        googleMap.setMyLocationEnabled(true);
                                    } else {
                                        Toast.makeText(getContext(), "ต้องการการอนุญาติ", Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                    googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                        @Override
                                        public void onMapClick(LatLng latLng) {
                                            Toast.makeText(getActivity().getApplicationContext(), "คลิกตำแหน่งค้างเพื่อแก้ไขตำแหน่ง", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                                        @Override
                                        public void onMapLongClick(LatLng latLng) {
                                            if (marker != null) {
                                                marker.setPosition(latLng);
                                            } else {
                                                marker = googleMap.addMarker(new MarkerOptions().position(latLng).title("สถานที่เกิดเหตุ"));
                                            }
                                        }
                                    });

                                    if (marker != null) {
                                        marker = null;
                                    }

                                    marker = googleMap.addMarker(new MarkerOptions().position(ll).title("สถานที่เกิดเหตุ"));
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 17f), 1000, null);
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            /** ซ่อน component สถานะ หากไม่ใช่เจ้าหน้าที่ **/
            if (app.getPreferences().getString(Preferences.KEY_OFFICER_ID) == null) {
                spinnerStatus.setEnabled(false);
            }
        }

        return v;
    }

    /** ฟังก์ชั่นสำหรับเรียกใช้ตัวแปรใน class **/
    public EditText getEditTextDetail() {
        return editTextDetail;
    }

    public EditText getEditTextTitle() {
        return editTextTitle;
    }

    public Spinner getSpinner() {
        return spinner;
    }

    public ArrayList<Integer> getSpinnerValue() {
        return spinnerValue;
    }

    public Marker getMarker() {
        return marker;
    }

    public LinearLayout getLayoutStatus() {
        return layoutStatus;
    }

    public Spinner getSpinnerStatus() {
        return spinnerStatus;
    }
}