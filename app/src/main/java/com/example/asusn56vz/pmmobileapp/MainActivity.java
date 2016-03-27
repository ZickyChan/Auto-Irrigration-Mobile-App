package com.example.asusn56vz.pmmobileapp;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
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



public class MainActivity extends ActionBarActivity {
    JSONObject[] jsonArrayObject;
    ListPump list_pump;
    ListView listView;
    SharedPreferences shared;
    String ip;
    String port;
    Handler h;
    Runnable r;

    boolean retake = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView (R.layout.activity_main);
        h = new Handler();
        listView = (ListView) findViewById(R.id.list_pump);
        shared = getSharedPreferences("ip_address",MODE_WORLD_READABLE);

        ip = shared.getString("ip",null);
        port = shared.getString("port",null);
        if(ip!=null){
            new GetData().execute();
        }
        else{
            showDialog();
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class GetData extends AsyncTask<String, String, String> {

        // Progress Dialog
        private ProgressDialog pDialog;
            /**
             * Before starting background thread Show Progress Dialog
             * */
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if(retake == false) {
                    pDialog = new ProgressDialog(MainActivity.this);
                    pDialog.setMessage("Retrieving data..");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();
                }
            }

        /*Request get Json Object from URL */
        protected String doInBackground(String ...args) {

            return getData();
        }

        /**
         * After completing background task Dismiss the progress dialog
         * *
         */
        protected void onPostExecute(String file_url) {
            if(retake == false) {
                // dismiss the dialog once done
                pDialog.dismiss();
            }
            if(file_url.equalsIgnoreCase("success")) {
                retake = true;
                list_pump = new ListPump(MainActivity.this.getApplicationContext(), jsonArrayObject);

                listView.setAdapter(list_pump);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Toast.makeText(MainActivity.this.getApplicationContext(),"Loading activity ....",Toast.LENGTH_LONG).show();
                        Intent foo = new Intent(MainActivity.this, ShowPumpActivity.class);
                        foo.putExtra("pump", jsonArrayObject[i].toString());
                        foo.putExtra("order", String.valueOf(i));
                        foo.putExtra("ip",ip);
                        foo.putExtra("port",port);
                        MainActivity.this.startActivity(foo);

                    }
                });
                r = new Runnable() {
                    @Override
                    public void run(){
                        new GetData().execute();
                    }
                };
                h = new Handler();
                h.postDelayed(r, 5000);
            }
            else{
                Toast.makeText(MainActivity.this.getApplicationContext(),"Can't retrieve data!!!",Toast.LENGTH_SHORT).show();
                showDialog();
            }

        }
    }
    String getData(){
        String result = "fail";
        BufferedReader in = null;
        String URL = "http://" + ip + ":" + port + "/watering/api/";
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
            jsonArrayObject = new JSONObject[jObj.length()];
            for(int i=0;i<jObj.length();i++){
                jsonArrayObject[i] = new JSONObject(jObj.getString(Integer.toString(i+1)));
            }
            result = "success";

        } catch (Exception e) {
            e.printStackTrace();
            result = "fail";
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

    @Override
    public void onDestroy(){
        h.removeCallbacks(r);
        super.onDestroy();
    }
    @Override
    public void onPause(){
        h.removeCallbacks(r);
        super.onPause();
    }

    @Override
    public void onResume(){
        h = new Handler();
        h.postDelayed(r,0);
        super.onResume();
    }

    public void executeFunction(String ip_address, String port_address){
        ip = ip_address;
        port = port_address;
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("ip",ip);
        editor.putString("port",port);
        editor.commit();
        new GetData().execute();
    }

    void showDialog() {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = new LogIn();
        newFragment.show(ft, "dialog");
    }

}
