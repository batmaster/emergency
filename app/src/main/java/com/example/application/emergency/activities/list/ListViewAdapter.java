package com.example.application.emergency.activities.list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.application.emergency.R;
import com.example.application.emergency.activities.add.AddActivity;
import com.example.application.emergency.services.EmergencyApplication;
import com.example.application.emergency.services.Preferences;
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

/**
 * class สำหรับการนำข้อมูลมาแสดงใน component listview
 */
public class ListViewAdapter extends BaseAdapter {

    /** ประกาศตัวแปร และ component ที่ใช้ในหน้า **/
    private Context context;
    private ArrayList<ListModel> list;
    private Activity activity;

    public ListViewAdapter(Context context, ArrayList<ListModel> list, Activity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
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
            view = mInflater.inflate(R.layout.listview_row, viewGroup, false);
        }

        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        Glide.with(context).load(list.get(i).getTypeImage()).listener(new RequestListener<String, GlideDrawable>() {
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

        TextView textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
        textViewTitle.setText(list.get(i).getTitle());

        TextView textViewTime = (TextView) view.findViewById(R.id.textViewTime);
        textViewTime.setText(ReadableTime.get(list.get(i).getDate()));

        TextView textViewTimeReal = (TextView) view.findViewById(R.id.textViewTimeReal);
        textViewTimeReal.setText(list.get(i).getDate());

        TextView textViewTimeApprove = (TextView) view.findViewById(R.id.textViewTimeApprove);
        textViewTimeApprove.setText(list.get(i).getDateApprove());

        final TextView textViewPeople = (TextView) view.findViewById(R.id.textViewPeople);
        final TextView textViewOfficer = (TextView) view.findViewById(R.id.textViewOfficer);

        new GraphRequest(
                AccessToken.getCurrentAccessToken(), "/" + list.get(i).getUserId(), null, HttpMethod.GET, new GraphRequest.Callback() {
            public void onCompleted(GraphResponse response) {
                try {
                    textViewPeople.setText(response.getJSONObject().getString("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        ).executeAsync();

        if (list.get(i).getStatus() > 0) {
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(), "/" + list.get(i).getOfficerId(), null, HttpMethod.GET, new GraphRequest.Callback() {
                public void onCompleted(GraphResponse response) {
                    try {
                        textViewOfficer.setText("รับโดย " + response.getJSONObject().getString("name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            ).executeAsync();
        }
        else {
            textViewOfficer.setVisibility(View.GONE);
            textViewTimeApprove.setVisibility(View.GONE);
        }

        view.setBackgroundColor(Color.parseColor(list.get(i).getColor()));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity.getApplicationContext(), AddActivity.class);
                intent.putExtra("aid", list.get(i).getId());
                activity.startActivity(intent);
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                /** แรกแอป Facebook หรือ browser สำหรับใช้แชร์ลิ้งค์ที่ได้จาก server **/
                ShareDialog shareDialog = new ShareDialog(activity);
                if (shareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent content = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse("http://batmasterio.com:8888/emergency.php?id=" + list.get(i).getId()))
                            .build();

                    shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
                }
                else {
                    Toast.makeText(context, "ไม่สามารถแชร์ได้", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });

        return view;
    }
}
