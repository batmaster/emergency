package com.example.application.emergency.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.application.emergency.R;
import com.example.application.emergency.services.EmergencyApplication;
import com.example.application.emergency.services.HTTPService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by batmaster on 4/15/2017 AD.
 */

public class ListFragment extends Fragment {

    private static EmergencyApplication app;

    private int status;
    public static final String KEY_STATUS = "KEY_STATUS";

    private ListView listView;

    public static ListFragment getInstance(int status) {
        ListFragment fragment = new ListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_STATUS, status);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        app = (EmergencyApplication) getActivity().getApplication();

        View v = inflater.inflate(R.layout.fragment_list, container, false);

        listView = (ListView) v.findViewById(R.id.listView);

        Bundle bundle = getArguments();
        if(bundle != null) {
            status = bundle.getInt(KEY_STATUS);
            loadList();
        }
        return v;
    }

    public static final int LIST_PENDING = 0;
    public static final int LIST_PROGRESSING = 1;
    public static final int LIST_DONE = 2;

    public void loadList() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("function", "get_accidents");
        params.put("status", Integer.toString(status));

        app.getHttpService().callPHP(params, new HTTPService.OnResponseCallback<JSONObject>() {
            @Override
            public void onResponse(boolean success, Throwable error, JSONObject data) {
                if (data != null) {
                    try {
                        ArrayList<ListModel> list = new ArrayList<ListModel>();
                        JSONArray a = data.getJSONArray("array");

                        for (int i = 0; i < a.length(); i++) {
                            JSONObject o = a.getJSONObject(i);
                            list.add(new ListModel(
                                    o.getInt("type_id"),
                                    o.getString("type"),
                                    o.getString("title"),
                                    o.getInt("people_id"),
                                    o.getString("people"),
                                    o.getInt("officer_id"),
                                    o.getString("officer"),
                                    o.getString("photo"),
                                    o.getString("detail"),
                                    o.getDouble("location_x"),
                                    o.getDouble("location_y"),
                                    o.getInt("status"),
                                    o.getString("date"),
                                    o.getString("approve_date")
                            ));
                        }

                        ListViewAdapter adapter = new ListViewAdapter(getContext(), list);
                        listView.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
