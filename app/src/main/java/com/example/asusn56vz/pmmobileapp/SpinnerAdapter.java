package com.example.asusn56vz.pmmobileapp;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Asus N56VZ on 28/12/2015.
 */
public class SpinnerAdapter extends ArrayAdapter<String> {
    Context mContext;
    int color;

    public SpinnerAdapter(ShowPumpActivity activity) {
        super(activity.getApplicationContext(), R.layout.pumpdetail);
        mContext = activity.getApplicationContext();
        color = activity.color;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.simple_spinner_item, parent, false);
        rowView.setBackgroundColor(mContext.getResources().getColor(color));

        return rowView;
    }
}
