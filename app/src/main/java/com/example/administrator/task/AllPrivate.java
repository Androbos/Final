
package com.example.administrator.task;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.view.menu.MenuView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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

public class AllPrivate extends ActionBarActivity implements AdapterView.OnItemClickListener, View.OnClickListener,
        OnSlideListener {
    String accountName;
    private ListView listView;
    Context context = this;
    private static final String TAG = "MainActivity";

    private ListViewCompat mListView;
    private ListViewCompat mListViewfinish;
    private ListViewCompat mListViewoverdue;

    private List<MessageItem> mMessageItems = new ArrayList<MessageItem>();
    private List<MessageItem> mMessageItems2 = new ArrayList<MessageItem>();
    private List<MessageItem> mMessageItems3 = new ArrayList<MessageItem>();

    private SlideAdapter SA;
    private SlideAdapter SB;
    private SlideAdapter SC;


    private SlideView mLastSlideViewWithStatusOn;

    final private ArrayList<String> PTask1 = new ArrayList<String>();
    final ArrayList<Integer> PTaskID1 = new ArrayList<Integer>();
    final private ArrayList<String> PTask2 = new ArrayList<String>();
    final ArrayList<Integer> PTaskID2 = new ArrayList<Integer>();
    final private ArrayList<String> PTask3 = new ArrayList<String>();
    final ArrayList<Integer> PTaskID3 = new ArrayList<Integer>();
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_all_private);


        Intent intentstream = getIntent();
        accountName = intentstream.getStringExtra("account");

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bar)));
        int abTitleId = getResources().getIdentifier("action_bar_title", "id", "android");
        findViewById(abTitleId).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllPrivate.this, ManageActivity.class);
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
                    JSONArray PrivateTaskfinished;
                    JSONArray PrivateTaskoverdue;

                    PrivateTask = jObject.getJSONArray("pritaskname");
                    PrivateTaskID =jObject.getJSONArray("pritaskid");
                    PrivateTaskfinished=jObject.getJSONArray("prifinished");
                    PrivateTaskoverdue=jObject.getJSONArray("prioverdue");
                    PrivateTaskdue=jObject.getJSONArray("pridue");

                    System.out.println(PrivateTask.length());

