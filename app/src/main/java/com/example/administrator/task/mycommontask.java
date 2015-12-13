package com.example.administrator.task;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
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

import com.example.administrator.task.SlideView2.OnSlideListener;
import com.example.administrator.task.MessageItem;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class mycommontask extends ActionBarActivity implements AdapterView.OnItemClickListener, View.OnClickListener,
        OnSlideListener{
    String accountName;
    String email;
    private ListViewCompat2 mListView;

    private List<MessageItem2> mMessageItems = new ArrayList<MessageItem2>();

    private SlideAdapter SA;

    Context context = this;
    private static final String TAG = "MainActivity";
    private SlideView2 mLastSlideViewWithStatusOn;

    final private ArrayList<String> PTask1 = new ArrayList<String>();
    final ArrayList<Integer> PTaskID1 = new ArrayList<Integer>();
    final ArrayList<Integer> subtaskid = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mycommontask);
        Intent intentstream = getIntent();
        accountName = intentstream.getStringExtra("account");
        email=intentstream.getStringExtra("email");

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bar)));
        int abTitleId = getResources().getIdentifier("action_bar_title", "id", "android");
        findViewById(abTitleId).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mycommontask.this, ManageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("account", accountName);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.push_right_in,
                        R.anim.push_left_out);
                finish();
            }
        });



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
                    JSONArray PrivateTaskdue;
                    JSONArray Jsubtaskid;

                    PrivateTask = jObject.getJSONArray("taskname");
                    PrivateTaskID = jObject.getJSONArray("taskid");
                    PrivateTaskdue = jObject.getJSONArray("due");
                    Jsubtaskid=jObject.getJSONArray("jointask_id");

                    for (int j = 0; j < Jsubtaskid.length(); j++) {
                        subtaskid.add(Jsubtaskid.getInt(j));
                    }

                    System.out.println(PrivateTask.length());

//                    for (int i = 0; i < PrivateTask.length(); i++) {
//                        PTask.add(PrivateTask.getString(i));
//                        PTaskID.add(PrivateTaskID.getInt(i));
//                    }

                    mListView = (ListViewCompat2) findViewById(R.id.Tasklist);

                    for (int i = 0; i < PrivateTask.length(); i++) {
                            PTask1.add(PrivateTask.getString(i));
                            PTaskID1.add(PrivateTaskID.getInt(i));
                            MessageItem2 item = new MessageItem2();
                            item.msg = PrivateTask.getString(i);
                            item.time = PrivateTaskdue.getString(i);
                            mMessageItems.add(item);
                    }


                    SA = new SlideAdapter(mMessageItems);

                    mListView.setAdapter(SA);
                    mListView.setOnItemClickListener(mycommontask.this);
                    ViewGroup.LayoutParams lp = mListView.getLayoutParams();
                    lp.height = 120 * mMessageItems.size();
                    mListView.setLayoutParams(lp);

                } catch (JSONException j) {
                    System.out.println("JSON Error");
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e("ManagePage", "There was a problem in retrieving the url : " + e.toString());
            }
        });
        final TextView load =(TextView) findViewById(R.id.loading);
        load.setVisibility(View.GONE);
    }

    private class SlideAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        private List<MessageItem2> MessageItems = new ArrayList<MessageItem2>();

        SlideAdapter(List<MessageItem2> MessageItems_) {
            super();
            this.MessageItems=MessageItems_;
            this.mInflater = getLayoutInflater();
        }

        @Override
        public int getCount() {
            return this.MessageItems.size();
        }

        @Override
        public Object getItem(int position) {
            return this.MessageItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            SlideView2 slideView = (SlideView2) convertView;
            if (slideView == null) {
                View itemView = this.mInflater.inflate(R.layout.list_item, null);

                slideView = new SlideView2(mycommontask.this);
                slideView.setContentView(itemView);

                holder = new ViewHolder(slideView);
                slideView.setOnSlideListener(mycommontask.this);
                slideView.setTag(holder);
            } else {
                holder = (ViewHolder) slideView.getTag();
            }
            MessageItem2 item = this.MessageItems.get(position);
            item.slideView = slideView;
            item.slideView.shrink();

//            holder.icon.setImageResource(item.iconRes);
//            holder.title.setText(item.title);
            holder.msg.setText(item.msg);
            holder.time.setText(item.time);
            holder.finishHolder.setOnClickListener(mycommontask.this);

            return slideView;
        }

    }
    private static class ViewHolder {
        public ImageView icon;
        public TextView title;
        public TextView msg;
        public TextView time;
        public ViewGroup finishHolder;

        ViewHolder(View view) {
//            icon = (ImageView) view.findViewById(R.id.icon);
//            title = (TextView) view.findViewById(R.id.title);
            msg = (TextView) view.findViewById(R.id.msg);
            time = (TextView) view.findViewById(R.id.time);
            finishHolder =  (ViewGroup)view.findViewById(R.id.holder1);

        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        try {
            Thread.sleep(100);                 //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

//===========================
            if(!mListView.isScroll){
                Log.e(TAG, "onItemClick position=" + position);
                Toast.makeText(context, "click", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, SingleCommonTask.class);
                Bundle bundle=new Bundle();
                int P = position;
                bundle.putInt("CTaskID", PTaskID1.get(P));
                bundle.putString("account", accountName);
                bundle.putString("email", email);
                if(subtaskid.contains(PTaskID1.get(P))){
                    bundle.putBoolean("hasJoined",true);
                }else{
                    bundle.putBoolean("hasJoined",false);
                }
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.push_right_in,
                        R.anim.push_left_out);
            }

    }

    @Override
    public void onSlide(View view, int status) {
        if (mLastSlideViewWithStatusOn != null && mLastSlideViewWithStatusOn != view) {
            mLastSlideViewWithStatusOn.shrink();
        }

        if (status == SLIDE_STATUS_ON) {
            mLastSlideViewWithStatusOn = (SlideView2) view;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.holder1){

            int position = mListView.getPositionForView(v);


            int finishid = PTaskID1.get(position);
            RequestParams params = new RequestParams();
            params.put("taskid", finishid);

            AsyncHttpClient client = new AsyncHttpClient();
            client.post("http://task-1123.appspot.com/deletecommontask", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] response) {
                    Log.w("async", "success!!!!");
                    Toast.makeText(context, "delete", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] errorResponse, Throwable e) {
                    Log.e("Posting_to_blob", "There was a problem in retrieving the url : " + e.toString());
                }
            });

            try {
                Thread.sleep(100);                 //1000 milliseconds is one second.
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            Intent intent= new Intent(this, mycommontask.class);
            Bundle bundle=new Bundle();
            bundle.putString("account", accountName);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            overridePendingTransition(R.anim.push_right_in,
                    R.anim.push_left_out);
            this.finish();

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

