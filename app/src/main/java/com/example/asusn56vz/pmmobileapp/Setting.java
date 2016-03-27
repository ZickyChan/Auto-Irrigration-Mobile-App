package com.example.asusn56vz.pmmobileapp;

import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
* Created by Asus N56VZ on 04/12/2015.
*/
public class Setting {

    private SeekBar safety;
    private SeekBar mode;
    private SeekBar time_mode;
    private RangeSeekBar minMax;


    private TextView save;
    private TextView safety_textView;
    private TextView minMaxTextView;
    private TextView timeModeTextView;
    private TextView[] days;


    private EditText hourText;
    private EditText minuteText;

    private String[] tokenDays;
    private ShowPumpActivity activity;


    int mode_progress = 0;
    int safe_progress = 0;
    int weekly = 0;
    int min;
    int max;
    int hour;
    int minutes;
    int max_timemode;

    int real_height;
    private boolean changed;
    boolean updated = false;

    public Setting(SeekBar safe, SeekBar mode, SeekBar time_mode, TextView save, TextView safe_text,
                   TextView[] daysArray, String[] token, ShowPumpActivity s,
                   int weekly, int current_mode, int safe_progress, int min, int max, int max_time, int hour, int minute){
        safety = safe;
        this.mode = mode;
        this.time_mode = time_mode;
        this.activity = s;
        this.save = save;
        safety_textView = safe_text;
        mode_progress = current_mode;
        tokenDays = new String[token.length];
        for(int i = 0; i<token.length;i++){
            tokenDays[i] = token[i];
        }
        this.safe_progress = safe_progress;
        this.weekly = weekly;
        days = daysArray;
        this.min = min;
        this.max = max;
        this.max_timemode = max_time;
        timeModeTextView = (TextView) activity.findViewById(R.id.maxtime_turnoff);
        hourText = (EditText) activity.findViewById(R.id.hour);
        minuteText = (EditText) activity.findViewById(R.id.minutes);
        this.hour = hour;
        this.minutes = minute;
        real_height = activity.mLayout.getHeight();

        changed = false;
        save.setTextColor(Color.parseColor("#FFFFFF"));
        ((GradientDrawable) save.getBackground()).setColor(Color.parseColor("#444444"));
        setUp();
    }
    public void setUp(){
        setUpModeSeekBar();
        setUpSafeSeekBar();
        setUpRangeSeekBar();
        setUpMaxTimeSeekBar();
        setUpTimer();
        setUpTextViewListener();
        setUpEditTime();
        setUpSave();

    }
    public void setUpRangeSeekBar(){
        if(activity.retake != true) {
            //Create range seekbar
            LinearLayout a = (LinearLayout) activity.findViewById(R.id.auto);
            minMaxTextView = new TextView(activity.getApplicationContext());
            minMaxTextView.setText("Min: " + min + "% - Max: " + max + "%");
            minMaxTextView.setTextAppearance(activity.getApplicationContext(),R.style.fontSerIfText);
            minMax = new RangeSeekBar<Integer>(10, 120, activity);
            minMax.setId(R.id.minMaxRange);
            minMaxTextView.setId(R.id.minMaxTextView);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(25, 35, 0, 35);
            minMax.setLayoutParams(lp);

            lp.setMargins(0, 30, 0, 0);
            minMaxTextView.setLayoutParams(lp);

            a.addView(minMax);
            a.addView(minMaxTextView);
        }
        else{
            minMax = (RangeSeekBar) activity.findViewById(R.id.minMaxRange);
            minMaxTextView = (TextView) activity.findViewById(R.id.minMaxTextView);
            minMax.changeThumb();
        }


        minMax.setSelectedMaxValue(max);
        minMax.setSelectedMinValue(min);
        minMax.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                activity.mLayout.menu_toggle = 1;
                min = minValue;
                max = maxValue;
                minMaxTextView.setText("Min: " + minValue + "% - Max: " + maxValue + "%");
                checkIfModified();
            }
        });
    }
    public void setUpModeSeekBar(){
        if(mode_progress == 0){
            activity.timer.setVisibility(View.GONE);
            activity.auto.setVisibility(View.GONE);
        }
        else if(mode_progress == 1){
            activity.timer.setVisibility(View.GONE);
            activity.auto.setVisibility(View.VISIBLE);
        }
        else{
            activity.timer.setVisibility(View.VISIBLE);
            activity.auto.setVisibility(View.GONE);
        }
        mode.setProgress(mode_progress);
        mode.setMax(2);
        mode.getThumb().setColorFilter(activity.getResources().getColor(activity.color), PorterDuff.Mode.SRC_ATOP);
        mode.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                mode_progress = progresValue;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                activity.mLayout.menu_toggle = 1;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                checkIfModified();
                if(mode_progress == 1){
                    activity.auto.setVisibility(LinearLayout.VISIBLE);
                    activity.timer.setVisibility(LinearLayout.GONE);
                }
                else if(mode_progress == 2){
                    activity.timer.setVisibility(LinearLayout.VISIBLE);
                    activity.auto.setVisibility(LinearLayout.GONE);
                }
                else{
                    activity.timer.setVisibility(LinearLayout.GONE);
                    activity.auto.setVisibility(LinearLayout.GONE);
                }
            }
        });
    }
    public void setUpSafeSeekBar(){
        safety.setProgress(safe_progress);
        safety.setMax(10);
        safety.getThumb().setColorFilter(activity.getResources().getColor(activity.color), PorterDuff.Mode.SRC_ATOP);
        safety_textView.setText("Turn off after " + safe_progress + " minutes");
        safety.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                safe_progress = progresValue;
                safety_textView.setText("Turn off after " + safe_progress + " minutes");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                checkIfModified();
            }
        });
    }

    public void setUpMaxTimeSeekBar(){
        timeModeTextView.setText("Turn off after reaching " + max_timemode + "%");
        time_mode.setProgress(max_timemode);
        time_mode.setMax(200);
        time_mode.getThumb().setColorFilter(activity.getResources().getColor(activity.color), PorterDuff.Mode.SRC_ATOP);
        time_mode.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                max_timemode = progresValue;
                timeModeTextView.setText("Turn off after reaching " + max_timemode + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                activity.mLayout.menu_toggle = 1;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                checkIfModified();
            }
        });
    }
    public void setUpTimer(){
        for (int i=0;i<tokenDays.length;i++){
            if(tokenDays[i].equalsIgnoreCase("1")){
                days[i].setTextColor(Color.parseColor("#000000"));
                ((GradientDrawable)days[i].getBackground()).setColor(activity.getResources().getColor(activity.color));
            }
            else{
                days[i].setTextColor(Color.parseColor("#FFFFFF"));
                ((GradientDrawable)days[i].getBackground()).setColor(Color.parseColor("#444444"));
            }
        }
        String textHour = String.valueOf(hour);
        String textMinute = String.valueOf(minutes);
        if(hour < 10){
            textHour = "0" + textHour;
        }
        if(minutes < 10){
            textMinute = "0" + textMinute;
        }
        hourText.setText(textHour);
        minuteText.setText(textMinute);
    }

    public void setUpTextViewListener(){
        for(int i=0; i<days.length; i++){
            days[i].setOnClickListener(new DaysClickListener(i));
        }
    }
    public void setUpEditTime(){
        hourText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                activity.mLayout.menu_toggle = 1;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                activity.viewSetting.setLayoutParams(new LinearLayout.LayoutParams(activity.viewSetting.getWidth(),real_height));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                activity.viewSetting.setLayoutParams(new LinearLayout.LayoutParams(activity.viewSetting.getWidth(),real_height));

                activity.mLayout.menu_toggle = 1;
                if(!hourText.getText().toString().equalsIgnoreCase("")) {
                    hour = Integer.parseInt(hourText.getText().toString());
                    if(hour > 23){
                        hour = 23;
                        hourText.setText(String.valueOf(hour));
                    }
                }else{
                    hour = 0;
                    hourText.setText("00");
                }
                checkIfModified();
            }
        });
        minuteText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                activity.mLayout.menu_toggle = 1;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                activity.viewSetting.setLayoutParams(new LinearLayout.LayoutParams(activity.viewSetting.getWidth(),real_height));

                activity.mLayout.menu_toggle = 1;
                if(!minuteText.getText().toString().equalsIgnoreCase("")) {
                    minutes = Integer.parseInt(minuteText.getText().toString());
                    if(minutes > 59){
                        minutes = 59;
                        minuteText.setText(String.valueOf(minutes));
                    }
                }
                else{
                    minutes = 0;
                    minuteText.setText("00");
                }
                checkIfModified();
            }
        });
    }
    public void setUpSave(){
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(changed == true){
                    if(hour >= 0 && hour < 24 && minutes < 60 && minutes >= 0){
                        changed = false;
                        save.setTextColor(Color.parseColor("#FFFFFF"));
                        ((GradientDrawable) save.getBackground()).setColor(Color.parseColor("#444444"));
                        Toast.makeText(activity,"Updating and changing ...",Toast.LENGTH_LONG);
                        new UpdateData().execute();
                        activity.justUpdate();
                    }
                }
            }
        });
    }

    class UpdateData extends AsyncTask<String, String, String> {
        // Progress Dialog
        private ProgressDialog pDialog;

        // url to create new product

        // JSON Node names
        private  String result = "fail";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        /**
         * Creating product
         */
        protected String doInBackground(String... args) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://"+ activity.ip + ":" + activity.port +"/watering/api/update/setting.php");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
//                nameValuePairs.add(new BasicNameValuePair("id","2"));
//                nameValuePairs.add(new BasicNameValuePair("min","50"));
                nameValuePairs = addParameterForList(nameValuePairs);
                Log.w("list",nameValuePairs.toString());
//                Log.w("id",nameValuePairs.get(0).getValue());
//                Log.w("time_on",nameValuePairs.get(5).getValue());
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                Log.w("respone", EntityUtils.toString(response.getEntity()));
                result="success";

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                Log.w("error", "client");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.w("error","ioerror");
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * *
         */
        protected void onPostExecute(String file_url) {
            if(result.equalsIgnoreCase("success")){

            }
            else{}
        }
    }

    public class DaysClickListener implements View.OnClickListener{
        int i;
        public DaysClickListener(int index){
            i = index;
        }
        public void toggleDays(int i){
            if(i==7){
                if(weekly == 1) {
                    weekly = 0;
                    days[i].setBackground(activity.getResources().getDrawable(R.drawable.check_icon));
                }
                else{
                    weekly = 1;
                    days[i].setBackground(activity.getResources().getDrawable(activity.weekly_icon));
                }
            }
            else {
                if (tokenDays[i].equalsIgnoreCase("1")) {
                    tokenDays[i] = "0";
                    days[i].setTextColor(Color.parseColor("#FFFFFF"));
                    ((GradientDrawable) days[i].getBackground()).setColor(Color.parseColor("#444444"));
                } else {
                    tokenDays[i] = "1";
                    days[i].setTextColor(Color.parseColor("#000000"));
                    ((GradientDrawable) days[i].getBackground()).setColor(activity.getResources().getColor(activity.color));
                }
            }
        }

        @Override
        public void onClick(View view) {
            toggleDays(i);
            checkIfModified();
        }
    }
    public void checkIfModified(){
        if(safe_progress == activity.safety && mode_progress == activity.current_mode){
            changed = false;
            if(mode_progress == 1){
                if (min != activity.min || max != activity.max){
                    changed = true;
                }
            }
            else if(mode_progress == 2){
                if(weekly != activity.weekly){
                    changed = true;
                }
                for(int i=0;i<7;i++){

                    if(!(tokenDays[i].equalsIgnoreCase(activity.token[i]))){
                        changed = true;
                        break;
                    }
                }
                if(max_timemode != activity.max_timemode){
                    changed = true;
                }
                if(hour != activity.hour || minutes != activity.minutes){
                    changed = true;
                }
                if(max_timemode != activity.max_timemode){
                    changed = true;
                }
            }
        }else{
            changed = true;
        }

        if(changed == true){
            save.setTextColor(Color.parseColor("#000000"));
            ((GradientDrawable) save.getBackground()).setColor(activity.getResources().getColor(activity.color));
        }
        else{
            save.setTextColor(Color.parseColor("#FFFFFF"));
            ((GradientDrawable) save.getBackground()).setColor(Color.parseColor("#444444"));
        }
    }

    public String combineDays(String[] token){
        String result = "";
        for(int i=0; i<token.length; i++){
            result = result + ":" +  token[i] ;
        }
        result = result.substring(1);
        return result;
    }
    public String combineTime(int h, int m){
        String result = String.valueOf(h)+":"+String.valueOf(m);
        return result;
    }

    public List<NameValuePair> addParameterForList(List<NameValuePair> nameValuePairs){
        nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(activity.order + 1)));
        nameValuePairs.add(new BasicNameValuePair("mode",String.valueOf(mode_progress)));
        nameValuePairs.add(new BasicNameValuePair("safety",String.valueOf(safe_progress)));

        if(mode_progress == 0){
            nameValuePairs.add(new BasicNameValuePair("min",String.valueOf(activity.min)));
            nameValuePairs.add(new BasicNameValuePair("max",String.valueOf(activity.max)));
            nameValuePairs.add(new BasicNameValuePair("time_on",combineTime(activity.hour, activity.minutes)));
            nameValuePairs.add(new BasicNameValuePair("dates_on",combineDays(activity.token)));
            nameValuePairs.add(new BasicNameValuePair("max_timemode",String.valueOf(activity.max_timemode)));
            nameValuePairs.add(new BasicNameValuePair("weekly",String.valueOf(activity.weekly)));
        }
        else if (mode_progress == 1){
            nameValuePairs.add(new BasicNameValuePair("min",String.valueOf(min)));
            nameValuePairs.add(new BasicNameValuePair("max",String.valueOf(max)));
            nameValuePairs.add(new BasicNameValuePair("time_on",combineTime(activity.hour,activity.minutes)));
            nameValuePairs.add(new BasicNameValuePair("dates_on",combineDays(activity.token)));
            nameValuePairs.add(new BasicNameValuePair("max_timemode",String.valueOf(activity.max_timemode)));
            nameValuePairs.add(new BasicNameValuePair("weekly",String.valueOf(activity.weekly)));
        }
        else{
            nameValuePairs.add(new BasicNameValuePair("min",String.valueOf(activity.min)));
            nameValuePairs.add(new BasicNameValuePair("max",String.valueOf(activity.max)));
            nameValuePairs.add(new BasicNameValuePair("time_on",combineTime(hour,minutes)));
            nameValuePairs.add(new BasicNameValuePair("dates_on",combineDays(tokenDays)));
            nameValuePairs.add(new BasicNameValuePair("max_timemode",String.valueOf(max_timemode)));
            nameValuePairs.add(new BasicNameValuePair("weekly",String.valueOf(weekly)));
        }
        return nameValuePairs;
    }

    public void setAutoSeekBarValue(int maxValue, int minValue){
        max = maxValue;
        min = minValue;
        minMax.setSelectedMaxValue(max);
        minMax.setSelectedMinValue(min);
        minMaxTextView.setText("Min: " + minValue + "% - Max: " + maxValue + "%");
        checkIfModified();
    }

    public void setTimerMoistureSeekBar(int value){
        max_timemode = value;
        time_mode.setProgress(max_timemode);
        timeModeTextView.setText("Turn off after reaching " + max_timemode + "%");
        checkIfModified();
    }

    public void changeThumbs(){
        minMax.changeThumb();
        minMax.setSelectedMaxValue(max);
        minMax.setSelectedMinValue(min);
        mode.getThumb().setColorFilter(activity.getResources().getColor(activity.color), PorterDuff.Mode.SRC_ATOP);
        safety.getThumb().setColorFilter(activity.getResources().getColor(activity.color), PorterDuff.Mode.SRC_ATOP);
        time_mode.getThumb().setColorFilter(activity.getResources().getColor(activity.color), PorterDuff.Mode.SRC_ATOP);
        for (int i=0;i<tokenDays.length;i++) {
            if (tokenDays[i].equalsIgnoreCase("1")) {
                days[i].setTextColor(Color.parseColor("#000000"));
                ((GradientDrawable) days[i].getBackground()).setColor(activity.getResources().getColor(activity.color));
            }
        }

    }
}
