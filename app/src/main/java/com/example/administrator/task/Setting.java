package com.example.administrator.task;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class Setting extends ActionBarActivity {

    private Switch gview;
    private Switch dview;
    private Switch evview;
    private Switch emailview;
    String accountName;
    Integer email_notification;
    Integer email_visible;
    Integer gender_visible;
    Integer dob_visible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Intent intentstream = getIntent();
        accountName =intentstream.getStringExtra("account");
        emailview = (Switch) findViewById(R.id.mySwitch);
        evview = (Switch) findViewById(R.id.email_visible);
        gview = (Switch) findViewById(R.id.gender_visible);
        dview = (Switch) findViewById(R.id.dob_visible);

        final TextView load =(TextView) findViewById(R.id.loading);

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bar)));
        int abTitleId = getResources().getIdentifier("action_bar_title", "id", "android");
        findViewById(abTitleId).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Setting.this, ManageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("account", accountName);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.push_right_in,
                        R.anim.push_left_out);
                finish();
            }
        });

        final String request_url = "http://task-1123.appspot.com/setting?userid="+accountName;
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] response) {

                email_notification = 0;
                try {

                    JSONObject jObject = new JSONObject(new String(response));
                    JSONArray emails = new JSONArray();


                    if (!jObject.isNull("email_notification")) {
                        email_notification = jObject.getJSONArray("email_notification").getInt(0);
                    }

                    if (!jObject.isNull("gender_visible")) {
                        gender_visible = jObject.getJSONArray("gender_visible").getInt(0);
                    }

                    if (!jObject.isNull("dob_visible")) {
                        dob_visible = jObject.getJSONArray("dob_visible").getInt(0);
                    }

                    if (!jObject.isNull("email_visible")) {
                        email_visible = jObject.getJSONArray("email_visible").getInt(0);
                    }

                } catch (JSONException j) {
                    System.out.println("JSON Error");
                }

                if (email_notification == 1) {
                    emailview.setChecked(true);
                } else {
                    emailview.setChecked(false);
                }

                if (gender_visible == 1) {
                    gview.setChecked(true);
                } else {
                    gview.setChecked(false);
                }

                if (dob_visible == 1) {
                    dview.setChecked(true);
                } else {
                    dview.setChecked(false);
                }

                if (email_visible == 1) {
                    evview.setChecked(true);
                } else {
                    evview.setChecked(false);
                }

                final TextView load =(TextView) findViewById(R.id.loading);
                load.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }

        });

        emailview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    email_notification = 1;
                } else {
                    email_notification = 0;
                }

            }
        });

        evview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    email_visible = 1;
                } else {
                    email_visible = 0;
                }

            }
        });

        gview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    gender_visible = 1;
                } else {
                    gender_visible = 0;
                }
            }
        });

        dview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    dob_visible = 1;
                } else {
                    dob_visible = 0;
                }

            }
        });

        Button btn1 = (Button) findViewById(R.id.Update);
        btn1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                RequestParams params = new RequestParams();
                params.put("email_notification", email_notification);
                params.put("gender_visible", gender_visible);
                params.put("dob_visible", dob_visible);
                params.put("email_visible", email_visible);
                params.put("userid", accountName);
                AsyncHttpClient client = new AsyncHttpClient();
                client.post("http://task-1123.appspot.com/updatesetting", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] response) {
                        Log.w("async", "success!!!!");

                        Intent intent = new Intent(Setting.this, Setting.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("account", accountName);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        overridePendingTransition(R.anim.push_right_in,
                                R.anim.push_left_out);
                        finish();

                    }

                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] errorResponse, Throwable e) {
                        Log.e("Posting_to_blob", "There was a problem in retrieving the url : " + e.toString());
                    }
                });

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