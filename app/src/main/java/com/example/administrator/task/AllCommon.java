
package com.example.administrator.task;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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

import com.example.administrator.task.SlideView.OnSlideListener;
import com.example.administrator.task.MessageItem;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AllCommon extends ActionBarActivity implements AdapterView.OnItemClickListener, View.OnClickListener,
        OnSlideListener {
    String accountName;
    private ListView listView;
    Context context = this;
    private static final String TAG = "MainActivity";

    private ListViewCompat mListView;

    private List<MessageItem> mMessageItems = new ArrayList<MessageItem>();

    private SlideAdapter mSlideAdapter;

    private SlideView mLastSlideViewWithStatusOn;

    final private ArrayList<String> PTask = new ArrayList<String>();
    final ArrayList<Integer> PTaskID = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_common);

        Intent intentstream = getIntent();
        accountName = intentstream.getStringExtra("account");

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

                    PrivateTask = jObject.getJSONArray("taskname");
                    PrivateTaskID =jObject.getJSONArray("taskid");
                    System.out.println(PrivateTask.length());

                    for (int i = 0; i < PrivateTask.length(); i++) {
                        PTask.add(PrivateTask.getString(i));
                        PTaskID.add(PrivateTaskID.getInt(i));
                    }

                    mListView = (ListViewCompat) findViewById(R.id.Tasklist);
                    for (int i = 0; i < PrivateTask.length(); i++) {
                        MessageItem item = new MessageItem();
                        item.msg =PrivateTask.getString(i) ;
                        mMessageItems.add(item);
                    }
                    mListView.setAdapter(new SlideAdapter(mMessageItems));
                    mListView.setOnItemClickListener(AllCommon.this);



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
    private class SlideAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        private List<MessageItem> MessageItems = new ArrayList<MessageItem>();

        SlideAdapter(List<MessageItem> MessageItems_) {
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
            SlideView slideView = (SlideView) convertView;
            if (slideView == null) {
                View itemView = this.mInflater.inflate(R.layout.list_item, null);

                slideView = new SlideView(AllCommon.this);
                slideView.setContentView(itemView);

                holder = new ViewHolder(slideView);
                slideView.setOnSlideListener(AllCommon.this);
                slideView.setTag(holder);
            } else {
                holder = (ViewHolder) slideView.getTag();
            }
            MessageItem item = this.MessageItems.get(position);
            item.slideView = slideView;
            item.slideView.shrink();

//            holder.icon.setImageResource(item.iconRes);
//            holder.title.setText(item.title);
            holder.msg.setText(item.msg);
//            holder.time.setText(item.time);
            holder.deleteHolder.setOnClickListener(AllCommon.this);
            holder.finishHolder.setOnClickListener(AllCommon.this);

            return slideView;
        }

    }

//    public class MessageItem {
//        //        public int iconRes;
////        public String title;
//        public String msg;
//        public String id;
//        //        public String time;
//        public SlideView slideView;
//    }

    private static class ViewHolder {
        public ImageView icon;
        public TextView title;
        public TextView msg;
        public TextView time;
        public ViewGroup deleteHolder;
        public ViewGroup finishHolder;

        ViewHolder(View view) {
//            icon = (ImageView) view.findViewById(R.id.icon);
//            title = (TextView) view.findViewById(R.id.title);
            msg = (TextView) view.findViewById(R.id.msg);
//            time = (TextView) view.findViewById(R.id.time);
            deleteHolder = (ViewGroup)view.findViewById(R.id.holder2);
            finishHolder =  (ViewGroup)view.findViewById(R.id.holder1);

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        try {
            Thread.sleep(500);                 //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        if(!mListView.isScroll){
            Log.e(TAG, "onItemClick position=" + position);
            Toast.makeText(context, "click", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, SingleCommonTask.class);
            Bundle bundle=new Bundle();
            int P = position;
            bundle.putInt("CTaskID", PTaskID.get(P));
            intent.putExtras(bundle);
            startActivity(intent);
        }

    }

    @Override
    public void onSlide(View view, int status) {
        if (mLastSlideViewWithStatusOn != null && mLastSlideViewWithStatusOn != view) {
            mLastSlideViewWithStatusOn.shrink();
        }

        if (status == SLIDE_STATUS_ON) {
            mLastSlideViewWithStatusOn = (SlideView) view;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.holder2) {
            int position = mListView.getPositionForView(v);
            int deletionid = PTaskID.get(position);

            RequestParams params = new RequestParams();
            params.put("taskid", deletionid);
            AsyncHttpClient client = new AsyncHttpClient();
            client.post("http://task-1123.appspot.com/deletecommontask", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] response) {
                    Log.w("async", "success!!!!");
                    Toast.makeText(context, "delete Successful", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] errorResponse, Throwable e) {
                    Log.e("Posting_to_blob", "There was a problem in retrieving the url : " + e.toString());
                }
            });

            Intent intent= new Intent(this, AllCommon.class);
            Bundle bundle=new Bundle();
            bundle.putString("account", accountName);
            intent.putExtras(bundle);
            startActivity(intent);

        }
        else if (v.getId() == R.id.holder1){
            Log.e(TAG, "onClick v=" + v);
            Toast.makeText(context, "finish", Toast.LENGTH_SHORT).show();

        }
    }

}
