package com.example.application.emergency.activities.add;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.application.emergency.R;
import com.example.application.emergency.activities.list.ListModel;
import com.example.application.emergency.activities.list.ListViewAdapter;
import com.example.application.emergency.services.EmergencyApplication;
import com.example.application.emergency.services.HTTPService;
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
 * Created by batmaster on 4/15/2017 AD.
 */

public class DetailFragment extends Fragment {

    private static EmergencyApplication app;

    private EditText editTextTitle;
    private EditText editTextDetail;
    private Spinner spinner;
    private ArrayList<Integer> spinnerValue;

    private MapFragment mapFragment;

    private Marker marker;

    public static DetailFragment getInstance() {
        DetailFragment fragment = new DetailFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        app = (EmergencyApplication) getActivity().getApplication();

        View v = inflater.inflate(R.layout.fragment_add_detail, container, false);

        editTextTitle = (EditText) v.findViewById(R.id.editTextTitle);
        editTextDetail = (EditText) v.findViewById(R.id.editTextDetail);

        spinner = (Spinner) v.findViewById(R.id.spinner);
        spinnerValue = new ArrayList<Integer>();

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


        mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {

                googleMap.getUiSettings().setMapToolbarEnabled(false);

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
            }
        });



        return v;
    }

}
