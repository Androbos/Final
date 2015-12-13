package com.example.administrator.task;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class EditCommonTask extends ActionBarActivity implements View.OnClickListener{
    Context context = this;
    protected GoogleApiClient mGoogleApiClient;

    String accountName;
    String CTaskName;
    String CTaskDescription;
    String CTaskDue;
    Integer CTaskID;
    Calendar c;
    CheckBox checkBox;
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_common_task);

        Intent intent = getIntent();
        accountName = intent.getStringExtra("account");
        CTaskID = intent.getIntExtra("taskid", 0);

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bar)));

        int abTitleId = getResources().getIdentifier("action_bar_title", "id", "android");
        findViewById(abTitleId).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditCommonTask.this, ManageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("account", accountName);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.push_right_in,
                        R.anim.push_left_out);
                finish();
            }
        });

        TextView User = (TextView)findViewById(R.id.createCdebug);
        User.setText(accountName);


        Button mCreate = (Button)findViewById(R.id.createC);
        mCreate.setOnClickListener(this);
        Button btn1 = (Button) findViewById(R.id.button1);
        Button btn2 = (Button) findViewById(R.id.button2);
        c = Calendar.getInstance();

        btn1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(EditCommonTask.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                                  int dayOfMonth) {
                                TextView text = (TextView) findViewById(R.id.taskdue);
                                monthOfYear+=1;
                                String tmp2 = String.valueOf(year)+"-"+String.valueOf(monthOfYear)+"-"+String.valueOf(dayOfMonth);
                                text.setText(tmp2);
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        btn2.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new TimePickerDialog(EditCommonTask.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // TODO Auto-generated method stub
                                TextView text = (TextView) findViewById(R.id.taskdue);
                                String minute0=String.valueOf(minute);
                                if(minute<10){
                                    minute0="0"+String.valueOf(minute);
                                }
                                String tmp=String.valueOf(c.get(Calendar.YEAR))+"-"+String.valueOf(c.get(Calendar.MONTH)+1)+"-"+String.valueOf(c.get(Calendar.DAY_OF_MONTH))+" "+String.valueOf(hourOfDay)+":"+ minute0;
                                text.setText(tmp);
                            }
                        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();

            }

        });

        final String request_url = "http://task-1123.appspot.com/viewsinglecommontask?taskid="+CTaskID;
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
                    CTaskName = jObject.getJSONArray("taskname").getString(0);
//                    System.out.println("here after Ptaskname");
                    CTaskDue = jObject.getJSONArray("due").getString(0);
                    CTaskDescription = jObject.getJSONArray("description").getString(0);
//                    System.out.println("here after Ptaskdescription");
//                    PTaskcreatetime = jObject.getJSONArray("create_time").getString(0);
//                    System.out.println("here after createtime");
                    EditText ptaskname = (EditText) findViewById(R.id.taskname);
                    EditText ptaskdue = (EditText) findViewById(R.id.taskdue);
                    EditText ptaskdescription = (EditText) findViewById(R.id.taskdiscrip);
                    ptaskname.setText(CTaskName);
                    ptaskdue.setText(CTaskDue);
                    ptaskdescription.setText(CTaskDescription);


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
        EditText TaskDescription = (EditText)findViewById(R.id.taskdiscrip);

        CTaskName = TaskName.getText().toString();
        CTaskDue = TaskDue.getText().toString();
        CTaskDescription = TaskDescription.getText().toString();
        CTaskID = (CTaskName+accountName).hashCode();

        checkBox = (CheckBox) findViewById(R.id.checkbox_id);
        int checked = 0;
        if (checkBox.isChecked()) {
            checked=1;
        }

        RequestParams params = new RequestParams();
        params.put("checked", checked);
        params.put("taskid", CTaskID);
        params.put("taskname", CTaskName);
        params.put("description", CTaskDescription);
        params.put("due", CTaskDue);
        AsyncHttpClient client = new AsyncHttpClient();

        client.post("http://task-1123.appspot.com/updatecommontask", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] response) {
                Log.w("async", "success!!!!");
                Toast.makeText(context, "Create Successful", Toast.LENGTH_SHORT).show();
                try {
                    Thread.sleep(100);                 //1000 milliseconds is one second.
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }


                Intent intent = new Intent(EditCommonTask.this, ManageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("account", accountName);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.push_right_in,
                        R.anim.push_left_out);
                EditCommonTask.this.finish();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e("Posting_to_blob", "There was a problem in retrieving the url : " + e.toString());
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

