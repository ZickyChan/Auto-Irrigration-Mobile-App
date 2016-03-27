package com.example.asusn56vz.pmmobileapp;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Asus N56VZ on 30/12/2015.
 */
public class GetDate  extends AsyncTask<Void, Long, Boolean> {
    private ShowPumpActivity activity;
    private Boolean result;
    private int hour;
    private int minute;
    private int second;
    private int day;

    String hourText;
    String minuteText;
    String secondText;

    private TextView time;

    public GetDate(ShowPumpActivity m){

        activity = m;
        time = (TextView) activity.viewSetting.findViewById(R.id.current_time);
    }

    protected Boolean doInBackground(Void... params) {

        BufferedReader in = null;
        String URL = "http://"+ activity.ip + ":" + activity.port +"/watering/api/retrieve/time.php";
        try {
            // HttpClient is more then less deprecated. Need to change to URLConnection
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost(URL);
            //httpPost.setEntity(new UrlEncodedFormEntity(param));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();

            // Read content & Log
            InputStream inputStream = httpEntity.getContent();

            BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
            StringBuilder sBuilder = new StringBuilder();

            String line = null;
            while ((line = bReader.readLine()) != null) {
                sBuilder.append(line + "\n");
            }

            inputStream.close();
            String data_retrieve = sBuilder.toString();

            JSONObject jObj = new JSONObject(data_retrieve);
            Log.w("data retrieved",data_retrieve);
            day = Integer.parseInt(jObj.getString("date"));
            hour = Integer.parseInt(jObj.getString("hour"));
            minute = Integer.parseInt(jObj.getString("minute"));
            second = Integer.parseInt(jObj.getString("second"));
            result = true;
        }
        catch(Exception e){
            e.printStackTrace();
            result = false;
        }
        Log.w("execute?", String.valueOf(result));
        return result;
    }
    protected void onPostExecute(Boolean result) {
        if (result) {
            Log.w("in here", "get true");
            final Handler timerHandler = new Handler();
            Runnable r = new Runnable() {
                @Override
                public void run(){
                    second++;
                    if(second == 60){
                        minute++;
                        second = 0;
                    }
                    if(minute == 60){
                        hour++;
                        minute = 0;
                    }
                    if(hour == 24){
                        day++;
                        hour = 0;
                    }
                    if(day == 7){
                        day = 0;
                    }
                    hourText = String.valueOf(hour);
                    minuteText = String.valueOf(minute);
                    secondText = String.valueOf(second);

                    if(hour <10){
                        hourText = "0" + hourText;
                    }
                    if(minute <10){
                        minuteText = "0" + minuteText;
                    }
                    if(second <10){
                        secondText = "0" + secondText;
                    }
                    time.setText(hourText + ":" + minuteText + ":" + secondText);
                    timerHandler.postDelayed(this,1000);
                }
            };
            timerHandler.postDelayed(r,0);


        } else {
            Log.w("in here", "get false");
        }
    }

    public int getDate(){
        return day;
    }
    public String getHour(){
        return hourText;
    }
    public String getMinute(){
        return minuteText;
    }
    public String getSecond(){
        return secondText;
    }
}
