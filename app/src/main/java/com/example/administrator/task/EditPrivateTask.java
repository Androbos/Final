package com.example.administrator.task;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class EditPrivateTask extends ActionBarActivity implements View.OnClickListener{
    Context context = this;
    protected GoogleApiClient mGoogleApiClient;
    String accountName;
    String PTaskName;
    String PTaskDiscription;
    String PTaskDue;
    Integer PTaskID;
    String dd;
    String hh;
    String mm;
    Calendar c;
    //    String PTaskCreateTime;
//    Integer PTaskFinished;
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_private_task);

        Intent intent = getIntent();
        PTaskID = intent.getIntExtra("taskid",0);
        accountName=intent.getStringExtra("account");

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bar)));
        int abTitleId = getResources().getIdentifier("action_bar_title", "id", "android");
        findViewById(abTitleId).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditPrivateTask.this, ManageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("account", accountName);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.push_right_in,
                        R.anim.push_left_out);
                finish();
            }
        });


//        TextView User = (TextView)findViewById(R.id.createPdebug);
//        User.setText(accountName);


        Button mCreate = (Button)findViewById(R.id.createP);
        mCreate.setOnClickListener(this);
//Time setting part==========================================

        Button btn1 = (Button) findViewById(R.id.button1);
        Button btn2 = (Button) findViewById(R.id.button2);
        c = Calendar.getInstance();

        final int[] y = {0,0,0};

        btn1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(EditPrivateTask.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                                  int dayOfMonth) {
                                TextView text = (TextView) findViewById(R.id.taskdue);

                                monthOfYear += 1;
                                y[0] = year;
                                y[1] = monthOfYear;
                                y[2] = dayOfMonth;
                                String month0 = String.valueOf(y[1]);
                                String day0 = String.valueOf(y[2]);
                                if (y[1] < 10) {
                                    month0 = "0" + String.valueOf(y[1]);
                                }
                                if (y[2] < 10) {
                                    day0 = "0" + String.valueOf(y[2]);
                                }
                                String tmp2 = String.valueOf(year) + "-" + month0 + "-" + day0;
                                text.setText(tmp2);
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        btn2.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new TimePickerDialog(EditPrivateTask.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // TODO Auto-generated method stub
                                TextView text = (TextView) findViewById(R.id.taskdue);
                                String minute0=String.valueOf(minute);
                                String month0=String.valueOf(y[1]);
                                String day0=String.valueOf(y[2]);
                                String hour0=String.valueOf(hourOfDay);
                                if(minute<10){
                                    minute0="0"+String.valueOf(minute);
                                }
                                if(y[1]<10){
                                    month0="0"+String.valueOf(y[1]);
                                }
                                if(y[2]<10){
                                    day0="0"+String.valueOf(y[2]);
                                }
                                if(hourOfDay<10){
                                    hour0="0"+String.valueOf(hourOfDay);
                                }
                                String tmp=String.valueOf(y[0])+"-"+month0+"-"+day0+" "+hour0+":"+ minute0;
                                text.setText(tmp);
                            }
                        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();

            }

        });


        final String request_url = "http://task-1123.appspot.com/viewsingleprivatetask?taskid="+PTaskID;
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] response) {
                try {

                    JSONObject jObject = new JSONObject(new String(response));
//                        ArrayList<String> CTask = new ArrayList<String>();
//                        ArrayList<String> PTask;
//                        CommonTask = jObject.getJSONArray("taskname");
//                    System.out.println("here before Ptaskname");
                    PTaskName = jObject.getJSONArray("taskname").getString(0);
//                    System.out.println("here after Ptaskname");
                    PTaskDue = jObject.getJSONArray("due").getString(0);
                    PTaskDiscription = jObject.getJSONArray("description").getString(0);
//                    System.out.println("here after Ptaskdescription");
//                    PTaskcreatetime = jObject.getJSONArray("create_time").getString(0);
//                    System.out.println("here after createtime");
                    EditText ptaskname = (EditText) findViewById(R.id.taskname);
                    EditText ptaskdue = (EditText) findViewById(R.id.taskdue);
                    EditText ptaskdescription = (EditText) findViewById(R.id.taskdiscrip);
                    ptaskname.setText(PTaskName);
                    ptaskdue.setText(PTaskDue);
                    ptaskdescription.setText(PTaskDiscription);


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

    @Override
    public void onClick(View v) {
        EditText TaskName =(EditText)findViewById(R.id.taskname);
        EditText TaskDue = (EditText)findViewById(R.id.taskdue);
        EditText TaskDiscription = (EditText)findViewById(R.id.taskdiscrip);
        EditText d =(EditText)findViewById(R.id.d);
        EditText h = (EditText)findViewById(R.id.h);
        EditText m = (EditText)findViewById(R.id.m);

        PTaskName = TaskName.getText().toString();
        PTaskDue = TaskDue.getText().toString();
        PTaskDiscription = TaskDiscription.getText().toString();
        dd = d.getText().toString();
        hh = h.getText().toString();
        mm = m.getText().toString();
//        PTaskID = (PTaskName+accountName).hashCode();

        if(PTaskName.equals("")||PTaskDue.equals("")){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);
            // set title
            alertDialogBuilder.setTitle("Your Title");
            // set dialog message
            alertDialogBuilder
                    .setTitle("Error")
                    .setMessage("Task name or due missing!")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, close
                            // current activity
                            Intent intent = new Intent(EditPrivateTask.this, EditPrivateTask.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("account", accountName);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            overridePendingTransition(R.anim.push_right_in,
                                    R.anim.push_left_out);
                            EditPrivateTask.this.finish();
                        }
                    });
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }else {

            RequestParams params = new RequestParams();
            params.put("taskname", PTaskName);
//        params.put("creator", accountName);
            params.put("description", PTaskDiscription);
            params.put("due", PTaskDue);
            params.put("taskid", PTaskID);
            params.put("d", dd);
            params.put("h", hh);
            params.put("m", mm);
            AsyncHttpClient client = new AsyncHttpClient();
            client.post("http://task-1123.appspot.com/updateprivatetask", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] response) {
                    Toast.makeText(context, "Update Successful", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(EditPrivateTask.this, SinglePrivateTask.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("PTaskID", PTaskID);
                    intent.putExtras(bundle);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(R.anim.push_right_in,
                            R.anim.push_left_out);
                    EditPrivateTask.this.finish();
                }

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] errorResponse, Throwable e) {
                    Log.e("Posting_to_blob", "There was a problem in retrieving the url : " + e.toString());
                }
            });
        }


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
