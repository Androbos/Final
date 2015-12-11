package com.example.administrator.task;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class SinglePrivateTask extends ActionBarActivity {
    Context context =this;
    Integer TaskId;
    String accountname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_private_task);

        Intent intent = getIntent();
        TaskId = intent.getIntExtra("PTaskID", 0);
        accountname =intent.getStringExtra("account");
        System.out.println(TaskId);

        ImageView edit = (ImageView) findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(context,EditPrivateTask.class);
                Bundle bundle = new Bundle();
                bundle.putInt("taskid", TaskId);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        final String request_url = "http://task-1123.appspot.com/viewsingleprivatetask?taskid="+TaskId;
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] response) {
                try {

                    JSONObject jObject = new JSONObject(new String(response));
                    String PTaskname;
                    String PTaskdue;
                    String PTaskdescription;
                    String PTaskcreatetime;
//                        ArrayList<String> CTask = new ArrayList<String>();
//                        ArrayList<String> PTask;
//                        CommonTask = jObject.getJSONArray("taskname");
//                    System.out.println("here before Ptaskname");
                    PTaskname = jObject.getJSONArray("taskname").getString(0);
//                    System.out.println("here after Ptaskname");
                    PTaskdue = jObject.getJSONArray("due").getString(0);
                    PTaskdescription = jObject.getJSONArray("description").getString(0);
//                    System.out.println("here after Ptaskdescription");
                    PTaskcreatetime = jObject.getJSONArray("create_time").getString(0);
//                    System.out.println("here after createtime");
                    TextView ptaskname = (TextView) findViewById(R.id.ptaskname);
                    TextView ptaskdue = (TextView) findViewById(R.id.ptaskdue);
                    TextView ptaskdescription = (TextView) findViewById(R.id.ptaskdescript);
                    TextView ptaskcreatetime = (TextView) findViewById(R.id.ptaskcreatetime);
                    ptaskname.setText(PTaskname);
                    ptaskdue.setText(PTaskdue);
                    ptaskdescription.setText(PTaskdescription);
                    ptaskcreatetime.setText(PTaskcreatetime);

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
