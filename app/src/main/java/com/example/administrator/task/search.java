package com.example.administrator.task;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class search extends ActionBarActivity {

    String accountName;
    String searchname;

    ListView listView;
    Button btn;
    EditText ET;
    String searchthistime;

    ArrayList<String> taskname = new ArrayList<String>();
    ArrayList<Integer> taskid =new ArrayList<Integer>();

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bar)));


        Intent intentstream = getIntent();
        accountName =intentstream.getStringExtra("account");
        searchname =intentstream.getStringExtra("Searchname");


        final String request_url = "http://task-1123.appspot.com/searchtask?searchname="+searchname;
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] response) {


                try {

                    JSONObject jObject = new JSONObject(new String(response));
                    JSONArray id = new JSONArray();
                    JSONArray name = new JSONArray();

                    if (!jObject.isNull("taskid")) {
                        id = jObject.getJSONArray("taskid");
                    }

                    if (!jObject.isNull("taskname")) {
                        name = jObject.getJSONArray("taskname");
                    }

                    for (int i = 0; i < name.length(); i++) {
                        taskname.add(name.getString(i));
                        taskid.add(id.getInt(i));
                        System.out.println(taskname.size());

                    }

                } catch (JSONException j) {
                    System.out.println("JSON Error");
                }

                listView = (ListView) findViewById(R.id.searchlist);
                System.out.println(taskname.size());
                SAdapter adapter = new SAdapter(search.this, taskname);

                // Assign adapter to ListView
                listView.setAdapter(adapter);
                // ListView Item Click Listener
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {

                        Integer itemValue = taskid.get(position);

                        Intent intent = new Intent(search.this, SingleCommonTask.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("account", accountName);
                        bundle.putInt("CTaskID", itemValue);
                        intent.putExtras(bundle);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }
                });

            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e("ManagePage", "There was a problem in retrieving the url : " + e.toString());
            }
        });


        ET= (EditText)findViewById(R.id.ET1);
        System.out.println(ET);
        btn=(Button) findViewById(R.id.btn);
        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(search.this, search.class);
                Bundle bundle = new Bundle();
                bundle.putString("account", accountName);
                searchthistime=ET.getText().toString();
                bundle.putString("Searchname",searchthistime);
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }


}
