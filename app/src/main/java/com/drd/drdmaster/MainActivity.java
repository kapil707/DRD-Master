package com.drd.drdmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public String main_url = "https://www.drdweb.co.in/android/Api_track2/";
    public String main_url_webview = "https://www.drdistributor.com/android/Api_mobile_html35/"; // Notification_Service ke page par be alag sau use ho raha ha so plz wha be kar layna change
    String result = "";
    String mainurl = "", page_url1 = "";
    UserSessionManager session;
    String user_session = "", user_password = "",user_altercode="",firebase_token="";

    Database db;
    SQLiteDatabase sql;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        user_session = user.get(UserSessionManager.KEY_USERID);
        user_password = user.get(UserSessionManager.KEY_PASSWORD);
        user_altercode  = user.get(UserSessionManager.KEY_USERALTERCODE);
        firebase_token  = user.get(UserSessionManager.KEY_FIREBASE_TOKEN);

        db = new Database(this);
        sql = db.getWritableDatabase();

        try {
            Cursor tbl_order_done = sql.rawQuery("Select * from tbl_user_loc limit 50", null);
            Toast.makeText(MainActivity.this, "Total rec of : " + tbl_order_done.getCount(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            db.onUpgrade(sql, 1, 2);
            Toast.makeText(MainActivity.this, "Update database", Toast.LENGTH_LONG).show();
        }

        /*String myt="";
        Cursor tbl_user_loc = sql.rawQuery("Select * from tbl_user_loc", null);
        if (tbl_user_loc.getCount() != 0) {
            if (tbl_user_loc.moveToFirst()) {
                do {
                    @SuppressLint("Range")
                    String gettime = tbl_user_loc.getString(tbl_user_loc.getColumnIndex("gettime"));
                    //Toast.makeText(getApplicationContext(), gettime, Toast.LENGTH_LONG).show();
                    myt = myt + "-" + gettime;
                }
                while (tbl_user_loc.moveToNext());
            }
        }*/

        MainActivity ma = new MainActivity();
        mainurl = ma.main_url;
        page_url1 = mainurl + "page_load_update_all_dt/post";


        /*
        TextView t1 =  findViewById(R.id.txt1);
        t1.setText(myt);*/
       try {
            if (session.checkLogin()) {
                finish();
            } else {
                /*Intent in = new Intent();
                in.setClass(MainActivity.this, Home_page.class);
                startActivity(in);
                finish();*/
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        wakeLock.acquire();

        new update_live_track_user().execute();
    }

    String qry = "";
    private class update_live_track_user extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                Cursor tbl_user_loc = sql.rawQuery("Select * from tbl_user_loc limit 50", null);
                if (tbl_user_loc.getCount() != 0) {
                    if (tbl_user_loc.moveToFirst()) {
                        do {
                            @SuppressLint("Range")
                            String latitude = tbl_user_loc.getString(tbl_user_loc.getColumnIndex("latitude"));
                            @SuppressLint("Range")
                            String longitude = tbl_user_loc.getString(tbl_user_loc.getColumnIndex("longitude"));

                            qry+= "{'latitude':'"+latitude+"','longitude':'"+latitude+"'},";
                        }
                        while (tbl_user_loc.moveToNext());
                    }
                }
            }catch (Exception e) {
            }
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            result = "";
            InputStream isr = null;
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(page_url1);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("api_id", "apiidkapil707sharma-kavita-zxy"));

                nameValuePairs.add(new BasicNameValuePair("user_session", user_session));
                nameValuePairs.add(new BasicNameValuePair("user_altercode", user_altercode));
                nameValuePairs.add(new BasicNameValuePair("firebase_token", firebase_token));
                nameValuePairs.add(new BasicNameValuePair("qry", qry));
                nameValuePairs.add(new BasicNameValuePair("submit", "98c08565401579448aad7c64033dcb4081906dcb"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                isr = entity.getContent();
            } catch (Exception e) {
            }
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(isr, "iso-8859-1"), 8);
                StringBuilder stringBuilder = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }
                isr.close();
                result = stringBuilder.toString();
            } catch (Exception e) {
                // TODO: handle exception
                //mProgressDialog.dismiss();
            }
            try {
            } catch (Exception e) {
                // TODO: handle exception
                Log.e("log_tag", "Error parsing data" + e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            String toast_msg = "0";
            try {
                JSONArray jArray = new JSONArray(result);
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jsonObject = jArray.getJSONObject(i);
                    toast_msg = jsonObject.getString("toast_msg");
                }
            } catch (Exception e) {
                // TODO: handle exception
                //Log.e("log_tag", "Error parsing data"+e.toString());
                //Toast.makeText(Login.this,"error json",Toast.LENGTH_SHORT).show();
            }
            if (toast_msg.equals("0")) {

            } else {
                result = "";
                //Toast.makeText(getApplicationContext(), toast_msg, Toast.LENGTH_SHORT).show();
                //addNotification("0101", toast_msg, toast_msg);
            }
        }
    }
}