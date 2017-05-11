package com.example.application.emergency.activities.officer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.application.emergency.R;
import com.example.application.emergency.activities.add.AddActivity;
import com.example.application.emergency.activities.list.ListModel;
import com.example.application.emergency.services.EmergencyApplication;
import com.example.application.emergency.services.HTTPService;
import com.example.application.emergency.services.ReadableTime;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * class สำหรับการนำข้อมูลมาแสดงใน component listview
 */
public class ListViewUserAdapter extends BaseAdapter {

    /** ประกาศตัวแปร และ component ที่ใช้ในหน้า **/
    private Context context;
    private ArrayList<UserModel> list;
    private Activity activity;

    private boolean[] firstChange = {true};

    public ListViewUserAdapter(Context context, ArrayList<UserModel> list, Activity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
    }

    @Override
    public int getItemViewType(int position) {
        return position + 1;
    }

    @Override
    public int getViewTypeCount() {
        return getCount() + 1;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        /** ตั้งค่า component **/
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(view == null) {
            view = mInflater.inflate(R.layout.listview_row_officer, viewGroup, false);
        }

        final ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        Bundle p = new Bundle();
        p.putString("fields", "name, picture.type(large)");
        new GraphRequest(
                AccessToken.getCurrentAccessToken(), "/" + list.get(i).getUserId(), p, HttpMethod.GET, new GraphRequest.Callback() {
            public void onCompleted(GraphResponse response) {
                try {

                    Glide.with(context).load(response.getJSONObject().getJSONObject("picture").getJSONObject("data").getString("url")).listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            e.printStackTrace();

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    }).fitCenter().placeholder(R.drawable.placeholder).into(imageView);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        ).executeAsync();

        TextView textViewName = (TextView) view.findViewById(R.id.textViewName);
        textViewName.setText(list.get(i).getCurrentName());

        TextView textViewLastUseDate = (TextView) view.findViewById(R.id.textViewLastUseDate);
        textViewLastUseDate.setText("ใช้งานล่าสุด " + list.get(i).getLastUseDate());

        final ToggleButton switchType = (ToggleButton) view.findViewById(R.id.switchType);
        switchType.setChecked(list.get(i).getType() > 0);
        switchType.setEnabled(list.get(i).getType() < 2);
        switchType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                /** ประกาศ parameter สำหรับสื่อสาร และเรียกใช้ฟังก์ชั่นบน server **/
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("function", "update_user");
                params.put("user_id", list.get(i).getUserId());
                params.put("type", switchType.isChecked() ? "1" : "0");
                ((EmergencyApplication) context.getApplicationContext()).getHttpService().callPHP(params, new HTTPService.OnResponseCallback<JSONObject>() {
                    @Override
                    public void onResponse(boolean success, Throwable error, JSONObject data) {
                        if (data != null) {
                            try {
                                int type = data.getInt("type");
                                if (type > 0) {
                                    Toast.makeText(context, "มอบหมาย " + list.get(i).getCurrentName() + " เป็นเจ้าหน้าที่แล้ว", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "มอบหมาย " + list.get(i).getCurrentName() + " เป็นผู้ใช้ทั่วไปแล้ว", Toast.LENGTH_SHORT).show();
                                }
                                return;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(context, "ไม่สามารถมอบหมายบทบาทได้", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return view;
    }
}
