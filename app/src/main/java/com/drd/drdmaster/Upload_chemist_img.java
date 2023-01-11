package com.drd.drdmaster;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class Upload_chemist_img extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    Bitmap bitmap;
    boolean check = true;
    Button UploadImageServer, UploadImageServer1;
    Button take_photo, galery_select;

    GridView listview;
    Upload_chemist_img_Adapter adapter;
    ProgressBar menu_loading1;
    List<Upload_chemist_img_get_or_set> movieList = new ArrayList<Upload_chemist_img_get_or_set>();

    String ImagePath = "image_path";

    String ServerUploadPath = "", page_url1 = "", page_url2 = "",page_url3 = "";
    String result = "", message = "";

    UserSessionManager session;

    String chemist_id = "", gstvno = "";
    String session_id = "", user_altercode = "";

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_chemist_img);

        Intent in = getIntent();
        chemist_id = in.getStringExtra("chemist_id");
        gstvno = in.getStringExtra("gstvno");

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
        action_bar_title1.setText("Update image (" + chemist_id + ")");
        TextView action_bar_title11 = (TextView) findViewById(R.id.action_bar_title1);
        action_bar_title11.setText(gstvno);
        action_bar_title11.setVisibility(View.VISIBLE);
        ImageButton imageButton = (ImageButton) findViewById(R.id.action_bar_back);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        menu_loading1 = (ProgressBar) findViewById(R.id.menu_loading1);

        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();

        session_id = user.get(UserSessionManager.KEY_USERID);
        user_altercode = user.get(UserSessionManager.KEY_USERID);


        MainActivity ma = new MainActivity();
        String mainurl = ma.main_url;
        ServerUploadPath = mainurl + "upload_rider_chemist_photo/post";
        page_url1 = mainurl + "show_rider_chemist_photo/post";
        page_url2 = mainurl + "delete_rider_chemist_photo/post";
        page_url3 = mainurl + "complete_order/post";

        listview = findViewById(R.id.listView1);
        adapter = new Upload_chemist_img_Adapter(Upload_chemist_img.this, movieList);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                // TODO Auto-generated method stub
                Upload_chemist_img_get_or_set clickedCategory = movieList.get(arg2);
                String id = clickedCategory.id();
                alertMessage_delete_rider_chemist_photo(id);
                //alertMessage_selected_acm();
            }
        });

        listview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View arg0) {
                // TODO Auto-generated method stub
                /* Toast.makeText(getApplicationContext(), "Position",Toast.LENGTH_LONG).show(); */
                return false;
            }
        });

        imageView = (ImageView) findViewById(R.id.imageView);
        take_photo = findViewById(R.id.take_photo);
        galery_select = findViewById(R.id.galery_select);
        UploadImageServer = (Button) findViewById(R.id.buttonUpload);
        UploadImageServer1 = (Button) findViewById(R.id.buttonUpload1);

        UploadImageServer.setVisibility(View.GONE);
        UploadImageServer1.setVisibility(View.VISIBLE);


        galery_select.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select image from gallery"), 1989);

            }
        });

        take_photo.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }

            }
        });

        UploadImageServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageUploadToServerFunction();
            }
        });

        final LinearLayout upload_order_LinearLayout = findViewById(R.id.upload_order_LinearLayout);
        final LinearLayout complete_order_LinearLayout = findViewById(R.id.complete_order_LinearLayout);
        final ImageView upload_btn = findViewById(R.id.upload_btn);
        upload_btn.setVisibility(View.VISIBLE);
        upload_order_LinearLayout.setVisibility(View.VISIBLE);
        final ImageView upload_cancel = findViewById(R.id.upload_cancel);
        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload_btn.setVisibility(View.GONE);
                upload_order_LinearLayout.setVisibility(View.GONE);
                upload_cancel.setVisibility(View.VISIBLE);
                complete_order_LinearLayout.setVisibility(View.VISIBLE);
            }
        });

        upload_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload_btn.setVisibility(View.VISIBLE);
                upload_order_LinearLayout.setVisibility(View.VISIBLE);
                upload_cancel.setVisibility(View.GONE);
                complete_order_LinearLayout.setVisibility(View.GONE);
            }
        });

        Button complete_order = findViewById(R.id.complete_order);
        complete_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertMessage_complete_order();
            }
        });
    }

    public void alertMessage_delete_rider_chemist_photo(final String id) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        try {
                            new json_delete_rider_chemist_photo().execute(id);
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
        builder.setMessage("Are you sure to delete photo?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public void alertMessage_complete_order() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        try {
                            mGPS_info();
                            new json_complete_order().execute();
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
        builder.setMessage("Are you sure to completed order?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int RC, int RQC, Intent I) {
        super.onActivityResult(RC, RQC, I);
        //Toast.makeText(User_image_uploading.this, String.valueOf(RC), Toast.LENGTH_LONG).show();
        if (RC == CAMERA_REQUEST) {
            bitmap = (Bitmap) I.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);

            UploadImageServer.setVisibility(View.VISIBLE);
            UploadImageServer1.setVisibility(View.GONE);
        }
        if (RC == 1989) {
            Uri uri = I.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
                UploadImageServer.setVisibility(View.VISIBLE);
                UploadImageServer1.setVisibility(View.GONE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void ImageUploadToServerFunction() {
        String ConvertImage = null;
        try {
            ByteArrayOutputStream byteArrayOutputStreamObject;
            byteArrayOutputStreamObject = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStreamObject);
            byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();
            ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);
        } catch (Exception ee) {

        }
        final String finalConvertImage = ConvertImage;
        class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //progressDialog = ProgressDialog.show(User_image_uploading.this,"Uploading","Please Wait",false,false);
                menu_loading1.setVisibility(View.VISIBLE);
                UploadImageServer.setVisibility(View.GONE);
                UploadImageServer1.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String user_image_server) {
                super.onPostExecute(user_image_server);
                menu_loading1.setVisibility(View.GONE);
                // Dismiss the progress dialog after done uploading.
                //progressDialog.dismiss();
                // Printing uploading success message coming from server on android app.
                Toast.makeText(Upload_chemist_img.this, "Uploaded Successfully", Toast.LENGTH_LONG).show();
                imageView.setVisibility(View.GONE);
                new json_show_rider_chemist_photo().execute();
            }

            @Override
            protected String doInBackground(Void... params) {
                ImageProcessClass imageProcessClass = new ImageProcessClass();
                HashMap<String, String> HashMapParams = new HashMap<String, String>();

                HashMapParams.put(ImagePath, finalConvertImage);
                HashMapParams.put("gstvno", gstvno);
                HashMapParams.put("chemist_id", chemist_id);
                HashMapParams.put("user_altercode", user_altercode);

                String FinalData = imageProcessClass.ImageHttpRequest(ServerUploadPath, HashMapParams);

                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
    }

    public class ImageProcessClass {
        public String ImageHttpRequest(String requestURL, HashMap<String, String> PData) {
            StringBuilder stringBuilder = new StringBuilder();
            try {
                URL url;
                HttpURLConnection httpURLConnectionObject;
                OutputStream OutPutStream;
                BufferedWriter bufferedWriterObject;
                BufferedReader bufferedReaderObject;
                int RC;
                url = new URL(requestURL);
                httpURLConnectionObject = (HttpURLConnection) url.openConnection();
                httpURLConnectionObject.setReadTimeout(19000);
                httpURLConnectionObject.setConnectTimeout(19000);
                httpURLConnectionObject.setRequestMethod("POST");
                httpURLConnectionObject.setDoInput(true);
                httpURLConnectionObject.setDoOutput(true);
                OutPutStream = httpURLConnectionObject.getOutputStream();
                bufferedWriterObject = new BufferedWriter(
                        new OutputStreamWriter(OutPutStream, "UTF-8"));
                bufferedWriterObject.write(bufferedWriterDataFN(PData));
                bufferedWriterObject.flush();
                bufferedWriterObject.close();
                OutPutStream.close();
                RC = httpURLConnectionObject.getResponseCode();
                if (RC == HttpsURLConnection.HTTP_OK) {
                    bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));
                    stringBuilder = new StringBuilder();
                    String RC2;
                    while ((RC2 = bufferedReaderObject.readLine()) != null) {
                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            StringBuilder stringBuilderObject;
            stringBuilderObject = new StringBuilder();
            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
                if (check)
                    check = false;
                else
                    stringBuilderObject.append("&");

                stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));
                stringBuilderObject.append("=");
                stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }
            return stringBuilderObject.toString();
        }
    }

    private class json_show_rider_chemist_photo extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            menu_loading1.setVisibility(View.VISIBLE);
            movieList.clear();
        }

        @SuppressWarnings("WrongThread")
        @Override
        protected Void doInBackground(Void... arg0) {
            result = "";
            InputStream isr = null;
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(page_url1);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("api_id", "apiidkapil707sharma-kavita-zxy"));
                nameValuePairs.add(new BasicNameValuePair("submit", "98c08565401579448aad7c64033dcb4081906dcb"));
                nameValuePairs.add(new BasicNameValuePair("gstvno", gstvno));
                nameValuePairs.add(new BasicNameValuePair("chemist_id", chemist_id));
                nameValuePairs.add(new BasicNameValuePair("user_altercode", user_altercode));

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
            movieList.clear();
            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            try {
                int intid = 0;
                ContentValues cvcv = new ContentValues();
                JSONArray jArray = new JSONArray(result);
                for (int i = 0; i < jArray.length(); i++) {

                    JSONObject jsonObject = jArray.getJSONObject(i);
                    String id =  jsonObject.getString("id");
                    String image = jsonObject.getString("image");
                    String time = jsonObject.getString("time");

                    Upload_chemist_img_get_or_set movie = new Upload_chemist_img_get_or_set();
                    movie.id(id);
                    movie.image(image);
                    movie.time(time);
                    movie.intid(String.valueOf(intid++));
                    movieList.add(movie);
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }
            adapter.notifyDataSetChanged();
        }
    }

    private class json_delete_rider_chemist_photo extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            menu_loading1.setVisibility(View.VISIBLE);
            movieList.clear();
        }

        @SuppressWarnings("WrongThread")
        @Override
        protected Void doInBackground(String... arg0) {
            result = "";
            InputStream isr = null;
            try {
                String id = arg0[0];
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(page_url2);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("api_id", "apiidkapil707sharma-kavita-zxy"));
                nameValuePairs.add(new BasicNameValuePair("submit", "98c08565401579448aad7c64033dcb4081906dcb"));
                nameValuePairs.add(new BasicNameValuePair("id", id));

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
                    String toast_msg = jsonObject.getString("toast_msg");
                    //finish();
                    Toast.makeText(getApplicationContext(), toast_msg, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }
            new json_show_rider_chemist_photo().execute();
        }
    }

    private class json_complete_order extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            menu_loading1.setVisibility(View.VISIBLE);
            movieList.clear();

            EditText enter_remarks = findViewById(R.id.enter_remarks);
            message = enter_remarks.getText().toString();
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
                nameValuePairs.add(new BasicNameValuePair("gstvno", gstvno));
                nameValuePairs.add(new BasicNameValuePair("chemist_id", chemist_id));
                nameValuePairs.add(new BasicNameValuePair("user_altercode", user_altercode));
                nameValuePairs.add(new BasicNameValuePair("message", message));
                nameValuePairs.add(new BasicNameValuePair("getlatitude", getlatitude));
                nameValuePairs.add(new BasicNameValuePair("getlongitude", getlongitude));

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
                    String toast_msg = jsonObject.getString("toast_msg");
                    finish();
                    Toast.makeText(getApplicationContext(), toast_msg, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    String gettime = "", getdate = "";
    GPSTracker mGPS;
    double latitude1, longitude1;
    String getlatitude = "", getlongitude = "";

    public void mGPS_info() {
        mGPS = new GPSTracker(this);
        mGPS.getLocation();

        latitude1 = mGPS.getLatitude();
        longitude1 = mGPS.getLongitude();

        getlatitude = String.valueOf(latitude1);
        getlongitude = String.valueOf(longitude1);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        mGPS_info();
        new json_show_rider_chemist_photo().execute();

        /*********************************************************
         Function_class fc = new Function_class();
         String count_cart = fc.count_cart_1(user_type,this);
         /*********************************************************

         TextView action_bar_cart_total1 = (TextView) findViewById(R.id.action_bar_cart_total);
         action_bar_cart_total1.setText(" " + count_cart + " ");*/
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}