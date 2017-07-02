package com.example.application.emergency.activities.list;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.application.emergency.R;
import com.example.application.emergency.services.EmergencyApplication;
import com.example.application.emergency.services.HTTPService;
import com.example.application.emergency.services.Preferences;
import com.facebook.AccessToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * class แสดงผล fragment หน้าแสดงรายการการแจ้งเหตุ
 */
public class ListFragment extends Fragment {

    /** ประกาศตัวแปร และ component ที่ใช้ในหน้า **/
    private static EmergencyApplication app;

    private int status;
    public static final String KEY_STATUS = "KEY_STATUS";

    public static final int LIST_PENDING = 0;
    public static final int LIST_PROGRESSING = 1;
    public static final int LIST_DONE = 2;

    private SearchView searchView;
    private ImageView imageViewDatePicker;
    private Dialog dialogDatePicker;

    private DatePicker datePickerFrom;
    private DatePicker datePickerTo;

    private ListView listView;
    private ArrayList<ListModel> list;
    private ListViewAdapter adapter;

    // แบ่งหน้า
    private int page = 1;
    private int row = 20;
    private int maxRow = -1;

    private boolean isLoadMore = false;
    private Handler mHandler = new Handler();
    private ProgressDialog loading;

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

        loading = new ProgressDialog(getActivity());
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setMessage("Loading....");

        searchView = (SearchView) v.findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                list.removeAll(list);
                page = 1;
                maxRow = -1;
                loadList();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.equals("")) {
                    list.removeAll(list);
                    page = 1;
                    maxRow = -1;
                    loadList();
                }
                return false;
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setIconified(false);
            }
        });

        dialogDatePicker = new Dialog(getActivity());
        dialogDatePicker.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogDatePicker.setContentView(R.layout.dialog_datepicker_fragment_list);
        dialogDatePicker.setCancelable(true);

        datePickerFrom = (DatePicker) dialogDatePicker.findViewById(R.id.datePickerFrom);
        datePickerFrom.setMaxDate(new Date().getTime());

        datePickerTo = (DatePicker) dialogDatePicker.findViewById(R.id.datePickerTo);
        datePickerTo.setMaxDate(new Date().getTime());

        final CheckBox checkBox = (CheckBox) dialogDatePicker.findViewById(R.id.checkBox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Calendar c = Calendar.getInstance();
                    datePickerFrom.setEnabled(false);
                    datePickerTo.setEnabled(false);
                    datePickerFrom.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                    datePickerTo.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                }
                else {
                    datePickerFrom.setEnabled(true);
                    datePickerTo.setEnabled(true);
                }
            }
        });

        Button buttonCancel = (Button) dialogDatePicker.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialogDatePicker.dismiss();
            }
        });

        Button buttonOK = (Button) dialogDatePicker.findViewById(R.id.buttonOK);
        buttonOK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialogDatePicker.dismiss();

//                if (checkBox.isChecked()) {
//                    loadList();
//                }
//                else {
//                    Calendar from2 = Calendar.getInstance();
//                    from2.set(Calendar.YEAR, datePickerFrom.getYear());
//                    from2.set(Calendar.MONTH, datePickerFrom.getMonth());
//                    from2.set(Calendar.DAY_OF_MONTH, datePickerFrom.getDayOfMonth()- 7);


                    list.removeAll(list);
                    page = 1;
                    maxRow = -1;
                    loadList();
