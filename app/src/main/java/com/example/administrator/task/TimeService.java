package com.example.administrator.task;

import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TimeService extends Service {

    ArrayList<String> reminds= new ArrayList<String>();
    ArrayList<String> taskname= new ArrayList<String>();
    ArrayList<Calendar> remind=new ArrayList<Calendar>();
    String accountName;

    public TimeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        Log.i("liujun", "后台进程被创建。。。");

//服务启动广播接收器，使得广播接收器可以在程序退出后在后天继续执行，接收系统时间变更广播事件
        DataChangeReceiver receiver=new DataChangeReceiver();
        registerReceiver(receiver,new IntentFilter(Intent.ACTION_TIME_TICK));

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Bundle bundle = intent.getExtras();
        accountName =bundle.getString("account");
        Log.i("liujun", "后台进程。。。");
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {

        Log.i("liujun", "后台进程被销毁了。。。");

        super.onDestroy();
    }
    public class DataChangeReceiver extends BroadcastReceiver {
        public DataChangeReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO: This method is called when the BroadcastReceiver is receiving
            // an Intent broadcast.

            final String request_url = "http://task-1123.appspot.com/viewmytask?userid="+accountName;
            AsyncHttpClient httpClient = new AsyncHttpClient();
            httpClient.get(request_url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] response) {
                    try {

                        JSONObject jObject = new JSONObject(new String(response));
                        JSONArray re = new JSONArray();
                        JSONArray name = new JSONArray();
                        JSONArray id = new JSONArray();

                        if (!jObject.isNull("priremind")) {
                            id = jObject.getJSONArray("pritaskid");
                        }

                        if (!jObject.isNull("priremind")) {
                            re = jObject.getJSONArray("priremind");
                        }

                        if (!jObject.isNull("pritaskname")) {
                            name = jObject.getJSONArray("pritaskname");
                        }

                        Calendar now = Calendar.getInstance();
//                        mWakeLock.release();

                        for (int i = 0; i < name.length(); i++) {
                            String tmp = re.getString(i);
                            Calendar cal = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            if (tmp.equals("null")) {
                                continue;
                            }
                            cal.setTime(sdf.parse(tmp));// all done)

                            if(now.getTime().toString().equals(cal.getTime().toString())) {
                                PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
                                PowerManager.WakeLock mWakeLock = pm.newWakeLock((PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "YourServie");
                                mWakeLock.acquire();
                                showBox(com.example.administrator.task.TimeService.this, id.getInt(i), name.getString(i));
                                System.out.println("remind");
                                mWakeLock.release();
                            }
                        }

                    } catch (JSONException j) {
                        System.out.println("JSON Error");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] errorResponse, Throwable e) {
                    Log.e("ManagePage", "There was a problem in retrieving the url : " + e.toString());
                }
            });

        }

    }
    private void showBox(final Context context, final int id,String name) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Task Reminder");
        dialog.setMessage(name);
        dialog.setIcon(android.R.drawable.ic_dialog_info);
        AlertDialog.Builder builder = dialog.setPositiveButton("See the task", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                //点击后跳转到某个Activity
                Intent result = new Intent(context, SinglePrivateTask.class);
                Bundle bundle = new Bundle();
                bundle.putString("account", accountName);
                bundle.putInt("PTaskID", id);
                result.putExtras(bundle);
                result.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(result);
            }
        }).setNegativeButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.cancel();
                    }
                });
        ;
        AlertDialog mDialog = dialog.create();
        mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//设定为系统级警告，关键
        mDialog.show();
    }
}
