package com.bstudio.emergency.activities.user;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import com.bstudio.application.emergency.R;
import com.bstudio.emergency.services.EmergencyApplication;
import com.bstudio.emergency.services.HTTPService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * class แสดงผล fragment หน้าแสดงรายการผู้ใช้
 */
public class UserFragment extends Fragment implements Observer {

    /** ประกาศตัวแปร และ component ที่ใช้ในหน้า **/
    private static EmergencyApplication app;

    private String type;
    public static final String KEY_TYPE = "KEY_TYPE";

    public static final String LIST_USER = "= 0";
    public static final String LIST_OFFICER = "> 0";

    private SearchView search;
    private ListView listView;

    private ListViewUserAdapter adapter;

    public static UserFragment getInstance(String type) {
        UserFragment fragment = new UserFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        app = (EmergencyApplication) getActivity().getApplication();

        /** ตั้งค่า component **/
        final View v = inflater.inflate(R.layout.fragment_user, container, false);

        search = (SearchView) v.findViewById(R.id.search);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                load();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.equals("")) {
                    load();
                }
                return false;
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search.setIconified(false);
            }
        });

        listView = (ListView) v.findViewById(R.id.listView);
        load();

        Bundle bundle = getArguments();
        if(bundle != null) {
            type = bundle.getString(KEY_TYPE);
            load();
        }

        UserListReloadObservable.getInstance().addObserver(this);
        return v;
    }

    /** ฟังก์ชั่นสำหรับดาวน์โหลดรายการผู้ใช้จาก server **/
    private void load() {
        /** ประกาศ parameter สำหรับสื่อสาร และเรียกใช้ฟังก์ชั่นบน server **/
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("function", "get_users");
        params.put("search", search.getQuery().toString());
        params.put("type", type);
        app.getHttpService().callPHP(params, new HTTPService.OnResponseCallback<JSONObject>() {
            @Override
            public void onResponse(boolean success, Throwable error, JSONObject data) {
                if (data != null) {
                    try {
                        JSONArray a = data.getJSONArray("array");

                        ArrayList<UserModel> list = new ArrayList<UserModel>();
                        for (int i = 0; i < a.length(); i++) {
                            JSONObject o = a.getJSONObject(i);

                            list.add(new UserModel(
                                    o.getString("user_id"),
                                    o.getString("current_name"),
                                    o.getInt("type"),
                                    o.getInt("status"),
                                    o.getString("last_use_date")
                            ));
                        }

                        adapter = new ListViewUserAdapter(getContext(), list, getActivity());
                        listView.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void update(Observable observable, Object o) {
        load();
    }
}