//                }
            }
        });

        imageViewDatePicker = (ImageView) v.findViewById(R.id.imageViewDatePicker);
        imageViewDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("DIALOG", "licked");

                dialogDatePicker.show();
            }
        });

        listView = (ListView) v.findViewById(R.id.listView);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;


                if (maxRow == -1 || (((page + 1) * row <= maxRow) || ((page + 1) * row) - maxRow <= row)) {
                    if ((lastInScreen == totalItemCount) && !isLoadMore && (firstVisibleItem != 0)) {
                        isLoadMore = true;
                        if (maxRow != -1) {
                            int r = (page + 1) * row;
                            if (r > maxRow) {
                                r = maxRow;
                            }
                            loading.setMessage(String.format("Loading %d from %d....", r, maxRow));
                        }
                        loading.show();

                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Calendar from = Calendar.getInstance();
                                from.set(Calendar.YEAR, datePickerFrom.getYear());
                                from.set(Calendar.MONTH, datePickerFrom.getMonth());
                                from.set(Calendar.DAY_OF_MONTH, datePickerFrom.getDayOfMonth());

                                page++;
                                loadList();
                            }
                        }, 1000);

                    }
                }
            }
        });
        list = new ArrayList<ListModel>();
        adapter = new ListViewAdapter(getContext(), list, getActivity());
        listView.setAdapter(adapter);

        Bundle bundle = getArguments();
        if(bundle != null) {
            status = bundle.getInt(KEY_STATUS);

            list.removeAll(list);
            page = 1;
            maxRow = -1;
            loadList();
        }
        return v;
    }

    /** ฟังก์ชั่นสำหรับดาวน์โหลดรายการการแจ้งเหตุจาก server **/
//    public void loadList() {
//        Calendar from = Calendar.getInstance();
//        from.set(Calendar.YEAR, 1);
//        from.set(Calendar.MONTH, 0);
//        from.set(Calendar.DAY_OF_MONTH, 1990);
//
//        Calendar to = Calendar.getInstance();
//
//        loadList(from, to);
//    }

    public void loadList() {
        Calendar from = Calendar.getInstance();
        from.set(Calendar.YEAR, datePickerFrom.getYear());
        from.set(Calendar.MONTH, datePickerFrom.getMonth());
        from.set(Calendar.DAY_OF_MONTH, datePickerFrom.getDayOfMonth());

        Calendar to = Calendar.getInstance();
        to.set(Calendar.YEAR, datePickerTo.getYear());
        to.set(Calendar.MONTH, datePickerTo.getMonth());
        to.set(Calendar.DAY_OF_MONTH, datePickerTo.getDayOfMonth());

        /** ประกาศ parameter สำหรับสื่อสาร และเรียกใช้ฟังก์ชั่นบน server **/
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("function", "get_accidents");
        params.put("status", Integer.toString(status));
        params.put("search", String.valueOf(searchView.getQuery()));
        params.put("from", EmergencyApplication.SQLSDF_REAL.format(from.getTime()));
        params.put("to", EmergencyApplication.SQLSDF_REAL.format(from.getTime()));
        if (app.getPreferences().getString(Preferences.KEY_USER_TYPE).equals("0")) {
            params.put("user_id", AccessToken.getCurrentAccessToken().getUserId());
        }

        params.put("page", String.valueOf(page));
        params.put("row", String.valueOf(row));

        app.getHttpService().callPHP(params, new HTTPService.OnResponseCallback<JSONObject>() {
            @Override
            public void onResponse(boolean success, Throwable error, JSONObject data) {
                if (data != null) {
                    try {
                        maxRow = data.getInt("count");

                        JSONArray a = data.getJSONArray("array");

                        for (int i = 0; i < a.length(); i++) {
                            JSONObject o = a.getJSONObject(i);

                            list.add(new ListModel(
                                    o.getInt("id"),
                                    o.getInt("type_id"),
                                    o.getString("type"),
                                    o.getString("title"),
                                    o.getString("user_id"),
                                    o.getString("officer_id"),
                                    o.getDouble("location_x"),
                                    o.getDouble("location_y"),
                                    o.getInt("status"),
                                    o.getString("date"),
                                    o.getString("date_approve"),
                                    o.getString("color"),
                                    o.getString("type_image")
                            ));
                        }
                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        loading.dismiss();
                        isLoadMore = false;
                    }
                }
            }
        });
    }
}
