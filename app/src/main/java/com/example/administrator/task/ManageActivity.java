package com.example.administrator.task;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.conn.params.ConnManagerPNames;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class ManageActivity extends ActionBarActivity implements ResultCallback<People.LoadPeopleResult>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnClickListener {

    public static Activity self;

    static String accountName;
    private GoogleApiClient mGoogleApiClient;
    boolean mSignInClicked;

    private PullToRefreshLayout mPullToRefreshLayout;
    ListView listView ;


    Context context = this;
    ArrayList<String> PTask = new ArrayList<String>();
    ArrayList<Integer> PTaskid =new ArrayList<Integer>();
    ArrayList<String> CTask = new ArrayList<String>();
    ArrayList<Integer> CTaskid = new ArrayList<Integer>();

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
;
        setContentView(R.layout.activity_manage);
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bar)));

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

        Intent intentstream = getIntent();
        accountName =intentstream.getStringExtra("account");
//        TextView mDebug = (TextView) findViewById(R.id.managedebug);
//        mDebug.setText(accountName);
//        System.out.println("account:"+accountName);
        if(accountName != null){

            Intent service=new Intent(this, TimeService.class);
            Bundle bundle= new Bundle();
            bundle.putString("account", accountName);
            service.putExtras(bundle);
            startService(service);
            // time service

            TextView mDebug = (TextView) findViewById(R.id.managedebug);
            mDebug.setText(accountName);
//            Button mSignout = (Button) findViewById(R.id.manage_sign_out);
//            Button mCreate = (Button) findViewById(R.id.CreateTask);
            ImageView mMore = (ImageView) findViewById(R.id.Pmore);
            ImageView mCMore = (ImageView) findViewById(R.id.Cmore);
            final TableRow PTRR = (TableRow)findViewById(R.id.PTR1);
            final TableRow CTRR = (TableRow)findViewById(R.id.CTR1);
            PTRR.setOnClickListener(ManageActivity.this);
            CTRR.setOnClickListener(ManageActivity.this);
            mMore.setOnClickListener(this);
            mCMore.setOnClickListener(this);
//            mCreate.setEnabled(true);
//            mCreate.setOnClickListener(this);
//            mSignout.setEnabled(true);
//            mSignout.setOnClickListener(this);

            final TableLayout PTaskTable = (TableLayout)findViewById(R.id.PrivateTask);
            final TableLayout CTaskTable = (TableLayout)findViewById(R.id.CommonTask);
            //----get TASK data from server


            mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
            ActionBarPullToRefresh.from(this)
                    // Mark All Children as pullable
                    .allChildrenArePullable()
                            // Set a OnRefreshListener
                    .listener(new OnRefreshListener() {
                        @Override
                        public void onRefreshStarted(View view) {
//                            Intent intent= new Intent(ManageActivity.this, ManageActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                            finish();
//
//                            startActivity(intent);
                            finish();
                            Intent i= getIntent();
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(i);
                            overridePendingTransition(R.anim.push_right_in,
                                    R.anim.push_left_out);
                            mPullToRefreshLayout.setRefreshing(false);
                        }
                    })

            // Finally commit the setup to our PullToRefreshLayout
                    .setup(mPullToRefreshLayout);

            SlidingMenu menu = new SlidingMenu(this);
            menu.setMode(SlidingMenu.RIGHT);
            menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
            menu.setBehindOffset(500);
            menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
            menu.setMenu(R.layout.slidemenu);

            listView=(ListView) findViewById(R.id.slidinglist);
            ArrayList<String>  values = new ArrayList<String>() ;
            values.add("Search");
            values.add("SMS Suggestion");
            values.add("Relative Task");
            values.add("QR Code Scan");
            values.add("Create Task");
            values.add("Log out");
            SAdapter adapter = new SAdapter(this,values);


            // Assign adapter to ListView
            listView.setAdapter(adapter);
            // ListView Item Click Listener
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    if(position==3) {
                        IntentIntegrator scanIntegrator = new IntentIntegrator(ManageActivity.this);
                        scanIntegrator.initiateScan();
                    }else if(position==0){
                        Intent intent= new Intent(ManageActivity.this, search.class);
                        Bundle bundle=new Bundle();
                        bundle.putString("Searchname","     x    ");
                        bundle.putString("account", accountName);
                        intent.putExtras(bundle);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }else if(position==1){
                        Intent intent= new Intent(ManageActivity.this, Suggestion.class);
                        Bundle bundle=new Bundle();
                        bundle.putString("account", accountName);
                        intent.putExtras(bundle);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }else if(position==2){
                        Intent intent= new Intent(ManageActivity.this, relative.class);
                        Bundle bundle=new Bundle();
                        bundle.putString("account", accountName);
                        intent.putExtras(bundle);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }else if(position==4){
                        DialogFragment newFragment = new CreateTaskSelect();
                        newFragment.show(getSupportFragmentManager(), "CREATE");
                    }else if(position==5){
                        if (mGoogleApiClient.isConnected()) {
                            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                            mGoogleApiClient.disconnect();
                            mGoogleApiClient.connect();
                            // updateUI(false);
                            System.err.println("LOG OUT ^^^^^^^^^^^^^^^^^^^^ SUCESS");
                            Intent intent= new Intent(ManageActivity.this, MainActivity.class);
                            startActivity(intent);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            ManageActivity.this.finish();
                        }
                    }

                }
            });



