package com.example.asusn56vz.pmmobileapp;

import android.app.ProgressDialog;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asus N56VZ on 13/12/2015.
 */
public class FragmentLayout extends Fragment {
    TextView moisture;
    TextView mode;
    TextView stt;
    TextView day_next;
    TextView time_set;
    TextView currentTime;
    TextView currentDay;
    LinearLayout timeMode;


    ShowPumpActivity activity;
    private int status__on_off;
    String mode_status;
    String[] day = {"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};

    Handler h = new Handler();
    Runnable r;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        activity = (ShowPumpActivity) getActivity();
        View view = inflater.inflate(R.layout.fragmentlayout, container, false);
        view.setBackgroundColor(activity.getResources().getColor(activity.color));

        moisture = (TextView) view.findViewById(R.id.pump_moisture);
        mode = (TextView) view.findViewById(R.id.mode);
        stt = (TextView) view.findViewById(R.id.stt);
        timeMode = (LinearLayout) view.findViewById(R.id.timerMode);
        currentTime = (TextView) timeMode.findViewById(R.id.current_time);
        currentDay = (TextView) timeMode.findViewById(R.id.current_day);
        time_set = (TextView) timeMode.findViewById(R.id.time_set);
        day_next = (TextView) timeMode.findViewById(R.id.day_next);
        try {
            moisture.setText(activity.getMoisture());
            if(!activity.getMode().equalsIgnoreCase("Timer")) {
                mode.setVisibility(View.VISIBLE);
                timeMode.setVisibility(View.GONE);
                mode.setText(activity.getMode());
            }
            else{
                mode.setVisibility(View.GONE);
                timeMode.setVisibility(View.VISIBLE);
                String time = activity.time_token[0] + ":" + activity.time_token[1];
                time_set.setText(time);
                currentDay.setText(day[activity.get_date.getDate()].toUpperCase());
                boolean ifTimeSet = false;
                for(int i=0;i<day.length;i++){
                    Log.w("day",String.valueOf(activity.get_date.getDate()));

                    int j = (i + activity.get_date.getDate()) % 7;

                    Log.w("J value", String.valueOf(j));
                    if(j == activity.get_date.getDate() &&  activity.token[j].equalsIgnoreCase("1") ){
                        if((Integer.parseInt(activity.get_date.getHour()) < Integer.parseInt(activity.time_token[0])) ||
                          ((Integer.parseInt(activity.get_date.getHour()) == Integer.parseInt(activity.time_token[0])) &&
                            (Integer.parseInt(activity.get_date.getMinute()) < Integer.parseInt(activity.time_token[1]))
                            )){
                            day_next.setText("Today");
                            ifTimeSet = true;
                            break;
                        }
                        else{
                            day_next.setText("Next Week");
                            ifTimeSet = true;
                        }

                    }
                    else if(j == ((activity.get_date.getDate() + 1) % 7) && activity.token[j].equalsIgnoreCase("1") ){
                        day_next.setText("Tomorrow");
                        ifTimeSet = true;
                        break;
                    }
                    else if(activity.token[j].equalsIgnoreCase("1")){
                        day_next.setText(day[j]);
                        ifTimeSet = true;
                        break;
                    }
                }

                if(ifTimeSet == false){
                    day_next.setText("Timer was not set");
                    time_set.setVisibility(View.GONE);
                    ((ImageView) view.findViewById(R.id.clock)).setVisibility(View.GONE);
                }
                else{
                    time_set.setVisibility(View.VISIBLE);
                    ((ImageView) view.findViewById(R.id.clock)).setVisibility(View.VISIBLE);
                }
                r = new Runnable() {
                    @Override
                    public void run() {
                        currentTime.setText(activity.get_date.getHour() + ":" + activity.get_date.getMinute() + ":" + activity.get_date.getSecond());
                        h.postDelayed(this,1000);
                    }
                };
                h.postDelayed(r, 0);

            }
            Log.w("mode what?",mode.getText().toString());
            mode.setTextColor(activity.getResources().getColor(activity.color));
            if(mode.getText().toString().equalsIgnoreCase("OFF")){
                stt.setText("Pumping...");
            }
            else {
                stt.setText(activity.getStt());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mode_status = mode.getText().toString();
        if (mode_status.equalsIgnoreCase("on") || mode_status.equalsIgnoreCase("off")) {
            mode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mode_status = mode.getText().toString();
                    if (mode_status.equalsIgnoreCase("on")) {
                        status__on_off = 1;
                        stt.setText("Pumping...");
                    } else {
                        status__on_off = 0;
                        try {
                            stt.setText(activity.getStt());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    new UpdateStatus().execute();

                }

            });
        }
        return view;
    }
    public void changeBackground(){
        this.getView().setBackgroundColor(activity.getResources().getColor(activity.color));
    }

    void removeRunnable(){
        h.removeCallbacks(null);
    }

    class UpdateStatus extends AsyncTask<String, String, String> {
        // Progress Dialog
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage("Reading File ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         */
        protected String doInBackground(String... args) {
            HttpClient httpclient = new DefaultHttpClient();
            //HttpPost httppost = new HttpPost("http://10.247.199.198/watering/api/update/ajax.php");
            HttpPost httppost = new HttpPost("http://"+ activity.ip + ":" + activity.port +"/watering/api/update/ajax.php");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(activity.order + 1)));
                nameValuePairs.add(new BasicNameValuePair("status", String.valueOf(status__on_off)));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                Log.w("respone", EntityUtils.toString(response.getEntity()));
                return "success";

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                Log.w("error", "client");
                return "fail";
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.w("error", "ioerror");
                return "fail";
            }
        }

        /**
         * After completing background task Dismiss the progress dialog
         * *
         */
        protected void onPostExecute(String file_url) {
            if (file_url.equalsIgnoreCase("success")) {
                if (status__on_off == 0)
                    mode.setText("ON");
                else
                    mode.setText("OFF");
            }
            pDialog.dismiss();
        }



     }
}
