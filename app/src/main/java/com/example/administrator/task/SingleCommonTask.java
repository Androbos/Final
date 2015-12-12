package com.example.administrator.task;

import com.example.administrator.task.myComment;
//import com.example.administrator.task.CommentAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SingleCommonTask extends ActionBarActivity implements View.OnClickListener{
    boolean hasEdit =false;
    int ComSubmitId = 0;
    int ComTextId = 0;
    int ReplySubmitId = 0;
    int ReplyTextId = 0;
    int TaskId = 0;
    String accountName;
    ArrayList<String> Comments;
    ArrayList<myComment> mComments = new ArrayList<myComment>();
    ArrayList<Integer> CommentIds;
    Context context = this;
    Activity activity = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_common_task);

        Intent intent = getIntent();
        TaskId = intent.getIntExtra("CTaskID", 0);
        accountName = intent.getStringExtra("account");
        System.out.println(TaskId);

        TextView Description = (TextView) findViewById(R.id.collapseDes);

        ImageView AddComment = (ImageView) findViewById(R.id.add_comment);
        ImageView ShowComment = (ImageView) findViewById(R.id.show_comments);
        Description.setOnClickListener(this);
        AddComment.setOnClickListener(this);
        ShowComment.setOnClickListener(this);

        final String request_url = "http://task-1123.appspot.com/viewsinglecommontask?taskid="+TaskId;
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] response) {


                try {

                    JSONObject jObject = new JSONObject(new String(response));
                    String CTaskname;
                    String CTaskdue;
                    String CTaskdescription;
                    String CTaskcreatetime;
                    String Ccreator;
                    ArrayList<String> CCCC= new ArrayList<String>();

                    JSONArray JComments =jObject.getJSONArray("comment_content");
                    JSONArray JCommentids=jObject.getJSONArray("comment_id");
                    JSONArray JCommenttimes = jObject.getJSONArray("commentcreate_time");
                    JSONArray JCommentcreators = jObject.getJSONArray("commentcreator");


                    for(int i = 0; i< JComments.length();i++){
                        String Content = JComments.getString(i);
                        String Creator = JCommentcreators.getString(i);
                        String CommentTime = JCommenttimes.getString(i);
                        int CommentId =JCommentids.getInt(i);
                        final myComment oneComment = new myComment(Content,CommentId,Creator,CommentTime);


                        final String reply_request_url = "http://task-1123.appspot.com/viewreply?commentid="+CommentId;
                        AsyncHttpClient httpClient_r = new AsyncHttpClient();
                        httpClient_r.get(reply_request_url, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode_r, cz.msebera.android.httpclient.Header[] headers_r, byte[] response_r) {


                                try {
                                    JSONObject jObject_r = new JSONObject(new String(response_r));
                                    JSONArray JReplys = jObject_r.getJSONArray("reply_content");
                                    JSONArray JReplyCommentids = jObject_r.getJSONArray("replycomment_id");
                                    JSONArray JReplytos = jObject_r.getJSONArray("replyto");
                                    JSONArray JReplyCreator = jObject_r.getJSONArray("replycreator");
                                    JSONArray JReplyTime =jObject_r.getJSONArray("replycreate_time");

                                    for(int i = 0; i<JReplys.length();i++){
                                        String RContent = JReplys.getString(i);
                                        int RCommentid = JReplyCommentids.getInt(i);
                                        String Replyto = JReplytos.getString(i);
                                        String RCreator = JReplyCreator.getString(i);
                                        String Replytime = JReplyTime.getString(i);
                                        Reply oneReply = new Reply(RCommentid,RContent,RCreator,Replytime,Replyto);
                                        oneComment.Replies.add(oneReply);
                                    }
                                    System.out.println(oneComment.Replies.size());

                                } catch (JSONException j) {
                                    System.out.println("JSON Error");
                                }

                            }

                            @Override
                            public void onFailure(int statusCode_r, cz.msebera.android.httpclient.Header[] headers_r, byte[] errorResponse_r, Throwable e_r) {
                                Log.e("ManagePage", "There was a problem in retrieving the url R: " + e_r.toString());
                            }
                        });

                        mComments.add(oneComment);
                        CCCC.add(Content);
                    }
                    System.out.println(mComments.size());
                    TextView commentnumber = (TextView) findViewById(R.id.comment_nums);
                    commentnumber.setText("(" + mComments.size() + ")");

