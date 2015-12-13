package com.example.administrator.task;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
    String accountName;

//    RelativeLayout r1;
//    LinearLayout l1;
//    LinearLayout l2;
//    LinearLayout l3;
//    TextView load;

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_private_task);
        final RelativeLayout r1= (RelativeLayout)findViewById(R.id.r1);
        final LinearLayout l1=(LinearLayout)findViewById(R.id.l1);
        final LinearLayout l2=(LinearLayout)findViewById(R.id.l2);
        final LinearLayout l3=(LinearLayout)findViewById(R.id.l3);
        final TextView ll =(TextView)findViewById(R.id.loading);
        ll.setVisibility(View.VISIBLE);
        r1.setVisibility(View.GONE);
        l1.setVisibility(View.GONE);
        l2.setVisibility(View.GONE);
        l3.setVisibility(View.GONE);


        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bar)));
        int abTitleId = getResources().getIdentifier("action_bar_title", "id", "android");
        findViewById(abTitleId).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SinglePrivateTask.this, ManageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("account", accountName);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.push_right_in,
                        R.anim.push_left_out);
                finish();
            }
        });

        Intent intent = getIntent();
        TaskId = intent.getIntExtra("PTaskID", 0);
        accountName =intent.getStringExtra("account");
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
                overridePendingTransition(R.anim.push_right_in,
                        R.anim.push_left_out);
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

                    ll.setVisibility(View.GONE);
                    r1.setVisibility(View.VISIBLE);
                    l1.setVisibility(View.VISIBLE);
                    l2.setVisibility(View.VISIBLE);
                    l3.setVisibility(View.VISIBLE);


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
