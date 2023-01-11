package com.drd.drdmaster;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

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


import android.content.Intent;
import android.os.PowerManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

public class Home_page extends AppCompatActivity {

    // GPSTracker class
    GPSTracker gps;

    String result = "";
    String main_url = "", page_url1 = "" , page_url2 = "", page_url3 = "";

    LinearLayout btn1, btn2, btn3, btn4;
    ProgressBar menu_loading1;
    TextView action_bar_title1;
    String main_url_webview="";
    WebView webView;

    UserSessionManager session;
    String user_session = "";
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.new_theme_color));
        }

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.menu);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.new_theme_color)));
        View view = getSupportActionBar().getCustomView();

        action_bar_title1 = (TextView) findViewById(R.id.action_bar_title);
        action_bar_title1.setText("DRD Member App");

        menu_loading1 = (ProgressBar) findViewById(R.id.menu_loading1);

        ImageButton imageButton = (ImageButton) view.findViewById(R.id.action_bar_back);
        imageButton.setVisibility(View.GONE);

        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        user_session = user.get(UserSessionManager.KEY_USERID);

        /*Toast.makeText(Home_page.this, "Your device registration token is " + user_session
                , Toast.LENGTH_SHORT).show();*/

        receiver = new MyBroadcastReceiver();

        MainActivity ma = new MainActivity();
        main_url = ma.main_url;
        page_url1 = main_url+"update_live_track_user/post";
        page_url2 = main_url+"user_logout/post";
        page_url3 = main_url+"get_home_page/post";
        main_url_webview = ma.main_url_webview;



        /* if (!forgroundServiesRunning()) {
            Intent servies1 = new Intent(this, MyforgoundServies.class);
            startForegroundService(servies1);
        } */

        //https://cybertechunt.medium.com/how-to-make-an-android-app-to-always-run-in-background-programmatically-b1798c9a4661
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }

        //startLocationUpdates();

        /*EditText editTextTextPersonName;
        editTextTextPersonName = findViewById(R.id.editTextTextPersonName);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            System.out.println("Fetching FCM registration token failed");
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        System.out.println(token);
                        Toast.makeText(Home_page.this, "Your device registration token is" + token
                                , Toast.LENGTH_SHORT).show();

                        editTextTextPersonName.setText(token);
                    }
                });*/

        /*FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef2 = database.getReference("test");

        java.util.Date noteTS = Calendar.getInstance().getTime();

        String time = "hh:mm:ss"; // 12:00
        //tvTime.setText(DateFormat.format(time, noteTS));

        String date = "dd-MM-yyyy"; // 01 January 2013
        //tvDate.setText(DateFormat.format(date, noteTS));

        String gettime = DateFormat.format(time, noteTS) + "";
        String getdate = DateFormat.format(date, noteTS) + "";

        myRef2.setValue(gettime);*/

        webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl(main_url_webview + "slider");

        webView.addJavascriptInterface(new WebAppInterface(this), "AndroidInterface"); // To call methods in Android from using js in the html, AndroidInterface.showToast, AndroidInterface.getAndroidVersion etc
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                //Make the bar disappear after URL is loaded, and changes string to Loading...
                setTitle("Loading...");
                setProgress(progress * 100); //Make the bar disappear after URL is loaded

                // Return the app name after finish loading
                if (progress == 100)
                    setTitle(R.string.app_name);
            }
        });

        btn1 = (LinearLayout) findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent();
                in.setClass(Home_page.this, Select_chemist.class);
                startActivity(in);
                finish();
            }
        });

        btn2 = (LinearLayout) findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent in = new Intent();
                in.setClass(Home_page.this, Qr_code_scan.class);
                startActivity(in);
                finish();*/
            }
        });

        btn3 = (LinearLayout) findViewById(R.id.btn3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn4 = (LinearLayout) findViewById(R.id.btn4);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertMessage_logoutUser();
            }
        });

        /*AlaramHandler alaramHandler = new AlaramHandler(this);
        alaramHandler.cancelAlarmManager();
        alaramHandler.setAlarmManager();*/
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            //Calling a javascript function in html page
            view.loadUrl("javascript:alert(showVersion('called by Android'))");
        }
    }

    public void alertMessage_logoutUser() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        try {
                            //new json_user_logout().execute();
                            session.logoutUser();
                            finish();
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        // do nothing

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to Logout?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public boolean forgroundServiesRunning()
    {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE))
        {
            if(MyforgoundServies.class.getName().equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }

    String latitude = "",longitude="";
    public void gps() {
        // New location has now been determined
        /*getlatitude = location.getLatitude()+"";
        getlongitude = location.getLongitude()+"";*/

        java.util.Date noteTS = Calendar.getInstance().getTime();

        String time = "hh:mm:ss"; // 12:00
        //tvTime.setText(DateFormat.format(time, noteTS));

        String date = "dd-MM-yyyy"; // 01 January 2013
        //tvDate.setText(DateFormat.format(date, noteTS));

        String gettime = DateFormat.format(time, noteTS) + "";
        String getdate = DateFormat.format(date, noteTS) + "";

        gps = new GPSTracker(Home_page.this);

        double latitude1 = gps.getLatitude();
        double longitude1 = gps.getLongitude();


        latitude =  String.valueOf(latitude1);
        longitude =  String.valueOf(longitude1);

        /*Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
                + user_session + "\nLong: " + longitude, Toast.LENGTH_LONG).show();*/

        //new update_live_track_user().execute();

        /*LocationHelper helper = new LocationHelper(
                location.getLongitude(),
                location.getLatitude(),
                gettime,
                getdate
        );*/

        /*FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef;

        myRef = database.getReference("master_live_location/" + user_altercode);

        myRef.setValue(helper).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {

                }
            }
        });*/
    }

    MyBroadcastReceiver receiver;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent servies = new Intent(this, Mybackground.class);
        startService(servies);

        //gps();
        new json_get_home_page().execute();
        //registerReceiver(receiver, intentFilter);
    }

    private class update_live_track_user extends AsyncTask<Void, Void, Void> {
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
                nameValuePairs.add(new BasicNameValuePair("latitude", latitude));
                nameValuePairs.add(new BasicNameValuePair("longitude", longitude));
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
                //Log.e("log_tag", "Error parsing data" + e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
           /* Toast.makeText(Home_page.this, result, Toast.LENGTH_SHORT).show(); */
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

    private class json_get_home_page extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            menu_loading1.setVisibility(View.VISIBLE);
        }

        @SuppressWarnings("WrongThread")
        @Override
        protected Void doInBackground(Void... arg0) {
            result = "";
            InputStream isr = null;
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(page_url3);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("api_id", "apiidkapil707sharma-kavita-zxy"));
                nameValuePairs.add(new BasicNameValuePair("submit", "98c08565401579448aad7c64033dcb4081906dcb"));

                nameValuePairs.add(new BasicNameValuePair("user_id", user_session));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                isr = entity.getContent();
            } catch (Exception e) {
                //Log.e("log_tag","Error in connection"+e.toString());
                //tv.setText("couldn't connect to the database");
                //mProgressDialog.dismiss();
                //user_alert = "Check your internet";
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
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            menu_loading1.setVisibility(View.GONE);

            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();

            try {
                JSONArray jArray = new JSONArray(result);
                for (int i = 0; i < jArray.length(); i++) {

                    JSONObject jsonObject = jArray.getJSONObject(i);
                    String attendance = jsonObject.getString("attendance");
                    String time1 = jsonObject.getString("time");
                    String type1 = jsonObject.getString("type");

                    btn1.setVisibility(View.GONE);
                    btn2.setVisibility(View.GONE);
                    btn3.setVisibility(View.GONE);
                    if(type1.equals("rider"))
                    {
                        btn1.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        if(attendance.equals("yes")) {
                            Toast.makeText(getApplicationContext(), "Attendance Done By Time :- "+time1, Toast.LENGTH_LONG).show();

                            btn3.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            btn2.setVisibility(View.VISIBLE);
                        }
                    }
                }
            } catch (Exception e) {
                //Toast.makeText(getApplicationContext(), "Scanner error", Toast.LENGTH_LONG).show();
            }
        }
    }
}