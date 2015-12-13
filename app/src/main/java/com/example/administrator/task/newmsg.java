package com.example.administrator.task;

import android.app.ActionBar;
import android.app.Activity;
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
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class newmsg extends Activity {

    String accountName;
    String email;
    ArrayList<Integer> newmsgtaskid = new ArrayList<Integer>();
    ArrayList<String> sender = new ArrayList<String>();

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newmsg);
        Intent intentstream = getIntent();
        accountName =intentstream.getStringExtra("account");
        newmsgtaskid=intentstream.getIntegerArrayListExtra("newmsgtaskid");
        sender=intentstream.getStringArrayListExtra("sender");
        email=intentstream.getStringExtra("email");

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bar)));
        int abTitleId = getResources().getIdentifier("action_bar_title", "id", "android");
        findViewById(abTitleId).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(newmsg.this, ManageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("account", accountName);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.push_right_in,
                        R.anim.push_left_out);
                finish();
            }
        });

        ArrayList<String> msg= new ArrayList<String>();
        for(int i=0;i<sender.size();i++){
            msg.add(sender.get(i) + " replies you");
        }


        final ListView listView = (ListView) findViewById(R.id.newmsglist);

        SAdapter adapter = new SAdapter(newmsg.this,msg);

        // Assign adapter to ListView
        listView.setAdapter(adapter);
        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {

                RequestParams params = new RequestParams();
                params.put("sender", sender.get(position));
                params.put("receiver", accountName);
                params.put("taskid", newmsgtaskid.get(position));


                AsyncHttpClient client = new AsyncHttpClient();
                client.post("http://task-1123.appspot.com/deletereplyremind", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] response) {
                        Log.w("delete", "success!!!!");

                        Integer itemValue = newmsgtaskid.get(position);

                        Intent intent = new Intent(newmsg.this, SingleCommonTask.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("account", accountName);
                        bundle.putInt("CTaskID", itemValue);
                        bundle.putBoolean("hasJoined", true);
                        bundle.putString("email", email);
                        intent.putExtras(bundle);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        overridePendingTransition(R.anim.push_right_in,
                                R.anim.push_left_out);

                        final TextView load = (TextView) findViewById(R.id.loading);
                        load.setVisibility(View.GONE);

//                Toast.makeText(context, "Upload Successful", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] errorResponse, Throwable e) {
                        Log.e("Posting_to_blob", "There was a problem in retrieving the url : " + e.toString());
                    }
                });
            }
        });


//=============================

        final TextView load =(TextView) findViewById(R.id.loading);
        load.setVisibility(View.GONE);

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


