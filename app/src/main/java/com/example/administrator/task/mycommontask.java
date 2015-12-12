
package com.example.administrator.task;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.task.SlideView.OnSlideListener;
import com.example.administrator.task.MessageItem;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class mycommontask extends ActionBarActivity{
    String accountName;
    private ListView listView;
    Context context = this;
    private static final String TAG = "MainActivity";

    final private ArrayList<String> PTask = new ArrayList<String>();
    final ArrayList<Integer> PTaskID = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycommontask);

        Intent intentstream = getIntent();
        accountName = intentstream.getStringExtra("account");

        TextView User = (TextView)findViewById(R.id.allpdebug);
        User.setText(accountName);

        final String request_url = "http://task-1123.appspot.com/viewmytask?userid="+accountName;
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] response) {
                try {

                    JSONObject jObject = new JSONObject(new String(response));
                    JSONArray PrivateTask;
                    JSONArray PrivateTaskID;

                    PrivateTask = jObject.getJSONArray("taskname");
                    PrivateTaskID =jObject.getJSONArray("taskid");
                    System.out.println(PrivateTask.length());

                    for (int i = 0; i < PrivateTask.length(); i++) {
                        PTask.add(PrivateTask.getString(i));
                        PTaskID.add(PrivateTaskID.getInt(i));
                    }

                    listView = (ListView) findViewById(R.id.Tasklist);

                    SAdapter a =new SAdapter(mycommontask.this,PTask);
                    listView.setAdapter(a);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {

                            Integer itemValue = PTaskID.get(position);

                            Intent intent = new Intent(mycommontask.this, SingleCommonTask.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("account", accountName);
                            bundle.putInt("CTaskID", itemValue);
                            intent.putExtras(bundle);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        }
                    });

                } catch (JSONException j) {
                    System.out.println("JSON Error");
                }

            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e("ManagePage", "There was a problem in retrieving the url : " + e.toString());
            }
        });
    }

}

