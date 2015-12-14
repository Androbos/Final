package com.example.administrator.task;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class relative extends Activity {

    String accountName;
    String email;
    Context context = this;
    ArrayList<Integer> taskid = new ArrayList<Integer>();
    ArrayList<Integer> subtaskid = new ArrayList<Integer>();
    ArrayList<String> taskname = new ArrayList<String>();

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relative);
        Intent intentstream = getIntent();
        accountName =intentstream.getStringExtra("account");
        email=intentstream.getStringExtra("email");
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bar)));
        int abTitleId = getResources().getIdentifier("action_bar_title", "id", "android");
        findViewById(abTitleId).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(relative.this, ManageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("account", accountName);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.push_right_in,
                        R.anim.push_left_out);
                finish();
            }
        });



        final ListView listView = (ListView) findViewById(R.id.relativelist);

        final String request_url = "http://task-1123.appspot.com/suggest?userid="+accountName;
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] response) {


                try {

                    JSONObject jObject = new JSONObject(new String(response));
                    JSONArray id = new JSONArray();
                    JSONArray name = new JSONArray();
                    JSONArray Jsubtaskid=new JSONArray();

                    if (!jObject.isNull("taskid")) {
                        id = jObject.getJSONArray("taskid");
                    }

                    if (!jObject.isNull("taskname")) {
                        name = jObject.getJSONArray("taskname");
                    }
                    if (!jObject.isNull("subtaskid")) {
                        Jsubtaskid = jObject.getJSONArray("subtaskid");
                    }

                    for (int j = 0; j < Jsubtaskid.length(); j++) {
                        subtaskid.add(Jsubtaskid.getInt(j));
                    }


                    for (int i = 0; i < name.length(); i++) {
                        taskname.add(name.getString(i));
                        taskid.add(id.getInt(i));
                        System.out.println(taskname.size());
                    }

                } catch (JSONException j) {
                    System.out.println("JSON Error");
                }

                System.out.println(taskname.size());
                SAdapter adapter = new SAdapter(relative.this, taskname);

                // Assign adapter to ListView
                listView.setAdapter(new ArrayAdapter<String>(context,R.layout.testview,taskname));
                // ListView Item Click Listener
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {

                        Integer itemValue = taskid.get(position);

                        Intent intent = new Intent(relative.this, SingleCommonTask.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("account", accountName);
                        bundle.putInt("CTaskID", itemValue);
                        bundle.putString("email",email);
                        if(subtaskid.contains(itemValue)){
                            bundle.putBoolean("hasJoined",true);
                        }else{
                            bundle.putBoolean("hasJoined",false);
                        }
                        intent.putExtras(bundle);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        overridePendingTransition(R.anim.push_right_in,
                                R.anim.push_left_out);
                    }
                });

                final TextView load =(TextView) findViewById(R.id.loading);
                load.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e("ManagePage", "There was a problem in retrieving the url : " + e.toString());
            }
        });
//=============================

    }
    //BACK
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK){

            this.finish();  //finish当前activity
            overridePendingTransition(R.anim.push_right_in,
                    R.anim.push_left_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}

