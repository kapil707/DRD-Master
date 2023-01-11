package com.drd.drdmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    public String main_url = "https://www.drdistributor.com/android/Api_track2/";
    public String main_url_webview = "https://www.drdistributor.com/android/Api_mobile_html35/"; // Notification_Service ke page par be alag sau use ho raha ha so plz wha be kar layna change

    UserSessionManager session;
    String user_session = "", user_password = "";

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        user_session = user.get(UserSessionManager.KEY_USERID);
        user_password = user.get(UserSessionManager.KEY_PASSWORD);

       try {
            if (session.checkLogin()) {
                finish();
            } else {
                Intent in = new Intent();
                in.setClass(MainActivity.this, Home_page.class);
                startActivity(in);
                finish();
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        wakeLock.acquire();
    }
}