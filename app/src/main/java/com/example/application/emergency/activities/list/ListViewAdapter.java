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
import android.widget.TextView;
import android.widget.Toast;

import com.example.application.emergency.R;
import com.example.application.emergency.activities.add.AddActivity;
import com.example.application.emergency.services.ReadableTime;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

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

        TextView textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
        textViewTitle.setText(list.get(i).getTitle());

        TextView textViewPhone = (TextView) view.findViewById(R.id.textViewPhone);
        textViewPhone.setText(list.get(i).getPhone());

        TextView textViewTime = (TextView) view.findViewById(R.id.textViewTime);
        textViewTime.setText(ReadableTime.get(list.get(i).getDate()));

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
