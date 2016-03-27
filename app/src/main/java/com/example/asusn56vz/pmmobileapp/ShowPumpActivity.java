package com.example.asusn56vz.pmmobileapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * Created by Asus N56VZ on 12/12/2015.
 */
public class ShowPumpActivity extends FragmentActivity {
    MainLayout mLayout;
    JSONObject jsonObj;
    FragmentLayout fragment;

    SeekBar aver_temp;

    LinearLayout auto;
    LinearLayout timer;
    RelativeLayout outdoor_check_layout;


    TextView save;
    TextView apply;
    TextView hintText;
    TextView hintText2;
    TextView icon_back_main;
    TextView icon_outdoor;
    TextView icon_setting;
    TextView icon_hintScreen;
    TextView outdoor_check_text;
    TextView resultMaxText;
    TextView resultMinText;
    TextView[] days;


    EditText hourText;
    EditText minuteText;
    Spinner type_soil;
    Spinner type_plant;

    View viewSetting;
    View viewHint;

    Setting setting;
    GetDate get_date;
    GetData get_data;

    int color = 0;
    int order;
    int current_mode;
    int safety;
    int weekly;
    int weekly_icon;
    int min;
    int max;
    int max_timemode;
    int hour;
    int minutes;


    int outdoor_check;
    int averageTempValue;
    int result_max_hint;
    int result_min_hint;
    int hint_position = 0;

    double[] soil_value ={1.2,1.15,1.1,1.05,1,0.95};
    double[] plant_value ={0.75,0.8,0.8,0.75,0.9,1,0.9};
    String[] time_token;


    String[] token;
    String ip;
    String port;

    boolean retake;

    FragmentManager fm;
    FragmentTransaction ft;
    Handler h;
    Runnable r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayout = (MainLayout) inflater.inflate(
                R.layout.show_pump, null);
        setContentView(mLayout);
        ip = getIntent().getStringExtra("ip");
        port = getIntent().getStringExtra("port");

        viewSetting = (View) findViewById(R.id.settingLayout);
        viewHint = (View) findViewById(R.id.hintLayout);

        auto = (LinearLayout) viewSetting.findViewById(R.id.auto);
        timer = (LinearLayout) viewSetting.findViewById(R.id.timer);
        outdoor_check_layout = (RelativeLayout) viewHint.findViewById(R.id.outdoor_layout);

        aver_temp = (SeekBar) viewHint.findViewById(R.id.average_temp);

        hourText = (EditText) findViewById(R.id.hour);
        minuteText = (EditText) findViewById(R.id.minutes);

        save = (TextView) viewSetting.findViewById(R.id.save);
        apply = (TextView) viewHint.findViewById(R.id.apply);
        hintText = (TextView) viewSetting.findViewById(R.id.hint);
        hintText2 = (TextView) viewSetting.findViewById(R.id.hint2);
        icon_back_main = (TextView) mLayout.findViewById(R.id.icon_back_main);

        icon_outdoor = (TextView) viewHint.findViewById(R.id.outdoor_icon);
        icon_setting = (TextView) viewSetting.findViewById(R.id.icon);
        icon_hintScreen = (TextView) viewHint.findViewById(R.id.icon);
        outdoor_check_text = (TextView) viewHint.findViewById(R.id.outdoor_check_text);
        resultMaxText = (TextView) viewHint.findViewById(R.id.result_max);
        resultMinText = (TextView) viewHint.findViewById(R.id.result_min);

        type_soil = (Spinner) viewHint.findViewById(R.id.type_soil);
        type_plant = (Spinner) viewHint.findViewById(R.id.type_plant);

        order = Integer.parseInt(getIntent().getStringExtra("order"));
        outdoor_check = 1;
        setUpBackButton();
        setUpOutDoorCheck();
        setUpAverageTempSeekBar();
        setUpSpinners();

        icon_back_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        RelativeLayout.LayoutParams p = new LayoutParams(55,55);
        p.setMargins(127,28,0,0);
        icon_outdoor.setLayoutParams(p);

        get_date = new GetDate(ShowPumpActivity.this);
        get_date.execute();

        get_data = new GetData();
        get_data.execute();


        if(outdoor_check == 1){
            outdoor_check_text.setText("Yes");
            outdoor_check_text.setPadding(45,18,100,18);
        }
        else{
            outdoor_check_text.setText("No");
            outdoor_check_text.setPadding(110,18,47,18);
        }

