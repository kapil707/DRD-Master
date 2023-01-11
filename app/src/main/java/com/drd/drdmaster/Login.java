package com.drd.drdmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.Html;
import android.text.InputType;
import android.text.format.Formatter;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;

public class Login extends AppCompatActivity {
    String result ="",firebase_token = "",device_id="";
    String mainurl = "",page_url1="";
    Button login_btn,login_btn1;
    EditText user_name,password;
    TextView alert;
    String user_session="",user_code="",user_fname="",user_altercode="",user_password="",user_type="",user_name1="",password1="",user_alert="",user_return="",user_division="",user_compcode="",user_compname="";
    UserSessionManager session;
    ProgressBar progressBar2;

    int PERMISSION_ID = 4455;
    FusedLocationProviderClient mFusedLocationClient;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

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

        TextView action_bar_title1 = (TextView) findViewById(R.id.action_bar_title);
        action_bar_title1.setText("DRD Rider App");

        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar2.setVisibility(View.GONE);

        ImageButton action_bar_back = (ImageButton) view.findViewById(R.id.action_bar_back);
        action_bar_back.setVisibility(View.GONE);

        MainActivity ma = new MainActivity();
        mainurl = ma.main_url;
        page_url1 = mainurl + "login/post";

        login_btn = findViewById(R.id.login_btn);
        login_btn1 = findViewById(R.id.login_btn1);

        alert = (TextView) findViewById(R.id.user_alert);
        user_name = (EditText) findViewById(R.id.user_name);
        password = (EditText) findViewById(R.id.user_password);

        String android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        device_id = android_id;

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
                        //System.out.println(token);
                        //Toast.makeText(Home_page.this, "Your device registration token is" + toke, Toast.LENGTH_SHORT).show();

