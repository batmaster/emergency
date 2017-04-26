package com.example.application.emergency.activities.add;

import android.Manifest;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.application.emergency.R;
import com.example.application.emergency.activities.list.ListModel;
import com.example.application.emergency.activities.list.ListViewAdapter;
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by batmaster on 4/15/2017 AD.
 */

public class DetailFragment extends Fragment {

    private static EmergencyApplication app;

    private int aid;

    private EditText editTextTitle;
    private EditText editTextDetail;
    private Spinner spinner;
    private ArrayList<Integer> spinnerValue;
    private LinearLayout layoutStatus;
    private Spinner spinnerStatus;

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

        aid = ((AddActivity)getActivity()).getAid();
        ((AddActivity)getActivity()).setDetailFragment(this);

        View v = inflater.inflate(R.layout.fragment_add_detail, container, false);

        editTextTitle = (EditText) v.findViewById(R.id.editTextTitle);
        editTextDetail = (EditText) v.findViewById(R.id.editTextDetail);

        spinner = (Spinner) v.findViewById(R.id.spinner);
        spinnerValue = new ArrayList<Integer>();

        layoutStatus = (LinearLayout) v.findViewById(R.id.layoutStatus);
        spinnerStatus = (Spinner) v.findViewById(R.id.spinnerStatus);

        mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);


        if (aid == -1) {
            layoutStatus.setVisibility(View.GONE);

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
        else {
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

                            final LatLng ll = new LatLng(o.getDouble("location_x"), o.getDouble("location_y"));
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

            if (app.getPreferences().getString(Preferences.KEY_OFFICER_ID) == null) {
//                editTextTitle.setInputType(InputType.TYPE_NULL);
//                editTextDetail.setInputType(InputType.TYPE_NULL);
//                spinner.setEnabled(false);
                spinnerStatus.setEnabled(false);
            }
        }

        return v;
    }


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
