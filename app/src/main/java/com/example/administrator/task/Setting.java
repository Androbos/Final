package com.example.administrator.task;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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

    private Switch emailview;
    String accountName;
    Integer email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        emailview = (Switch) findViewById(R.id.mySwitch);

        Intent intentstream = getIntent();
        accountName =intentstream.getStringExtra("account");

        final String request_url = "http://task-1123.appspot.com/setting?userid="+accountName;
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] response) {

                    email=0;
                try {

                    JSONObject jObject = new JSONObject(new String(response));
                    JSONArray emails = new JSONArray();


                    if (!jObject.isNull("email")) {
                        email = jObject.getJSONArray("email").getInt(0);
                    }
                } catch (JSONException j) {
                    System.out.println("JSON Error");
                }

                if (email == 1) {
                    emailview.setChecked(true);
                } else {
                    emailview.setChecked(false);
                }
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
                    email = 1;
                } else {
                    email = 0;
                }

            }
        });

        Button btn1 = (Button) findViewById(R.id.Update);
        btn1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                RequestParams params = new RequestParams();
                params.put("email", email);
                AsyncHttpClient client = new AsyncHttpClient();
                client.post("http://task-1123.appspot.com/updatesetting?userid="+accountName, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] response) {
                        Log.w("async", "success!!!!");
                    }
                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] errorResponse, Throwable e) {
                        Log.e("Posting_to_blob", "There was a problem in retrieving the url : " + e.toString());
                    }
                });

            }
        });
    }
}