//                    for (int i = 0; i < PrivateTask.length(); i++) {
//                        PTask.add(PrivateTask.getString(i));
//                        PTaskID.add(PrivateTaskID.getInt(i));
//                    }

                    mListView = (ListViewCompat) findViewById(R.id.Tasklist);
                    mListViewfinish = (ListViewCompat) findViewById(R.id.Tasklist2);
                    mListViewoverdue = (ListViewCompat) findViewById(R.id.Tasklist3);

                    for (int i = 0; i < PrivateTask.length(); i++) {
                        if(PrivateTaskfinished.getInt(i)==1){
                            PTask2.add(PrivateTask.getString(i));
                            PTaskID2.add(PrivateTaskID.getInt(i));
                            MessageItem item = new MessageItem();
                            item.msg =PrivateTask.getString(i);
                            item.time=PrivateTaskdue.getString(i);
                            mMessageItems2.add(item);
                        }
                        else{
                            if(PrivateTaskoverdue.getInt(i)==0){
                                PTask1.add(PrivateTask.getString(i));
                                PTaskID1.add(PrivateTaskID.getInt(i));
                                MessageItem item = new MessageItem();
                                item.msg =PrivateTask.getString(i) ;
                                item.time=PrivateTaskdue.getString(i);
                                mMessageItems.add(item);
                            }else{
                                PTask3.add(PrivateTask.getString(i));
                                PTaskID3.add(PrivateTaskID.getInt(i));
                                MessageItem item = new MessageItem();
                                item.msg =PrivateTask.getString(i) ;
                                item.time=PrivateTaskdue.getString(i);
                                mMessageItems3.add(item);
                            }
                            }

                    }


                    SA = new SlideAdapter(mMessageItems);

                    mListView.setAdapter(SA);
                    mListView.setOnItemClickListener(AllPrivate.this);
                    ViewGroup.LayoutParams lp = mListView.getLayoutParams();
                    lp.height = 120*mMessageItems.size();
                    mListView.setLayoutParams(lp);

                    SB = new SlideAdapter(mMessageItems2);
                    mListViewfinish.setAdapter(SB);
                    mListViewfinish.setOnItemClickListener(AllPrivate.this);
                    lp = mListViewfinish.getLayoutParams();
                    lp.height = 120*mMessageItems2.size();
                    mListViewfinish.setLayoutParams(lp);

                    SC = new SlideAdapter(mMessageItems3);
                    mListViewoverdue.setAdapter(SC);
                    mListViewoverdue.setOnItemClickListener(AllPrivate.this);
                    lp = mListViewoverdue.getLayoutParams();
                    lp.height = 120*mMessageItems3.size();
                    mListViewoverdue.setLayoutParams(lp);

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

                slideView = new SlideView(AllPrivate.this);
                slideView.setContentView(itemView);

                holder = new ViewHolder(slideView);
                slideView.setOnSlideListener(AllPrivate.this);
                slideView.setTag(holder);
            } else {
                holder = (ViewHolder) slideView.getTag();
            }
            MessageItem item = this.MessageItems.get(position);
            item.slideView = slideView;
            item.slideView.shrink();

            holder.msg.setText(item.msg);
            holder.time.setText(item.time);
            holder.deleteHolder.setOnClickListener(AllPrivate.this);
            holder.finishHolder.setOnClickListener(AllPrivate.this);

            return slideView;
        }

    }


    private static class ViewHolder {
        public ImageView icon;
        public TextView title;
        public TextView msg;
        public TextView time;
        public ViewGroup deleteHolder;
        public ViewGroup finishHolder;

        ViewHolder(View view) {
            msg = (TextView) view.findViewById(R.id.msg);
            time = (TextView) view.findViewById(R.id.time);
            deleteHolder = (ViewGroup)view.findViewById(R.id.holder2);
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

            int viewnumber;

        position = mListView.getPositionForView(view);
        viewnumber=0;
        if(position==-1){
            position =  mListViewfinish.getPositionForView(view);
            viewnumber=1;
        }
        if(position==-1){
            position =  mListViewoverdue.getPositionForView(view);
            viewnumber=2;
        }

        if(viewnumber==0) {
            if(!mListView.isScroll){
                Log.e(TAG, "onItemClick position=" + position);
                Toast.makeText(context, "click", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, com.example.administrator.task.SinglePrivateTask.class);
                Bundle bundle=new Bundle();
                int P = position;
                bundle.putInt("PTaskID", PTaskID1.get(P));
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.push_right_in,
                        R.anim.push_left_out);
            }
        }else if(viewnumber==1){
            if(!mListViewfinish.isScroll){
                Log.e(TAG, "onItemClick position=" + position);
                Toast.makeText(context, "click", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, com.example.administrator.task.SinglePrivateTask.class);
                Bundle bundle=new Bundle();
                int P = position;
                bundle.putInt("PTaskID", PTaskID2.get(P));
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.push_right_in,
                        R.anim.push_left_out);
            }

        }else if(viewnumber==2){
            if(!mListViewoverdue.isScroll){
                Log.e(TAG, "onItemClick position=" + position);
                Toast.makeText(context, "click", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, com.example.administrator.task.SinglePrivateTask.class);
                Bundle bundle=new Bundle();
                int P = position;
                bundle.putInt("PTaskID", PTaskID3.get(P));
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.push_right_in,
                        R.anim.push_left_out);
            }
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

            int viewnumber;

            int position = mListView.getPositionForView(v);
            viewnumber=0;
            if(position==-1){
                position =  mListViewfinish.getPositionForView(v);
                viewnumber=1;
            }
            if(position==-1){
                position =  mListViewoverdue.getPositionForView(v);
                viewnumber=2;
            }

            int deletionid=-1;
            if(viewnumber==0) {
                deletionid = PTaskID1.get(position);
                ViewGroup parent= (ViewGroup) v.getParent();
                ViewGroup parent2= (ViewGroup) parent.getParent();
                mListView.invalidateViews();
                Log.e(TAG, "onClick v=" + parent2);
//                parent2.setVisibility(View.INVISIBLE);
                parent2.setVisibility(View.GONE);
//                mListView.removeViewInLayout(v);
                SlideAdapter myAdapter = (SlideAdapter) mListView.getAdapter();
                myAdapter.notifyDataSetChanged();

                runOnUiThread(new Runnable() {
                    public void run() {
                        SA.notifyDataSetChanged();
                    }
                });
//                mListView.removeViewAt(position);
            }else if(viewnumber==1){
                deletionid = PTaskID2.get(position);
//                mListViewfinish.removeViewAt(position);
            }else if(viewnumber==2){
                deletionid = PTaskID3.get(position);
//                mListViewoverdue.removeViewAt(position);
            }

            RequestParams params = new RequestParams();
            params.put("taskid", deletionid);
            AsyncHttpClient client = new AsyncHttpClient();
            client.post("http://task-1123.appspot.com/deleteprivatetask", params, new AsyncHttpResponseHandler() {
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

            try {
                Thread.sleep(100);                 //1000 milliseconds is one second.
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            Intent intent= new Intent(this, AllPrivate.class);
            Bundle bundle=new Bundle();
            bundle.putString("account", accountName);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            overridePendingTransition(R.anim.push_right_in,
                    R.anim.push_left_out);
            this.finish();


        }
        else if (v.getId() == R.id.holder1){

            int viewnumber;

            int position = mListView.getPositionForView(v);
            viewnumber=0;
            if(position==-1){
                position =  mListViewfinish.getPositionForView(v);
                viewnumber=1;

            }
            if(position==-1){
                position =  mListViewoverdue.getPositionForView(v);
                viewnumber=2;
            }

            int finishid=-1;
            if(viewnumber==0) {
                finishid = PTaskID1.get(position);
            }else if(viewnumber==1){
                finishid = PTaskID2.get(position);
                v.setClickable(false);
                v.setBackgroundColor(getResources().getColor(R.color.themecolor));
                return;
            }else if(viewnumber==2){
                finishid = PTaskID3.get(position);

            }


            RequestParams params = new RequestParams();
            params.put("taskid", finishid);
            AsyncHttpClient client = new AsyncHttpClient();
            client.post("http://task-1123.appspot.com/finishprivatetask", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] response) {
                    Log.w("async", "success!!!!");
                    Toast.makeText(context, "finish", Toast.LENGTH_SHORT).show();
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

            Intent intent= new Intent(this, AllPrivate.class);
            Bundle bundle=new Bundle();
            bundle.putString("account", accountName);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            overridePendingTransition(0, 0);
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