//                    ArrayAdapter<String> adp =new ArrayAdapter<String>(context,R.layout.testview,CCCC);
                    ExpandableListView CommentList = (ExpandableListView)findViewById(R.id.comments);

                    for(int i = 0; i < mComments.size();i++){
                        System.out.println(mComments.get(i).Content + " has " + mComments.get(i).Replies.size());
                    }

                    ExpendableCommentAdapter adapter = new ExpendableCommentAdapter(activity,mComments);
                    CommentList.setAdapter(adapter);
                    CommentList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                        @Override
                        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                            // Doing nothing
                            return true;
                        }
                    });
                    CTaskname = jObject.getJSONArray("taskname").getString(0);
                    CTaskdue = jObject.getJSONArray("due").getString(0);
                    CTaskdescription = jObject.getJSONArray("description").getString(0);
                    CTaskcreatetime = jObject.getJSONArray("create_time").getString(0);
                    Ccreator = jObject.getJSONArray("creator").getString(0);
                    ImageView ManageCommonTask= (ImageView)findViewById(R.id.manage_common);
                    if(Ccreator.equals(accountName)){
                        ManageCommonTask.setImageResource(R.drawable.ic_edit_white_18dp);
                        ManageCommonTask.setTag("edit");
                    }
                    TextView ctaskname = (TextView) findViewById(R.id.ctaskname);
                    TextView ctaskdue = (TextView) findViewById(R.id.ctaskdue);
                    TextView ctaskdescription = (TextView) findViewById(R.id.ctaskdescript);
                    TextView ctaskcreatetime = (TextView) findViewById(R.id.ctaskcreatetime);
                    ctaskname.setText(CTaskname);
                    ctaskdue.setText(CTaskdue);
                    ctaskdescription.setText(CTaskdescription);
                    ctaskcreatetime.setText("Created by "+Ccreator+" at "+CTaskcreatetime);

                    CommentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            // When clicked, show a toast with the TextView text
                            Toast.makeText(getApplicationContext(),
                                    ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
                        }
                    });

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
    public void onClick(View v){
        if(v.getId()==R.id.add_comment) {
            LinearLayout CommentDialog = (LinearLayout) findViewById(R.id.comment_dialog);
            if (hasEdit) {
                CommentDialog.removeAllViews();
                hasEdit = false;
            } else {
                EditText CommentEdit = new EditText(this);
                CommentEdit.setId(View.generateViewId());
                ComTextId = CommentEdit.getId();
                CommentEdit.setBackground(getResources().getDrawable(R.drawable.round_edit));
                CommentEdit.setPadding(10,0,0,0);
                LinearLayout split = new LinearLayout(this);
                Button ComSubmit =new Button(this);
                ComSubmit.setText("Submit");
                ComSubmit.setOnClickListener(this);
                ComSubmit.setId(View.generateViewId());
                ComSubmitId = ComSubmit.getId();
                ComSubmit.setBackgroundResource(R.drawable.mybutton);
                ComSubmit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                ComSubmit.setPadding(0, 0, 0, 0);
                GradientDrawable background = (GradientDrawable) ComSubmit.getBackground();
                background.setColor(Color.WHITE);
                ComSubmit.setTextColor(getResources().getColor(R.color.themecolor));
                CommentDialog.addView(CommentEdit, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100, 7));
                CommentDialog.addView(ComSubmit, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 100, 1));
                CommentDialog.addView(split, new LinearLayout.LayoutParams(8, 100));
                hasEdit = true;
                if(CommentEdit.requestFocus()) {
                    InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInputFromWindow(split.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                }
            }

        }else if(v.getId()==ComSubmitId){
            EditText CommentText = (EditText)findViewById(ComTextId);
            String Comment = CommentText.getText().toString();

            RequestParams params = new RequestParams();
            params.put("taskid", TaskId);
            params.put("creator", accountName);
            params.put("content", Comment);

            AsyncHttpClient client = new AsyncHttpClient();
            client.post("http://task-1123.appspot.com/createcomment", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] response) {
                    Log.w("async", "success!!!!");
//                    Intent intent =
                    Toast.makeText(context, "Submit Successful", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] errorResponse, Throwable e) {
                    Log.e("Posting_to_blob", "There was a problem in retrieving the url : " + e.toString());
                }
            });
        }else if(v.getId() == R.id.reply){
            ListView CommentList = (ListView) findViewById(R.id.comments);
            int position = CommentList.getPositionForView(v);
            View child = CommentList.getChildAt(position);
            LinearLayout replydialog = (LinearLayout) child.findViewById(R.id.reply_dialog);
            if(replydialog.getTag().equals("noview")){
                EditText ReplyEdit = new EditText(context);
                ReplyEdit.setPadding(10,0,0,0);
                ReplyEdit.setId(View.generateViewId());
                ReplyEdit.setBackground(getResources().getDrawable(R.drawable.round_edit));
                ReplyTextId = ReplyEdit.getId();
                LinearLayout split = new LinearLayout(this);
                Button ReplySubmit =new Button(this);
                ReplySubmit.setText("Submit");
                ReplySubmit.setOnClickListener(this);
                ReplySubmit.setId(View.generateViewId());
                ReplySubmitId = ReplySubmit.getId();
                ReplySubmit.setBackgroundResource(R.drawable.mybutton);
                ReplySubmit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                ReplySubmit.setPadding(0, 0, 0, 0);
                GradientDrawable background = (GradientDrawable) ReplySubmit.getBackground();
                background.setColor(Color.WHITE);
                ReplySubmit.setTextColor(getResources().getColor(R.color.themecolor));
                replydialog.addView(ReplyEdit, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100, 7));
                replydialog.addView(ReplySubmit, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 100, 1));
                replydialog.addView(split, new LinearLayout.LayoutParams(8, 100));
                replydialog.setTag("hasview");
                if(ReplyEdit.requestFocus()) {
                    InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInputFromWindow(split.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                }
            }else{
                replydialog.removeAllViews();
                replydialog.setTag("noview");
            }
            Toast.makeText(context, "Reply", Toast.LENGTH_SHORT).show();
        }else if(v.getId() == ReplySubmitId){
            EditText ReplyText = (EditText)findViewById(ReplyTextId);
            String ReplyContent = ReplyText.getText().toString();
            ListView CommentList = (ListView) findViewById(R.id.comments);
            int position = CommentList.getPositionForView(v);
            myComment iComment = mComments.get(position);

            RequestParams params = new RequestParams();
            params.put("taskid", TaskId);
            params.put("commentid",iComment.Id);
            params.put("creator", accountName);
            params.put("content", ReplyContent);
            params.put("replyto",iComment.Creator);

            AsyncHttpClient client = new AsyncHttpClient();
            client.post("http://task-1123.appspot.com/createreply", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] response) {
                    Log.w("async", "success!!!!");
                    Toast.makeText(context, "Submit Successful", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] errorResponse, Throwable e) {
                    Log.e("Posting_to_blob", "There was a problem in retrieving the url : " + e.toString());
                }
            });
        }else if(v.getId() == R.id.show_comments){
            ImageView ShowComments = (ImageView) findViewById(R.id.show_comments);
            ListView CommentList = (ListView)findViewById(R.id.comments);
            if(ShowComments.getTag().equals("minus")){
                ShowComments.setImageResource(R.drawable.ic_add_box_white_18dp);
                ShowComments.setTag("plus");
                CommentList.setVisibility(View.GONE);
            }else{
                ShowComments.setImageResource(R.drawable.ic_indeterminate_check_box_white_24dp);
                ShowComments.setTag("minus");
                CommentList.setVisibility(View.VISIBLE);
            }

        }else if(v.getId() == R.id.collapseDes){
            TextView descontent = (TextView)findViewById(R.id.ctaskdescript);
            if(v.getTag().equals("show")){
                ((TextView)v).setText("+ Description:");
                v.setTag("notshow");
                descontent.setVisibility(View.GONE);
            }else{
                ((TextView)v).setText("— Description:");
                v.setTag("show");
                descontent.setVisibility(View.VISIBLE);
            }
        }
    }
}

