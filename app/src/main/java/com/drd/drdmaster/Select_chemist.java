package com.drd.drdmaster;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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

public class Select_chemist extends AppCompatActivity {

    LinearLayout linearLayout1;
    String user_session="",user_type="";
    UserSessionManager session;

    String result ="";

    ListView listview;
    Select_chemist_Adapter adapter;
    ProgressBar menu_loading1;
    List<Select_chemist_get_or_set> movieList = new ArrayList<Select_chemist_get_or_set>();

    String chemist_id="";
    String mainurl="",page_url1="";
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_chemist);

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
        action_bar_title1.setText("Select Chemist");

        menu_loading1 = (ProgressBar) findViewById(R.id.menu_loading1);

        ImageButton imageButton= (ImageButton)view.findViewById(R.id.action_bar_back);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent();
                in.setClass(Select_chemist.this, Home_page.class);
                startActivity(in);
                finish();
            }
        });

        MainActivity ma = new MainActivity();
        mainurl = ma.main_url;
        page_url1 =  mainurl + "get_delivery_chemist/post";

        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        user_session = user.get(UserSessionManager.KEY_USERID);

        listview = (ListView) findViewById(R.id.listView1);
        adapter = new Select_chemist_Adapter(Select_chemist.this, movieList);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                // TODO Auto-generated method stub
                Select_chemist_get_or_set clickedCategory = movieList.get(arg2);
                chemist_id = clickedCategory.chemist_id();
                String gstvno = clickedCategory.gstvno();

                //alertMessage_selected_acm();

                Intent in = new Intent();
                in.setClass(Select_chemist.this, Upload_chemist_img.class);
                in.putExtra("chemist_id", chemist_id);
                in.putExtra("gstvno", gstvno);
                startActivity(in);
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
    }

    private class json_get_delivery_chemist extends AsyncTask<Void, Void, Void> {
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
                nameValuePairs.add(new BasicNameValuePair("user_session", user_session));

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
        protected void onPostExecute(Void args)
        {
            menu_loading1.setVisibility(View.GONE);
            movieList.clear();
            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            try {
                int intid = 0;
                ContentValues cvcv = new ContentValues();
                JSONArray jArray = new JSONArray(result);
                for (int i = 0; i < jArray.length(); i++) {

                    JSONObject jsonObject = jArray.getJSONObject(i);
                    String chemist_id = jsonObject.getString("chemist_id");
                    String name = jsonObject.getString("name");
                    String amt = jsonObject.getString("amt");
                    String gstvno = jsonObject.getString("gstvno");

                    Select_chemist_get_or_set movie = new Select_chemist_get_or_set();
                    movie.chemist_id(chemist_id);
                    movie.name(name);
                    movie.amt(amt);
                    movie.gstvno(gstvno);
                    movie.intid(String.valueOf(intid++));
                    movieList.add(movie);
                }
            }catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume(){
        // TODO Auto-generated method stub
        super.onResume();

        new json_get_delivery_chemist().execute();
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent();
        in.setClass(Select_chemist.this, Home_page.class);
        startActivity(in);
        finish();
    }
}