        setUpApplyButton();

    }

    public void toggleMenu() {
        h.removeCallbacks(r);
        viewHint.setVisibility(View.GONE);
        viewSetting.setVisibility(View.VISIBLE);
        mLayout.toggleMenu();
        h.postDelayed(r,5000);

    }

    public void setUpDetail(JSONObject j){
        TextView name = (TextView) findViewById(R.id.pump_name);
        ImageView menu = (ImageView) findViewById(R.id.menu_icon);
        try {
            name.setText(j.getString("name"));
            safety = Integer.parseInt(j.getString("safety"));
        }
        catch(JSONException e){
            e.printStackTrace();
        }

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleMenu();
            }
        });
    }
    public String getMoisture() throws JSONException {
        String result = jsonObj.getString("current_moisture");
        return result;
    }
    public String getMode() throws JSONException {
        Log.w("current mode", String.valueOf(current_mode));
        if(current_mode == 0){
            Log.w("on/off",jsonObj.getString("status"));
            if(Integer.parseInt(jsonObj.getString("status")) == 0){
                return "ON";
            }
            else{
                return "OFF";
            }
        }
        else if(current_mode == 1){
            return "AUTO";
        }
        else{
            return "TIMER";
        }
    }
    public String getStt() throws JSONException {
        int value = Integer.parseInt(jsonObj.getString("current_moisture"));
        int status = Integer.parseInt(jsonObj.getString("status"));
        if(status == 1){
            return "Pumping...";
        }
        if(value < 10){
            return "I'm dying!";
        }
        else if(value < 30){
            return "I'm feeling thirsty :(";
        }
        else if(value < 100){
            return "I'm feeling full :D";
        }
        else{
            return "I'm drowning :(";
        }
    }
    public void setUpTimer() throws JSONException{
        LinearLayout timer = (LinearLayout) mLayout.findViewById(R.id.choose_day);
        days = new TextView[8];
        days[7] = (TextView) timer.getChildAt(8);
        weekly = Integer.parseInt(jsonObj.getString("weekly"));
        if(weekly == 1){
            days[7].setBackground(getResources().getDrawable(weekly_icon));
        }
        for(int i=0;i<7;i++){
            days[i] = (TextView) timer.getChildAt(i);
        }
        String date_on = jsonObj.getString("dates_on");
        token = date_on.split(":");

        hourText.setTextColor(getResources().getColor(color));
        minuteText.setTextColor(getResources().getColor(color));
        String time = jsonObj.getString("time_on");
        time_token = time.split(":");
        hour = Integer.parseInt(time_token[0]);
        minutes = Integer.parseInt(time_token[1]);
//        if(hour < 10){
//            time_token[0] = "0" + time_token[0];
//        }
//        if(minutes < 10){
//            time_token[1] = "0" + time_token[1];
//        }

    }

    public void setUpHintButton(){
        ((GradientDrawable)hintText.getBackground()).setColor(getResources().getColor(color));
        ((GradientDrawable)hintText2.getBackground()).setColor(getResources().getColor(color));

        hintText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.menu_toggle = 1;
                viewSetting.setVisibility(View.GONE);
                viewHint.setVisibility(View.VISIBLE);
                resultMinText.setVisibility(View.VISIBLE);
                hint_position = 1;
            }
        });
        hintText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.menu_toggle = 1;
                viewSetting.setVisibility(View.GONE);
                viewHint.setVisibility(View.VISIBLE);
                hint_position = 2;
                resultMinText.setVisibility(View.GONE);
            }
        });
    }

    public void setUpBackButton(){
        TextView backText = (TextView) viewHint.findViewById(R.id.back);
        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.menu_toggle = 1;
                viewSetting.setVisibility(View.VISIBLE);
                viewHint.setVisibility(View.GONE);
            }
        });
    }
    public void setUpApplyButton(){
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hint_position == 1){
                    setting.setAutoSeekBarValue(result_max_hint,result_min_hint);

                }
                else if(hint_position == 2){
                    setting.setTimerMoistureSeekBar(result_max_hint);
                }
                viewSetting.setVisibility(View.VISIBLE);
                viewHint.setVisibility(View.GONE);
            }
        });
    }

    public void setUpSpinners(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getApplicationContext(),R.layout.simple_spinner_item);
        //SpinnerAdapter adapter = new SpinnerAdapter(ShowPumpActivity.this);
        adapter.add("Sand");
        adapter.add("Sandy Loam");
        adapter.add("Loam");
        adapter.add("Silt Loam");
        adapter.add("Clay Loam");
        adapter.add("Clay");
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_soil.setAdapter(adapter);


        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this.getApplicationContext(),R.layout.simple_spinner_item);
        adapter2.add("Grass");
        adapter2.add("Herb");
        adapter2.add("Pot Plant");
        adapter2.add("Seedling");
        adapter2.add("Vegetable");
        adapter2.add("Bush");
        adapter2.add("Flower");
        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_plant.setAdapter(adapter2);

        type_plant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mLayout.menu_toggle = 1;
                calculateResultHint();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        type_soil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mLayout.menu_toggle = 1;
                calculateResultHint();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void setUpHintScreen(){
        RelativeLayout type_soil_layout = (RelativeLayout) viewHint.findViewById(R.id.layout_spinner_soil);
        ((GradientDrawable)type_soil_layout.getBackground()).setColor(getResources().getColor(color));


        RelativeLayout type_plant_layout = (RelativeLayout) viewHint.findViewById(R.id.layout_spinner_plant);
        ((GradientDrawable)type_plant_layout.getBackground()).setColor(getResources().getColor(color));

        ((GradientDrawable)icon_setting.getBackground()).setColor(getResources().getColor(color));
        ((GradientDrawable)icon_hintScreen.getBackground()).setColor(getResources().getColor(color));

    }

    public void setUpAverageTempSeekBar(){
        aver_temp.setProgress(0);
        aver_temp.setMax(40);
        ((TextView)this.findViewById(R.id.temp_text)).setText("Average temperature: " + String.valueOf(averageTempValue) + " degree");
        aver_temp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            TextView text = (TextView) ShowPumpActivity.this.findViewById(R.id.temp_text);
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mLayout.menu_toggle = 1;
                averageTempValue = i;
                text.setText("Average temperature: " + String.valueOf(averageTempValue) + " degree");
                calculateResultHint();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void calculateResultHint(){
        double result_max_standard = 80.0;
        double result_min_standard = 20.0;
        double ot;
        if(outdoor_check == 1){
            ot = 0.9;
        }
        else{
            ot = 0.7;
        }

        double tt = (averageTempValue*2 + 30.0)/100.0;
        double percent = ot * tt * (soil_value[type_soil.getSelectedItemPosition()]) * (plant_value[type_plant.getSelectedItemPosition()]);
        result_max_hint = (int) (percent*result_max_standard);
        result_min_hint = (int) (percent*result_min_standard*1.2);

        if(result_max_hint > 120){
            result_max_hint = 120;
        }
        if(result_min_hint < 10){
            result_min_hint = 10;
        }
        resultMinText.setText(String.valueOf(result_min_hint) + "%");
        resultMaxText.setText(String.valueOf(result_max_hint) + "%");

    }

    public void setUpOutDoorCheck(){
        outdoor_check_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleYesNo();
                calculateResultHint();
            }
        });
    }

    public void toggleYesNo(){
        if(outdoor_check == 1){
            outdoor_check = 0;
            icon_outdoor.startAnimation(AnimationUtils.loadAnimation(this.getApplicationContext(),R.anim.move_left));
            outdoor_check_text.setText("No");
            outdoor_check_text.setPadding(110,18,45,18);

        }
        else{
            outdoor_check = 1;
            icon_outdoor.startAnimation(AnimationUtils.loadAnimation(this.getApplicationContext(),R.anim.move_right));
            outdoor_check_text.setText("Yes");
            outdoor_check_text.setPadding(45,18,100,18);
        }
    }

    public class GetData extends AsyncTask<String, String, String> {

        // Progress Dialog
        private ProgressDialog pDialog;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        /*Request get Json Object from URL */
        protected String doInBackground(String ...args) {
            String result = getData();
            if(isCancelled()){

                result = "fail";
            }
            Log.w("canceled?",String.valueOf(isCancelled()));
            return result;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * *
         */
        protected void onPostExecute(final String file_url) {
            mLayout.getting_data = 0;
            if(file_url.equalsIgnoreCase("success")) {
                outdoor_check_text.setTextColor(getResources().getColor(color));
                resultMinText.setTextColor(getResources().getColor(color));
                resultMaxText.setTextColor(getResources().getColor(color));
                ((GradientDrawable)icon_outdoor.getBackground()).setColor(getResources().getColor(color));
                ((GradientDrawable)apply.getBackground()).setColor(getResources().getColor(color));
                aver_temp.getThumb().setColorFilter(getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);
                ((FrameLayout) findViewById(R.id.fragment_pump)).setBackgroundColor(getResources().getColor(color));

                setUpHintButton();
                setUpHintScreen();

                mLayout.setBackgroundColor(getResources().getColor(color));
                RelativeLayout title = (RelativeLayout) mLayout.findViewById(R.id.title);
                title.setBackgroundColor(getResources().getColor(color));


                if(retake == true){
                    fragment.removeRunnable();
                    fm.popBackStack();
                }

                fragment = new FragmentLayout();

                fm = ShowPumpActivity.this.getSupportFragmentManager();
                ft = fm.beginTransaction();
                ft.addToBackStack(null);
                ft.add(R.id.fragment_pump, fragment);
                if(!isCancelled()) {
                    ft.commit();
                }

                setUpDetail(jsonObj);
                try {
                    setUpTimer();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(!mLayout.isMenuShown()) {
                    setting = new Setting((SeekBar) ShowPumpActivity.this.findViewById(R.id.seek_safe),
                            (SeekBar) ShowPumpActivity.this.findViewById(R.id.seek_mode),
                            (SeekBar) ShowPumpActivity.this.findViewById(R.id.maxtime_mode),
                            save, (TextView) ShowPumpActivity.this.findViewById(R.id.safety_text), days, token,
                            ShowPumpActivity.this, weekly, current_mode, safety, min, max, max_timemode,hour,minutes);
                    mLayout.menu_toggle = 0;
                    viewHint.setVisibility(View.GONE);
                    viewSetting.setVisibility(View.VISIBLE);

                }
                else{
                    setting.changeThumbs();
                    mLayout.menu_toggle = 1;

                    LayoutParams params = new LayoutParams(55, 55);
                    params.setMargins(127,28,0,0);
                    icon_outdoor.setLayoutParams(params);

                }

            }
            else{
                Toast.makeText(ShowPumpActivity.this.getApplicationContext(),"Sorry! Can't retrieve data from server",Toast.LENGTH_LONG).show();
            }

            r = new Runnable() {
                @Override
                public void run(){
                    if(mLayout.getCurrentMenuState() == MainLayout.MenuState.SHOWING || mLayout.getCurrentMenuState() == MainLayout.MenuState.HIDING ){
                        try {
                            Thread.sleep(2500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mLayout.getting_data = 1;
                    get_data = new GetData();
                    get_data.execute();
                    retake = true;
                    if(file_url.equalsIgnoreCase("success")) {
                        setting.checkIfModified();
                    }
                }
            };
            h = new Handler();
            h.postDelayed(r, 5000);
        }
    }
    String getData(){
        String result = "fail";
        BufferedReader in = null;
        String URL = "http://"+ ip + ":" + port +"/watering/api/";
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
            result = sBuilder.toString();

            JSONObject jObj = new JSONObject(result);
            jsonObj = new JSONObject(jObj.getString(Integer.toString(order+1)));
            current_mode = Integer.parseInt(jsonObj.getString("mode"));
            int value = 0;
            try {
                value = Integer.parseInt(jsonObj.getString("current_moisture"));
                if(value < 10){
                    color = R.color.red_embedded;
                    weekly_icon = R.drawable.check_icon1;
                }
                else if(value < 30){
                    color = R.color.orange_embedded;
                    weekly_icon = R.drawable.check_icon2;
                }
                else if(value <100){
                    color = R.color.green_embedded;
                    weekly_icon = R.drawable.check_icon3;
                }
                else{
                    color = R.color.blue_embedded;
                    weekly_icon = R.drawable.check_icon4;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            min = Integer.parseInt(jsonObj.getString("min"));
            max= Integer.parseInt(jsonObj.getString("max"));
            max_timemode = Integer.parseInt(jsonObj.getString("max_timemode"));

            result = "success";

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.d("BBB", e.toString());
                }
            }
        }
        return result;
    }
    public void justUpdate(){
        toggleMenu();
        retake = true;
        h.removeCallbacks(r);
        h.postDelayed(r,0);
    }
    @Override
    public void onDestroy(){
        get_data.cancel(true);
        h.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public void onPause(){
        get_data.cancel(true);
        h.removeCallbacksAndMessages(null);
        super.onPause();
    }

    @Override
    public void onStop(){
        get_data.cancel(true);
        h.removeCallbacksAndMessages(null);
        super.onStop();
    }


    @Override
    public void onResume(){
        h = new Handler();
        h.postDelayed(r, 0);
        super.onResume();
    }

    @Override
    public void onBackPressed(){
        get_data.cancel(true);
        h.removeCallbacks(r);
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        super.onBackPressed();
        //Toast.makeText(ShowPumpActivity.this.getApplicationContext(),"Back to the main page ....",Toast.LENGTH_SHORT).show();
    }
}
