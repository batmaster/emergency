package com.bstudio.emergency.activities.user;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bstudio.emergency.services.EmergencyApplication;
import com.bstudio.emergency.services.HTTPService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bstudio.application.emergency.R;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

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
            view = mInflater.inflate(R.layout.listview_row_user, viewGroup, false);
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

        final boolean[] isClick = {false, false};

        final Switch switchEnable = (Switch) view.findViewById(R.id.switchEnable);
        final Switch switchType = (Switch) view.findViewById(R.id.switchType);

        switchEnable.setChecked(list.get(i).getStatus() == 1);
        switchEnable.setEnabled(list.get(i).getType() < 2);
        switchEnable.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!isClick[0]) {
                    isClick[0] = true;
                    new AlertDialog.Builder(activity)
                            .setCancelable(false)
                            .setTitle("เปลี่ยนสถานะผู้ใช้")
                            .setMessage("เปลี่ยนสถานะผู้ใช้\n" + list.get(i).getCurrentName() + "\nเป็น " + (switchEnable.isChecked() ? "ระงับการใช้งาน" : "เปิดการใช้งาน") + " หรือไม่")
                            .setPositiveButton(R.string.change_confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i2) {
                                    /** ประกาศ parameter สำหรับสื่อสาร และเรียกใช้ฟังก์ชั่นบน server **/
                                    HashMap<String, String> params = new HashMap<String, String>();
                                    params.put("function", "update_user");
                                    params.put("user_id", list.get(i).getUserId());
                                    params.put("type", switchType.isChecked() ? "1" : "0");
                                    params.put("status", switchEnable.isChecked() ? "0" : "1");
                                    ((EmergencyApplication) context.getApplicationContext()).getHttpService().callPHP(params, new HTTPService.OnResponseCallback<JSONObject>() {
                                        @Override
                                        public void onResponse(boolean success, Throwable error, JSONObject data) {
                                            if (data != null) {
                                                try {
                                                    if (data.getInt("status") != 0) {
                                                        Toast.makeText(context, "เปิดการใช้งาน " + list.get(i).getCurrentName() + " แล้ว", Toast.LENGTH_SHORT).show();
                                                        switchEnable.setChecked(true);
                                                    } else {
                                                        Toast.makeText(context, "ระงับการใช้งาน " + list.get(i).getCurrentName() + " แล้ว", Toast.LENGTH_SHORT).show();
                                                        switchEnable.setChecked(false);
                                                    }
                                                    return;
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                Toast.makeText(context, "ไม่สามารถเปลี่ยนสถานะได้", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                    isClick[0] = false;
                                }
                            })
                            .setNegativeButton(R.string.change_no_confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    isClick[0] = false;
                                }
                            }).show();
                }

                return true;
            }
        });

        switchType.setChecked(list.get(i).getType() > 0);
        switchType.setEnabled(list.get(i).getType() < 2);
        switchType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!isClick[1]) {
                    isClick[1] = true;
                    new AlertDialog.Builder(activity)
                        .setCancelable(false)
                        .setTitle("เปลี่ยนบทบาทผู้ใช้")
                        .setMessage("เปลี่ยนบทบาทผู้ใช้\n" + list.get(i).getCurrentName() + "\nเป็น " + (switchType.isChecked() ? "ผู้ใช้" : "เจ้าหน้าที่") + " หรือไม่")
                        .setPositiveButton(R.string.change_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i2) {
                                /** ประกาศ parameter สำหรับสื่อสาร และเรียกใช้ฟังก์ชั่นบน server **/
                                HashMap<String, String> params = new HashMap<String, String>();
                                params.put("function", "update_user");
                                params.put("user_id", list.get(i).getUserId());
                                params.put("type", switchType.isChecked() ? "0" : "1");
                                params.put("status", switchEnable.isChecked() ? "1" : "0");
                                ((EmergencyApplication) context.getApplicationContext()).getHttpService().callPHP(params, new HTTPService.OnResponseCallback<JSONObject>() {
                                    @Override
                                    public void onResponse(boolean success, Throwable error, JSONObject data) {
                                        if (data != null) {
                                            try {
                                                if (data.getInt("type") > 0) {
                                                    Toast.makeText(context, "มอบหมาย " + list.get(i).getCurrentName() + " เป็นเจ้าหน้าที่แล้ว", Toast.LENGTH_SHORT).show();
                                                    switchType.setChecked(true);
                                                } else {
                                                    Toast.makeText(context, "มอบหมาย " + list.get(i).getCurrentName() + " เป็นผู้ใช้ทั่วไปแล้ว", Toast.LENGTH_SHORT).show();
                                                    switchType.setChecked(false);
                                                }

                                                UserListReloadObservable.getInstance().notifyObservers();
                                                return;
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            Toast.makeText(context, "ไม่สามารถมอบหมายบทบาทได้", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                isClick[1] = false;
                            }
                        })
                        .setNegativeButton(R.string.change_no_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                isClick[1] = false;
                            }
                        }).show();
                }

                return true;
            }
        });

        return view;
    }
}
