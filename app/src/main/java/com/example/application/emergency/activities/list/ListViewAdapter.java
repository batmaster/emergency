package com.example.application.emergency.activities.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.application.emergency.R;
import com.example.application.emergency.services.ReadableTime;

import java.util.ArrayList;

/**
 * Created by batmaster on 4/16/2017 AD.
 */

public class ListViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ListModel> list;

    public ListViewAdapter(Context context, ArrayList<ListModel> list) {
        this.context = context;
        this.list = list;
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(view == null) {
            view = mInflater.inflate(R.layout.listview_row, viewGroup, false);
        }

        TextView textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
        textViewTitle.setText(list.get(i).getTitle());

        TextView textViewTime = (TextView) view.findViewById(R.id.textViewTime);
        textViewTime.setText(ReadableTime.get(list.get(i).getDate()));

        TextView textViewPeople = (TextView) view.findViewById(R.id.textViewPeople);
        textViewPeople.setText(list.get(i).getPeople());

        TextView textViewOfficer = (TextView) view.findViewById(R.id.textViewOfficer);
        textViewOfficer.setText(list.get(i).getOfficer());
        if (list.get(i).getOfficer() == "null") {
            textViewOfficer.setVisibility(View.INVISIBLE);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return view;
    }
}
