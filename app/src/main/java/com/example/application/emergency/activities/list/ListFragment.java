package com.example.application.emergency.activities.list;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.application.emergency.R;
import com.example.application.emergency.services.EmergencyApplication;
import com.example.application.emergency.services.HTTPService;
import com.example.application.emergency.services.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * class แสดงผล fragment หน้าแสดงรายการการแจ้งเหตุ
 */
public class ListFragment extends Fragment {

    /** ประกาศตัวแปร และ component ที่ใช้ในหน้า **/
    private static EmergencyApplication app;

    private int status;
    public static final String KEY_STATUS = "KEY_STATUS";

    private SearchView searchView;
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

        /** ตั้งค่า component **/
        final View v = inflater.inflate(R.layout.fragment_list, container, false);

        searchView = (SearchView) v.findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                loadList();
                return false;
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setIconified(false);
            }
        });
//        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                Log.d("focus", b + "  " + view.toString());
//                if (!b) {
//                    hideKeyboard(view);
//                }
//            }
//        });

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

    /** ฟังก์ชั่นสำหรับดาวน์โหลดรายการการแจ้งเหตุจาก server **/
    public void loadList() {
        /** ประกาศ parameter สำหรับสื่อสาร และเรียกใช้ฟังก์ชั่นบน server **/
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("function", "get_accidents");
        params.put("status", Integer.toString(status));
        params.put("search", String.valueOf(searchView.getQuery()));
        if (app.getPreferences().getString(Preferences.KEY_OFFICER_ID) == null) {
            String phone = app.getPreferences().getString(Preferences.KEY_PHONE);
            if (phone == null || phone.equals("")) {
                phone = app.getPhoneNumber();
            }
            if (phone.equals("")) {
                phone = "x";
            }
            params.put("phone", phone);
        }
        app.getHttpService().callPHP(params, new HTTPService.OnResponseCallback<JSONObject>() {
            @Override
            public void onResponse(boolean success, Throwable error, JSONObject data) {
                if (data != null) {
                    try {
                        JSONArray a = data.getJSONArray("array");

                        ArrayList<ListModel> list = new ArrayList<ListModel>();
                        for (int i = 0; i < a.length(); i++) {
                            JSONObject o = a.getJSONObject(i);

                            list.add(new ListModel(
                                    o.getInt("id"),
                                    o.getInt("type_id"),
                                    o.getString("type"),
                                    o.getString("title"),
                                    o.getString("phone"),
                                    o.getDouble("location_x"),
                                    o.getDouble("location_y"),
                                    o.getInt("status"),
                                    o.getString("date"),
                                    o.getString("color"),
                                    o.getString("type_image")
                            ));
                        }

                        ListViewAdapter adapter = new ListViewAdapter(getContext(), list, getActivity());
                        listView.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
