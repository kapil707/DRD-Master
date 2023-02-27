package com.drd.drdmaster;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

public class Mybackground extends Service {

    Database db;
    SQLiteDatabase sql;

    private LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 5000; /* 2 sec */
    private final Handler handler_myservice_page = new Handler();

    String result = "";
    String mainurl = "", page_url1 = "";

    UserSessionManager session;
    String user_session = "",user_altercode="",firebase_token="";

    @Override
    public void onCreate() {
        super.onCreate();
        /*new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();

                            DatabaseReference myRef2 = database.getReference("master_livelocation2");

                            java.util.Date noteTS = Calendar.getInstance().getTime();

                            String time = "hh:mm:ss"; // 12:00
                            //tvTime.setText(DateFormat.format(time, noteTS));

                            String date = "dd-MM-yyyy"; // 01 January 2013
                            //tvDate.setText(DateFormat.format(date, noteTS));

                            String gettime = DateFormat.format(time, noteTS) + "";
                            String getdate = DateFormat.format(date, noteTS) + "";

                            myRef2.setValue(gettime);

                            //startLocationUpdates();
                            Log.d("Servies", "run: ");
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        ).start();*/
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        user_session    = user.get(UserSessionManager.KEY_USERID);
        user_altercode  = user.get(UserSessionManager.KEY_USERALTERCODE);
        firebase_token  = user.get(UserSessionManager.KEY_FIREBASE_TOKEN);

        MainActivity ma = new MainActivity();
        mainurl = ma.main_url;
        page_url1 = mainurl + "update_live_track_user/post";

        db = new Database(this);
        sql = db.getWritableDatabase();

        if(!user_session.equals("")) {
            startLocationUpdates();
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    String myquery = "";
    String latitude = "", longitude = "";
    public void onLocationChanged(Location location) {

        java.util.Date noteTS = Calendar.getInstance().getTime();

        String time = "hh:mm"; // 12:00
        //tvTime.setText(DateFormat.format(time, noteTS));

        String date = "yyyy-MM-dd"; // 01 January 2013
        //tvDate.setText(DateFormat.format(date, noteTS));

        String gettime = DateFormat.format(time, noteTS) + "";
        String getdate = DateFormat.format(date, noteTS) + "";

        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());

        if(!myquery.equals(gettime)) {
            myquery = gettime;

            try
            {
                ContentValues cvcv = new ContentValues();

                cvcv.put("user_session",user_session);
                cvcv.put("user_altercode",user_altercode);
                cvcv.put("firebase_token",firebase_token);
                cvcv.put("latitude",latitude);
                cvcv.put("longitude",longitude);
                cvcv.put("getdate",getdate);
                cvcv.put("gettime",gettime);

                sql.insert("tbl_user_loc", "", cvcv);
            }catch (Exception e) {
                // TODO: handle exception
            }

            new update_live_track_user().execute();
        }
    }

    String new_latitude = "";
    String new_longitude = "";
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

                            new_latitude = "{'latitude':'"+latitude+"'},";
                            new_longitude = "{'longitude':'"+latitude+"'},";
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
                nameValuePairs.add(new BasicNameValuePair("new_latitude", new_latitude));
                nameValuePairs.add(new BasicNameValuePair("new_longitude", new_longitude));
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