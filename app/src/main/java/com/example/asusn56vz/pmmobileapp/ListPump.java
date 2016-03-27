package com.example.asusn56vz.pmmobileapp;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Asus N56VZ on 11/12/2015.
 */
public class ListPump extends ArrayAdapter {
    Context mContext;
    JSONObject[] jObj;

    public ListPump(Context context, JSONObject[] j) {
        super(context, R.layout.pumpdetail, j);
        mContext = context;
        jObj = j;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.pumpdetail, parent, false);
        TextView moisture = (TextView) rowView.findViewById(R.id.moisture);
        TextView name = (TextView) rowView.findViewById(R.id.name);
        TextView lastRun = (TextView) rowView.findViewById(R.id.last_run);
        try {
            moisture.setText(jObj[position].getString("current_moisture") + "%");

            ((GradientDrawable)moisture.getBackground()).setColor(Color.parseColor("#000000"));
            name.setText(jObj[position].getString("name"));
            lastRun.setText("Last run at " + jObj[position].getString("last_run"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rowView;
    }
}
