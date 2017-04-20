package com.example.application.emergency.activities.list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.application.emergency.R;
import com.example.application.emergency.activities.add.AddActivity;
import com.example.application.emergency.services.ReadableTime;

import java.util.ArrayList;

/**
 * Created by batmaster on 4/16/2017 AD.
 */

public class ListViewAdapter extends BaseAdapter {

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
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(view == null) {
            view = mInflater.inflate(R.layout.listview_row, viewGroup, false);
        }

        TextView textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
        textViewTitle.setText(list.get(i).getTitle());

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

        return view;
    }
}
