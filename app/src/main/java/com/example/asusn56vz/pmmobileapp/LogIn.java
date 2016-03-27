package com.example.asusn56vz.pmmobileapp;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Asus N56VZ on 27/12/2015.
 */
public class LogIn extends DialogFragment {
    private EditText ip;
    private EditText port;
    private TextView login;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.log_in_screen, container, false);
        MainActivity activity = (MainActivity) getActivity();
        ip = (EditText) v.findViewById(R.id.ip);
        port = (EditText) v.findViewById(R.id.port);
        login = (TextView) v.findViewById(R.id.log_in);
        ip.setText(String.valueOf(activity.ip));
        port.setText(String.valueOf(activity.port));

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).executeFunction(ip.getText().toString(),port.getText().toString());
                LogIn.this.dismiss();
            }
        });

        return v;
    }
}