//sliding=========================

            final String request_url = "http://task-1123.appspot.com/viewmytask?userid="+accountName;
            AsyncHttpClient httpClient = new AsyncHttpClient();
            httpClient.get(request_url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] response) {


                    try {

                        JSONObject jObject = new JSONObject(new String(response));
                        JSONArray CommonTask = new JSONArray();
                        JSONArray CommonTaskId = new JSONArray();
                        JSONArray PrivateTask = new JSONArray();
                        JSONArray PrivateTaskId = new JSONArray();


                        System.out.println(jObject.isNull("jointaskname"));
                        System.out.println(jObject.isNull("pritaskname"));

                        if(!jObject.isNull("jointaskname")){
                            CommonTask = jObject.getJSONArray("jointaskname");
                            CommonTaskId =jObject.getJSONArray("jointaskid");
                        }

                        if(!jObject.isNull("pritaskname")) {
                            PrivateTask = jObject.getJSONArray("pritaskname");
                            PrivateTaskId = jObject.getJSONArray("pritaskid");
                        }

                        int tasknum;

                        if(CommonTask.length()>=0 && CommonTask.length()<3){
                            tasknum = CommonTask.length();
                        }
                        else tasknum = 3;
                        System.out.println("Common show task number: "+tasknum);
                        for(int i = 0; i < tasknum; i++){
                            CTask.add(CommonTask.getString(i));
                            CTaskid.add(CommonTaskId.getInt(i));
                        }
                        if(PrivateTask.length()>=0 && PrivateTask.length()<3){
                            tasknum = PrivateTask.length();
                        }else tasknum = 3;
                        System.out.println("Private show task number: "+tasknum);
                        for(int i = 0; i < tasknum; i++){
                            PTask.add(PrivateTask.getString(i));
                            PTaskid.add(PrivateTaskId.getInt(i));
                        }
                    } catch (JSONException j) {
                        System.out.println("JSON Error");
                    }

                    for(int i = 0; i < CTask.size(); i++){
                        if(i == 0){
                            TextView CT1 = (TextView)findViewById(R.id.Ctask1);
                            CT1.setText(CTask.get(i));
                            CT1.setPadding(0, 0, 0, 0);
                            CTRR.setPadding(0,0,0,0);

                        }else{
                            TableRow CTR;
                            try{
                                CTR = (TableRow)findViewById(100+i);
                                TextView CT2 = new TextView(context);
                                CT2.setText(CTask.get(i));
                                CT2.setTextColor(Color.WHITE);
                                CT2.setTextSize(23);
                                CTR.addView(CT2);
                            }catch (Exception e){
                                CTR = new TableRow(context);
                                CTR.setBackground(CTRR.getBackground());
                                CTR.setId(i + 200);

                                CTR.setOnClickListener(ManageActivity.this);
                                TextView CT2 = new TextView(context);
                                CT2.setText(CTask.get(i));
                                CT2.setTextColor(Color.WHITE);
                                CT2.setTextSize(23);
                                CT2.setPadding(0, 0, 0, 0);
                                CTR.addView(CT2);
                                CTR.setPadding(0,0,0,0);
                                CTaskTable.addView(CTR);
                            }

                        }
                    }

                    for(int i = 0; i < PTask.size(); i++){
                        if(i == 0){
                            TextView PT1 = (TextView)findViewById(R.id.Ptask1);
                            PT1.setText(PTask.get(i));
                            PT1.setPadding(0, 0, 0, 0);
                            PTRR.setPadding(0,0,0,0);

                        }else{
                            TableRow PTR;
                            try{
                                PTR = (TableRow)findViewById(100+i);
                                TextView PT2 = new TextView(context);
                                PT2.setText(PTask.get(i));
                                PT2.setTextColor(Color.WHITE);
                                PT2.setTextSize(23);
                                PTR.addView(PT2);
                            }catch (Exception e){
                                PTR = new TableRow(context);
                                PTR.setBackground(PTRR.getBackground());
                                PTR.setId(i + 100);

                                PTR.setOnClickListener(ManageActivity.this);
                                TextView PT2 = new TextView(context);
                                PT2.setText(PTask.get(i));
                                PT2.setTextColor(Color.WHITE);
                                PT2.setTextSize(23);
                                PT2.setPadding(0, 0, 0, 0);
                                PTR.addView(PT2);
                                PTR.setPadding(0,0,0,0);
                                PTaskTable.addView(PTR);
                            }

                        }
                    }

                }

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] errorResponse, Throwable e) {
                    Log.e("ManagePage", "There was a problem in retrieving the url : " + e.toString());
                }
            });
        }
        else{
            Intent intent= new Intent(this, ManageActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
//         TODO Auto-generated method stub
        switch (v.getId()) {
//            case R.id.manage_sign_out:
//                if (mGoogleApiClient.isConnected()) {
//                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
//                    mGoogleApiClient.disconnect();
//                    mGoogleApiClient.connect();
//                    // updateUI(false);
//                    System.err.println("LOG OUT ^^^^^^^^^^^^^^^^^^^^ SUCESS");
//                    Intent intent= new Intent(this, MainActivity.class);
//                    startActivity(intent);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                    this.finish();
//                }
//                break;
//            case R.id.CreateTask:
//                DialogFragment newFragment = new CreateTaskSelect();
//                newFragment.show(getSupportFragmentManager(), "CREATE");
////                Dialog CreateDialog = newFragment.getDialog();
////                Button positiveButton = CreateDialog.getActionBar(DialogInterface.BUTTON_POSITIVE);
//                break;
            case R.id.Pmore:
                Intent intent= new Intent(this, AllPrivate.class);
                Bundle bundle=new Bundle();
                bundle.putString("account", accountName);
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.PTR1:
                intent= new Intent(this, SinglePrivateTask.class);
                bundle=new Bundle();
                bundle.putInt("PTaskID", PTaskid.get(0));
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                break;
            case 101:
                intent= new Intent(this, SinglePrivateTask.class);
                bundle=new Bundle();
                bundle.putInt("PTaskID",PTaskid.get(1));
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                break;
            case 102:
                intent= new Intent(this, SinglePrivateTask.class);
                bundle=new Bundle();
                bundle.putInt("PTaskID",PTaskid.get(2));
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                break;
            case 201:
                intent= new Intent(this, SingleCommonTask.class);
                bundle=new Bundle();
                bundle.putInt("CTaskID",CTaskid.get(1));
                bundle.putString("account", accountName);
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                break;
            case 202:
                intent= new Intent(this, SingleCommonTask.class);
                bundle=new Bundle();
                bundle.putInt("CTaskID",CTaskid.get(2));
                bundle.putString("account", accountName);
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                break;
            case R.id.CTR1:
                intent= new Intent(this, SingleCommonTask.class);
                bundle=new Bundle();
                bundle.putInt("CTaskID", CTaskid.get(0));
                bundle.putString("account", accountName);
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                break;
            case R.id.Cmore:
                intent= new Intent(this, AllCommon.class);
                bundle= new Bundle();
                bundle.putString("account", accountName);
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                break;
        }


    }

    @Override
    public void onConnected(Bundle arg0) {
        // TODO Auto-generated method stub
        mSignInClicked = false;

        // updateUI(true);
        Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback(this);
    }


// app scan result
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            System.out.println(scanContent);
            Intent httpIntent = new Intent(Intent.ACTION_VIEW);
            httpIntent.setData(Uri.parse(scanContent));

            startActivity(httpIntent);
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        // TODO Auto-generated method stub
        mGoogleApiClient.connect();
        // updateUI(false);
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        // TODO Auto-generated method stub

    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }



    @Override
    public void onResult(People.LoadPeopleResult arg0) {
        // TODO Auto-generated method stub

    }

    public static class CreateTaskSelect extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.AlertDialogCustom);
            builder.setMessage(R.string.tasktype)
                    .setPositiveButton(R.string.common_task, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(getActivity(), com.example.administrator.task.CreateCommonTask.class);
                            Bundle bundle=new Bundle();
                            bundle.putString("account", accountName);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            self.finish();
                        }
                    })


                    .setNegativeButton(R.string.private_task, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(getActivity(), com.example.administrator.task.CreatePrivateTask.class);
                            Bundle bundle=new Bundle();
                            bundle.putString("account", accountName);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            self.finish();
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }

        @Override
        public void onStart()
        {
            super.onStart();

            Button pButton =  ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE);
            Button nButton =  ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_NEGATIVE);
//        ((AlertDialog) getDialog()).getWindow().setTitleColor(getResources().getColor(R.color.orange));
            pButton.setBackgroundColor(getResources().getColor(R.color.dialogcolor));
            pButton.setTextColor(getResources().getColor(R.color.white));
            nButton.setBackgroundColor(getResources().getColor(R.color.dialogcolor));
            nButton.setTextColor(getResources().getColor(R.color.white));
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

}