                        firebase_token = token;
                    }
                });

        session = new UserSessionManager(getApplicationContext());
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                try {
                    user_name1 = user_name.getText().toString();
                    password1 = password.getText().toString();
                    if (firebase_token.length() == 0) {
                        alert.setText(Html.fromHtml("<font color='red'>button click error 2</font>"));
                        Toast.makeText(Login.this, "button click error 2", Toast.LENGTH_SHORT).show();
                    } else {
                        if (user_name1.length() > 0) {
                            if (password1.length() > 0) {
                                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                                @SuppressLint("MissingPermission")
                                NetworkInfo ni = cm.getActiveNetworkInfo();
                                if (ni != null) {
                                    try {
                                        new login().execute();
                                        login_btn1.setVisibility(View.VISIBLE);
                                        login_btn.setVisibility(View.GONE);
                                    } catch (Exception e) {
                                        // TODO: handle exception
                                    }
                                } else {
                                    alert.setText(Html.fromHtml("<font color='red'>Check your internet connection</font>"));
                                    Toast.makeText(Login.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                alert.setText(Html.fromHtml("<font color='red'>Enter password</font>"));
                                Toast.makeText(Login.this, "Enter Password", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            alert.setText(Html.fromHtml("<font color='red'>Enter username</font>"));
                            Toast.makeText(Login.this, "Enter Username", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    alert.setText(Html.fromHtml("<font color='red'>button click error</font>"));
                    Toast.makeText(Login.this, "button click error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final ImageView eyes = findViewById(R.id.eyes);
        final ImageView eyes1 = findViewById(R.id.eyes1);
        eyes1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                eyes1.setVisibility(View.GONE);
                eyes.setVisibility(View.VISIBLE);
                password.setInputType(InputType.TYPE_CLASS_TEXT);
                password.setSelection(password.getText().length());
            }
        });

        eyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                eyes.setVisibility(View.GONE);
                eyes1.setVisibility(View.VISIBLE);
                password.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                password.setSelection(password.getText().length());
            }
        });

        /**************location parmisson************/
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
        /*******************************************/

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
    }


    private class login extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar2.setVisibility(View.VISIBLE);
        }
        @SuppressWarnings("WrongThread")
        @Override
        protected Void doInBackground(Void... arg0) {
            user_name1 = user_name.getText().toString();
            password1 = password.getText().toString();
            result ="";
            InputStream isr = null;
            try{
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(page_url1);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("api_id","apiidkapil707sharma-kavita-zxy"));

                nameValuePairs.add(new BasicNameValuePair("user_name1",user_name1));
                nameValuePairs.add(new BasicNameValuePair("password1",password1));
                nameValuePairs.add(new BasicNameValuePair("firebase_token",firebase_token));
                nameValuePairs.add(new BasicNameValuePair("device_id",device_id));
                nameValuePairs.add(new BasicNameValuePair("submit","98c08565401579448aad7c64033dcb4081906dcb"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                isr = entity.getContent();
            }
            catch (Exception e) {
                //Log.e("log_tag","Error in connection"+e.toString());
                //tv.setText("couldn't connect to the database");
                user_alert = "Check your internet";
            }

            try {
                BufferedReader reader= new BufferedReader(new InputStreamReader(isr,"iso-8859-1"),8);
                StringBuilder stringBuilder = new StringBuilder();
                String line = null;
                while ((line = reader.readLine())!=null) {
                    stringBuilder.append(line+"\n");
                }
                isr.close();
                result = stringBuilder.toString();
            } catch (Exception e) {
                // TODO: handle exception
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void args) {
            login_btn.setVisibility(View.VISIBLE);
            login_btn1.setVisibility(View.GONE);
            progressBar2.setVisibility(View.GONE);
            try {
                JSONArray jArray = new JSONArray(result);
                for (int i = 0; i < jArray.length(); i++)
                {
                    JSONObject jsonObject = jArray.getJSONObject(i);
                    user_session    = jsonObject.getString("user_session");
                    user_fname      = jsonObject.getString("user_fname");
                    user_altercode  = jsonObject.getString("user_altercode");
                    user_password   = jsonObject.getString("user_password");
                    user_alert      = jsonObject.getString("user_alert");
                    user_return     = jsonObject.getString("user_return");
                }
            }
            catch (Exception e) {
                // TODO: handle exception
                //Log.e("log_tag", "Error parsing data"+e.toString());
                //Toast.makeText(Login.this,"error json",Toast.LENGTH_SHORT).show();
            }
            if(user_return.equals("1"))
            {
                Toast.makeText(Login.this,user_alert,Toast.LENGTH_SHORT).show();
                alert.setText(user_alert);
                session.createUserLoginSession(user_session,user_altercode,user_password,user_fname,firebase_token);

                Intent in = new Intent();
                in.setClass(Login.this,Home_page.class);
                startActivity(in);
                finish();
            }
            else
            {
                Toast.makeText(Login.this,user_alert,Toast.LENGTH_SHORT).show();
                alert.setText(user_alert);
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            onBackPressed();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try
        {
            finish();
        }catch (Exception e) {
            // TODO: handle exception
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //checkPermission();
    }

    /******************************/

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                PERMISSION_ID
        );
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
        }
    };

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    /******************old code***********************/
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(Login.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Fine Location permission is granted
            // Check if current android version >= 11, if >= 11 check for Background Location permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (ContextCompat.checkSelfPermission(Login.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // Background Location Permission is granted so do your work here
                } else {
                    // Ask for Background Location Permission
                    askPermissionForBackgroundUsage();
                }
            }
        } else {
            // Fine Location Permission is not granted so ask for permission
            askForLocationPermission();
        }
    }

    private void askForLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(Login.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed!")
                    .setMessage("Location Permission Needed!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(Login.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    1456);
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Permission is denied by the user
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1478);
        }
    }

    private void askPermissionForBackgroundUsage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(Login.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed!")
                    .setMessage("Background Location Permission Needed!, tap \"Allow all time in the next screen\"")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(Login.this,
                                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                                    1499);
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // User declined for Background Location Permission.
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1499);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1456) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // User granted location permission
                // Now check if android version >= 11, if >= 11 check for Background Location Permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (ContextCompat.checkSelfPermission(Login.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        // Background Location Permission is granted so do your work here
                    } else {
                        // Ask for Background Location Permission
                        askPermissionForBackgroundUsage();
                    }
                }
            } else {
                // User denied location permission
            }
        } else if (requestCode == 1478) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // User granted for Background Location Permission.
            } else {
                // User declined for Background Location Permission.
            }
        }

    }
}