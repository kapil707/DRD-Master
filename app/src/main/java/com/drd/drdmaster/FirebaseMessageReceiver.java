package com.drd.drdmaster;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class FirebaseMessageReceiver extends FirebaseMessagingService {
    public static final String ANDROID_CHANNEL_ID = "com.drd.drdmaster";
    private LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 5000; /* 2 sec */
    private final Handler handler_myservice_page = new Handler();

    String result = "";
    String mainurl = "", page_url1 = "";

    UserSessionManager session;
    String user_session = "";

    // Override onMessageReceived() method to extract the
    // title and
    // body from the message passed in FCM
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void
    onMessageReceived(RemoteMessage remoteMessage) {
        // First case when notifications are received via
        // data event
        // Here, 'title' and 'message' are the assumed names
        // of JSON
        // attributes. Since here we do not have any data
        // payload, This section is commented out. It is
        // here only for reference purposes.
        /*if(remoteMessage.getData().size()>0){
            showNotification(remoteMessage.getData().get("title"),
                          remoteMessage.getData().get("message"));
        }*/

        String id = remoteMessage.getData().get("id");
        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("message");

        final String CHANNELID = "FORGOUND";
        NotificationChannel channel = new NotificationChannel(
                CHANNELID,
                CHANNELID,
                NotificationManager.IMPORTANCE_LOW
        );

        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder noti = new Notification.Builder(this,CHANNELID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.logo);

        startForeground(1001,noti.build());

        /*Intent servies1 = new Intent(this, MyforgoundServies.class);
        startForegroundService(servies1);*/

        Intent servies = new Intent(this, Mybackground.class);
        startService(servies);

        /*session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        user_session = user.get(UserSessionManager.KEY_USERID);

        MainActivity ma = new MainActivity();
        mainurl = ma.main_url;
        page_url1 = mainurl + "update_live_track_user/post";
        startLocationUpdates();*/

        //showNotification(title,message);

        // Second case when notification payload is
        // received.
        if (remoteMessage.getNotification() != null) {
            // Since the notification is received directly from
            // FCM, the title and the body can be fetched
            // directly as below.
            /*showNotification(
                    remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody());*/





            /*Intent servies1 = new Intent(this, MyforgoundServies.class);
            startForegroundService(servies1);

            Intent servies = new Intent(this, Mybackground.class);
            startService(servies);*/
        }
    }

    private void addNotification(int myid, String title, String message, String funtype, String itemid, String division, String company_full_name, final String image) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_layout);
            contentView.setImageViewResource(R.id.image, R.drawable.logo);
            contentView.setTextViewText(R.id.title, title);
            contentView.setTextViewText(R.id.text, Html.fromHtml(message));

            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(getApplicationContext(), ANDROID_CHANNEL_ID)
                            .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                            .setSmallIcon(R.drawable.logo) //set icon for notification
                            .setContent(contentView)//this is notification message
                            .setAutoCancel(true) // makes auto cancel of notification
                            .setContentTitle(title)
                            .setContentText(message)
                            .setSound(soundUri);

            Intent notificationIntent = null;
            if(funtype.equals("0")) {
                notificationIntent = new Intent(this, MainActivity.class);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }

            if(funtype.equals("1")) {
                notificationIntent = new Intent(this, MainActivity.class);
                notificationIntent.putExtra("item_code",itemid);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }

            if(funtype.equals("2")) {
                notificationIntent = new Intent(this, MainActivity.class);
                notificationIntent.putExtra("item_code",itemid);
                notificationIntent.putExtra("item_division",division);
                notificationIntent.putExtra("item_page_type","featured_brand");
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }

            if(funtype.equals("3")) {
                notificationIntent = new Intent(this, MainActivity.class);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }

            if(funtype.equals("4")) {
                notificationIntent = new Intent(this, MainActivity.class);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }

            if(funtype.equals("5")) {
                notificationIntent = new Intent(this, MainActivity.class);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);

            if(image.equals("not")) {
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(myid, builder.build());
            }
            else{
                //Handle image url if present in the push message
                String attachmentUrl = image;

                if (attachmentUrl != null) {
                    Bitmap image1 = getBitmapFromURL(attachmentUrl);
                    if (image != null) {
                        builder.setStyle(new
                                NotificationCompat.BigPictureStyle().bigPicture(image1));
                    }
                }

                final Notification notification = builder.build();
                if (android.os.Build.VERSION.SDK_INT >= 16) {
                    notification.bigContentView = contentView;
                }

                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(myid, builder.build());
            }

        } else {
            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_layout);
            contentView.setImageViewResource(R.id.image, R.drawable.logo);
            contentView.setTextViewText(R.id.title, title);
            contentView.setTextViewText(R.id.text, Html.fromHtml(message));

            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this)
                            .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                            .setSmallIcon(R.drawable.logo) //set icon for notification
                            .setContent(contentView)//this is notification message
                            .setAutoCancel(true) // makes auto cancel of notification
                            .setContentTitle(title)
                            .setContentText(message)
                            .setSound(soundUri)
                            .setPriority(Notification.PRIORITY_DEFAULT); //set priority of notification


            Intent notificationIntent = null;
            if(funtype.equals("0")) {
                notificationIntent = new Intent(this, MainActivity.class);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }

            if(funtype.equals("1")) {
                notificationIntent = new Intent(this, MainActivity.class);
                notificationIntent.putExtra("item_code",itemid);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }

            if(funtype.equals("2")) {
                notificationIntent = new Intent(this, MainActivity.class);
                notificationIntent.putExtra("item_code",itemid);
                notificationIntent.putExtra("item_division",division);
                notificationIntent.putExtra("item_page_type","featured_brand");
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }

            if(funtype.equals("3")) {
                notificationIntent = new Intent(this, MainActivity.class);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }

            if(funtype.equals("4")) {
                notificationIntent = new Intent(this, MainActivity.class);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }

            if(funtype.equals("5")) {
                notificationIntent = new Intent(this, MainActivity.class);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);

            if(image.equals("not")) {
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(myid, builder.build());
            }
            else{
                //Handle image url if present in the push message
                String attachmentUrl = image;

                if (attachmentUrl != null) {
                    Bitmap image1 = getBitmapFromURL(attachmentUrl);
                    if (image != null) {
                        builder.setStyle(new
                                NotificationCompat.BigPictureStyle().bigPicture(image1));
                    }
                }

                final Notification notification = builder.build();
                if (android.os.Build.VERSION.SDK_INT >= 16) {
                    notification.bigContentView = contentView;
                }

                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(myid, builder.build());
            }
        }
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
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

    String latitude = "", longitude = "";
    public void onLocationChanged(Location location) {

        java.util.Date noteTS = Calendar.getInstance().getTime();

        String time = "hh:mm:ss"; // 12:00
        //tvTime.setText(DateFormat.format(time, noteTS));

        String date = "dd-MM-yyyy"; // 01 January 2013
        //tvDate.setText(DateFormat.format(date, noteTS));

        String gettime = DateFormat.format(time, noteTS) + "";
        String getdate = DateFormat.format(date, noteTS) + "";

        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());

        new update_live_track_user().execute();
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
                Log.e("log_tag", "Error parsing data" + e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
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