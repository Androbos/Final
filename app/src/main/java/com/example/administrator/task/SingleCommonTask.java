package com.example.administrator.task;

import com.example.administrator.task.myComment;
//import com.example.administrator.task.CommentAdapter;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nhaarman.supertooltips.ToolTip;
import com.nhaarman.supertooltips.ToolTipRelativeLayout;
import com.nhaarman.supertooltips.ToolTipView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class SingleCommonTask extends FragmentActivity implements View.OnClickListener, ToolTipView.OnToolTipViewClickedListener, AlertTimeSelect.NoticeDialogListener,AlertEmail.NoticeDialogListener{
    boolean hasEdit =false;
    boolean hasJoined = false;
    boolean thisbubblesend = true;
    boolean toReplys = false;
    int ComSubmitId = 0;
    int ComTextId = 0;
    int ReplySubmitId = 0;
    int ReplyTextId = 0;
    int TaskId = 0;
    int groupposition;
    String Days;
    String Hours;
    String ReplyTo;
    String Minutes;
    String accountName;
    String email;
    String thisbubblereceiver;
    ToolTipView myToolTipView;
    private PullToRefreshLayout mPullToRefreshLayout;

    ArrayList<String> Comments;
    ArrayList<String> MemberName = new ArrayList<String>();
    ArrayList<String> MemberPic = new ArrayList<String>();
    ArrayList<myComment> mComments = new ArrayList<myComment>();
    ArrayList<String> MemberGender = new ArrayList<String>();
    ArrayList<String> MemberDob = new ArrayList<String>();
    ArrayList<String> MemberEmail = new ArrayList<String>();
    ArrayList<Integer> CommentIds;
    Context context = this;
    Activity activity = this;

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_common_task);

        Intent intent = getIntent();
        TaskId = intent.getIntExtra("CTaskID", 0);
        accountName = intent.getStringExtra("account");
        hasJoined = intent.getBooleanExtra("hasJoined", false);
        email = intent.getStringExtra("email");
        groupposition = intent.getIntExtra("groupposition", -1);

        //==========hide
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bar)));
        int abTitleId = getResources().getIdentifier("action_bar_title", "id", "android");
        findViewById(abTitleId).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SingleCommonTask.this, ManageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("account", accountName);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.push_right_in,
                        R.anim.push_left_out);
                finish();
            }
        });

        final LinearLayout l1 = (LinearLayout) findViewById(R.id.linear1);
        final LinearLayout l2 = (LinearLayout) findViewById(R.id.linear2);
        final LinearLayout l3 = (LinearLayout) findViewById(R.id.linear3);
        final LinearLayout l4 = (LinearLayout) findViewById(R.id.linear4);
        final LinearLayout l5 = (LinearLayout) findViewById(R.id.linear5);
        final LinearLayout l6 = (LinearLayout) findViewById(R.id.linear6);
        final LinearLayout CD = (LinearLayout) findViewById(R.id.comment_dialog);
        final GridView gv = (GridView) findViewById(R.id.gridview);

        l1.setVisibility(View.GONE);
        l2.setVisibility(View.GONE);
        l3.setVisibility(View.GONE);
        l4.setVisibility(View.GONE);
        l5.setVisibility(View.GONE);
        l6.setVisibility(View.GONE);
        CD.setVisibility(View.GONE);
        gv.setVisibility(View.GONE);


        //--tooltip


        ImageView Description = (ImageView) findViewById(R.id.collapseDes);
        ImageView CollapseMember = (ImageView) findViewById(R.id.collapseMember);
        RelativeLayout Wholescreen =(RelativeLayout) findViewById(R.id.wholescreen);

        ImageView AddComment = (ImageView) findViewById(R.id.add_comment);
        ImageView ShowComment = (ImageView) findViewById(R.id.show_comments);
        CollapseMember.setOnClickListener(this);
        Description.setOnClickListener(this);
        Wholescreen.setOnClickListener(this);
        AddComment.setOnClickListener(this);
        ShowComment.setOnClickListener(this);
        ImageView ManageCommonTask= (ImageView)findViewById(R.id.manage_common);
        ManageCommonTask.setOnClickListener(this);

        final String request_url = "http://task-1123.appspot.com/viewsinglecommontask?taskid="+TaskId;
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] response) {


                try {

                    JSONObject jObject = new JSONObject(new String(response));
                    final String CTaskname;
                    String CTaskdue;
                    String CTaskdescription;
                    String CTaskcreatetime;
                    String Ccreator;

                    ArrayList<String> CCCC = new ArrayList<String>();
                    MemberGender = new ArrayList<String>();
                    MemberDob = new ArrayList<String>();
                    MemberEmail = new ArrayList<String>();
                    MemberName = new ArrayList<String>();
                    MemberPic = new ArrayList<String>();

                    JSONArray JComments = jObject.getJSONArray("comment_content");
                    JSONArray JCommentids = jObject.getJSONArray("comment_id");
                    JSONArray JCommenttimes = jObject.getJSONArray("commentcreate_time");
                    JSONArray JCommentcreators = jObject.getJSONArray("commentcreator");
                    JSONArray JMemberNames = jObject.getJSONArray("member");
                    JSONArray JMemberPics = jObject.getJSONArray("membericon");
                    JSONArray JMemberDob = jObject.getJSONArray("dob_");
                    JSONArray JMemberEmail = jObject.getJSONArray("email_");
                    JSONArray JMemberGender = jObject.getJSONArray("gender_");

                    for (int i = 0; i < JMemberNames.length(); i++) {
                        MemberName.add(JMemberNames.getString(i));
                        MemberPic.add(JMemberPics.getString(i));
                        MemberDob.add(JMemberDob.getString(i));
                        MemberEmail.add(JMemberEmail.getString(i));
                        MemberGender.add(JMemberGender.getString(i));
                    }

                    System.out.println("Membersize" + MemberPic.size());


                    for (int i = 0; i < JComments.length(); i++) {
                        String Content = JComments.getString(i);
                        String Creator = JCommentcreators.getString(i);
                        String CommentTime = JCommenttimes.getString(i);
                        int CommentId = JCommentids.getInt(i);
                        final myComment oneComment = new myComment(Content, CommentId, Creator, CommentTime);


                        final String reply_request_url = "http://task-1123.appspot.com/viewreply?commentid=" + CommentId;
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
                                    JSONArray JReplyTime = jObject_r.getJSONArray("replycreate_time");

                                    for (int i = 0; i < JReplys.length(); i++) {
                                        String RContent = JReplys.getString(i);
                                        int RCommentid = JReplyCommentids.getInt(i);
                                        String Replyto = JReplytos.getString(i);
                                        String RCreator = JReplyCreator.getString(i);
                                        String Replytime = JReplyTime.getString(i);
                                        Reply oneReply = new Reply(RCommentid, RContent, RCreator, Replytime, Replyto);
                                        oneComment.Replies.add(oneReply);
                                    }
                                    System.out.println(oneComment.Replies.size());

                                } catch (JSONException j) {
                                    System.out.println("JSON Error");
                                    j.printStackTrace();
                                }

                                //hide show-------------


                            }

                            @Override
                            public void onFailure(int statusCode_r, cz.msebera.android.httpclient.Header[] headers_r, byte[] errorResponse_r, Throwable e_r) {
                                Log.e("ManagePage", "There was a problem in retrieving the url R: " + e_r.toString());
                            }
                        });

                        mComments.add(oneComment);
                        CCCC.add(Content);

                            TextView load = (TextView) findViewById(R.id.loading);
                            load.setVisibility(View.GONE);
                            l1.setVisibility(View.VISIBLE);
                            l2.setVisibility(View.VISIBLE);
                            l3.setVisibility(View.VISIBLE);
                            l4.setVisibility(View.VISIBLE);
                            l5.setVisibility(View.VISIBLE);
                            l6.setVisibility(View.VISIBLE);
                            CD.setVisibility(View.VISIBLE);
                            gv.setVisibility(View.VISIBLE);

                    }

                    if(JComments.length()==0){
                        TextView load = (TextView) findViewById(R.id.loading);
                        load.setVisibility(View.GONE);
                        l1.setVisibility(View.VISIBLE);
                        l2.setVisibility(View.VISIBLE);
                        l3.setVisibility(View.VISIBLE);
                        l4.setVisibility(View.VISIBLE);
                        l5.setVisibility(View.VISIBLE);
                        l6.setVisibility(View.VISIBLE);
                        CD.setVisibility(View.VISIBLE);
                        gv.setVisibility(View.VISIBLE);
                    }


                    System.out.println(mComments.size());
                    TextView commentnumber = (TextView) findViewById(R.id.comment_nums);
                    commentnumber.setText("(" + mComments.size() + ")");

//                    ArrayAdapter<String> adp =new ArrayAdapter<String>(context,R.layout.testview,CCCC);
                    final ExpandableListView CommentList = (ExpandableListView) findViewById(R.id.comments);

                    for (int i = 0; i < mComments.size(); i++) {
                        System.out.println(mComments.get(i).Content + " has " + mComments.get(i).Replies.size());
                    }

                    ExpendableCommentAdapter adapter = new ExpendableCommentAdapter(activity, mComments);
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
                    ImageView showcomment  = (ImageView) findViewById(R.id.show_comments);
                    ImageView ManageCommonTask = (ImageView) findViewById(R.id.manage_common);
                    if(groupposition>=0){
                        showcomment.setImageResource(R.drawable.ic_indeterminate_check_box_white_24dp);
                        showcomment.setTag("minus");
                        CommentList.setVisibility(View.VISIBLE);
                        CommentList.expandGroup(groupposition,true);
                    }

                    if (Ccreator.equals(accountName)) {
                        ManageCommonTask.setImageResource(R.drawable.ic_edit_white_18dp);
                        ManageCommonTask.setTag("edit");
                    } else if (hasJoined) {
                        ManageCommonTask.setImageResource(R.drawable.ic_star_white_18dp);
                        showcomment.setTag("minus");
                        Toast.makeText(context, "Click + to see comments", Toast.LENGTH_SHORT).show();
                        ManageCommonTask.setTag("hasJoined");
                    } else {
                        ManageCommonTask.setImageResource(R.drawable.ic_star_border_white_18dp);
                        ManageCommonTask.setTag("notJoined");
                        //---------*******************---------------
                        CommentList.setVisibility(View.GONE);

                        showcomment.setTag("0");
                        Toast.makeText(context, "Join to see the comments", Toast.LENGTH_SHORT).show();
                    }

                    TextView ctaskname = (TextView) findViewById(R.id.ctaskname);
                    TextView ctaskdue = (TextView) findViewById(R.id.ctaskdue);
                    TextView ctaskdescription = (TextView) findViewById(R.id.ctaskdescript);
                    TextView ctaskcreatetime = (TextView) findViewById(R.id.ctaskcreatetime);
                    ctaskname.setText(CTaskname);
                    ctaskdue.setText(CTaskdue);
                    ctaskdescription.setText(CTaskdescription);
                    ctaskcreatetime.setText("Created by " + Ccreator + " at " + CTaskcreatetime);

                    ctaskname.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String url = String.valueOf(TaskId);
                            int QR_WIDTH = 500;
                            int QR_HEIGHT = 500;
                            try {
                                if (url == null || "".equals(url) || url.length() < 1) {
                                    return;
                                }
                                Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
                                hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
                                BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
                                int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
                                for (int y = 0; y < QR_HEIGHT; y++) {
                                    for (int x = 0; x < QR_WIDTH; x++) {
                                        if (bitMatrix.get(x, y)) {
                                            pixels[y * QR_WIDTH + x] = 0xff000000;
                                        } else {
                                            pixels[y * QR_WIDTH + x] = 0xffffffff;
                                        }
                                    }
                                }

                                Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
                                bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
                                ImageView image = new ImageView(SingleCommonTask.this);
                                image.setImageBitmap(bitmap);
                                AlertDialog.Builder builder =
                                        new AlertDialog.Builder(SingleCommonTask.this).
                                                setTitle("QR Code").
                                                setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                }).
                                                setView(image);
                                builder.create().show();

                            } catch (WriterException e) {
                                e.printStackTrace();
                            }

                        }
                    });

                    CommentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            // When clicked, show a toast with the TextView text
                            Toast.makeText(getApplicationContext(),
                                    ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
                        }
                    });




                    CommentList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                        public boolean onChildClick(ExpandableListView parent, View v, final int groupPosition, int childPosition, long id) {
                            toReplys = true;
                            TextView ReplyCreator = (TextView) v.findViewById(R.id.reply_creator);
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            View child = CommentList.getChildAt(groupPosition);
                            LinearLayout replydialog = (LinearLayout) child.findViewById(R.id.reply_dialog);
                            if (replydialog.getTag().equals("noview")) {
                                EditText ReplyEdit = new EditText(context);
                                ReplyEdit.setPadding(10, 0, 0, 0);
                                ReplyEdit.setId(View.generateViewId());
                                ReplyEdit.setBackground(getResources().getDrawable(R.drawable.round_edit));
                                ReplyTextId = ReplyEdit.getId();
                                LinearLayout split = new LinearLayout(context);
                                Button ReplySubmit = new Button(context);
                                ReplySubmit.setText("Submit");
                                ReplySubmit.setOnClickListener(SingleCommonTask.this);
                                ReplySubmit.setId(View.generateViewId());
                                ReplySubmitId = ReplySubmit.getId();
                                ReplyTo = ReplyCreator.getText().toString();
                                ReplyEdit.setHint("@" + ReplyTo);
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
                                if (ReplyEdit.requestFocus()) {
                                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//                    inputMethodManager.toggleSoftInputFromWindow(replydialog.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                                }
                            } else {
                                replydialog.removeAllViews();
                                replydialog.setTag("noview");
                                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//                inputMethodManager.toggleSoftInputFromWindow(replydialog.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                            }
                            Toast.makeText(context, "Reply", Toast.LENGTH_SHORT).show();

                            return false;
                        }
                    });

                    GridView gridview = (GridView) findViewById(R.id.gridview);
                    gridview.setAdapter(new ImageAdapter(context, MemberPic));


                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {
                            ToolTipRelativeLayout toolTipRelativeLayout = (ToolTipRelativeLayout) findViewById(R.id.tooltip);
                            String s = "\n【Click to send an email】";
                            if (MemberEmail.get(position).equals("Email Invisible")) {
                                s = "";
                                thisbubblesend = false;
                            } else {
                                thisbubblesend = true;
                                thisbubblereceiver = MemberEmail.get(position);
                            }
                            ToolTip toolTip = new ToolTip()
                                    .withText(MemberName.get(position) + "\nEmail: " + MemberEmail.get(position) + "\nGender: " + MemberGender.get(position) + "\nDOB: " + MemberDob.get(position) + s)
                                    .withTextColor(Color.WHITE)

                                    .withColor(getResources().getColor(R.color.blue))
                                    .withShadow();

                            if (myToolTipView == null) {
                                myToolTipView = toolTipRelativeLayout.showToolTipForView(toolTip, v);
                                myToolTipView.setOnToolTipViewClickedListener(SingleCommonTask.this);
                                final int[] screenPos = new int[2]; // origin is device display
                                final Rect displayFrame = new Rect(); // includes decorations (e.g. status bar)
                                v.getLocationOnScreen(screenPos);
                                v.getWindowVisibleDisplayFrame(displayFrame);
                                final int viewHeight = v.getHeight();
                                final int estimatedToastHeight = (int) (170
                                        * v.getContext().getResources().getDisplayMetrics().density);
                                myToolTipView.setY(screenPos[1] - displayFrame.top - estimatedToastHeight);

                            } else {
                                myToolTipView.remove();
                                myToolTipView = null;
                            }


                        }
                    });


                } catch (JSONException j) {
                    System.out.println("JSON Error");
                    j.printStackTrace();
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
            InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            if (hasEdit) {
                CommentDialog.removeAllViews();
                hasEdit = false;
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//                inputMethodManager.toggleSoftInputFromWindow(CommentDialog.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
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
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//                    inputMethodManager.toggleSoftInputFromWindow(CommentDialog.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                }
            }

        }else if(v.getId()==ComSubmitId){
            EditText CommentText = (EditText)findViewById(ComTextId);
            String Comment = CommentText.getText().toString();

            RequestParams params = new RequestParams();
            params.put("taskid", TaskId);
            System.out.println("name" + accountName);
            params.put("creator", accountName);
            params.put("content", Comment);

            AsyncHttpClient client = new AsyncHttpClient();
            client.post("http://task-1123.appspot.com/createcomment", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] response) {
                    Log.w("async", "success!!!!");
                    Intent intent =new Intent(SingleCommonTask.this, SingleCommonTask.class);
                    Bundle bundle =new Bundle();
                    bundle.putString("account", accountName);
                    bundle.putInt("CTaskID", TaskId);
                    bundle.putString("email", email);
                    bundle.putBoolean("hasJoined", hasJoined);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    SingleCommonTask.this.finish();
                    Toast.makeText(context, "Submit Successful", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] errorResponse, Throwable e) {
                    Log.e("Posting_to_blob", "There was a problem in retrieving the url : " + e.toString());
                }
            });
        }else if(v.getId() == R.id.reply){
            toReplys = false;
            InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
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
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//                    inputMethodManager.toggleSoftInputFromWindow(replydialog.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                }
            }else{
                replydialog.removeAllViews();
                replydialog.setTag("noview");
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//                inputMethodManager.toggleSoftInputFromWindow(replydialog.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
            }
            Toast.makeText(context, "Reply", Toast.LENGTH_SHORT).show();
        }else if(v.getId() == ReplySubmitId){
            EditText ReplyText = (EditText)findViewById(ReplyTextId);
            String ReplyContent = ReplyText.getText().toString();
            String replyto;
            ListView CommentList = (ListView) findViewById(R.id.comments);
            final int position = CommentList.getPositionForView(v);
            myComment iComment = mComments.get(position);
            if(!toReplys){
                replyto = iComment.Creator;
            }else{
                replyto = ReplyTo;
            }


            RequestParams params = new RequestParams();
            params.put("taskid", TaskId);
            params.put("commentid",iComment.Id);
            params.put("creator", accountName);
            params.put("content", ReplyContent);
            params.put("replyto",replyto);
            params.put("groupposition",position);

            AsyncHttpClient client = new AsyncHttpClient();
            client.post("http://task-1123.appspot.com/createreply", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] response) {
                    Log.w("async", "success!!!!");
                    Intent intent =new Intent(SingleCommonTask.this, SingleCommonTask.class);
                    Bundle bundle =new Bundle();
                    bundle.putString("account", accountName);
                    bundle.putInt("CTaskID", TaskId);
                    bundle.putString("email", email);
                    bundle.putBoolean("hasJoined", hasJoined);
                    bundle.putInt("groupposition", position);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    SingleCommonTask.this.finish();
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
            }else if(ShowComments.getTag().equals("plus")){
                ShowComments.setImageResource(R.drawable.ic_indeterminate_check_box_white_24dp);
                ShowComments.setTag("minus");
                CommentList.setVisibility(View.VISIBLE);
            }

        }else if(v.getId() == R.id.collapseDes){
            TextView descontent = (TextView)findViewById(R.id.ctaskdescript);
            if(v.getTag().equals("show")){
                ((ImageView)v).setImageResource(R.drawable.ic_add_white_18dp);
                v.setTag("notshow");
                descontent.setVisibility(View.GONE);
            }else{
                ((ImageView)v).setImageResource(R.drawable.ic_remove_white_18dp);
                v.setTag("show");
                descontent.setVisibility(View.VISIBLE);
            }
        }else if(v.getId() == R.id.manage_common){

            if(v.getTag().equals("edit")){

                Intent intent= new Intent(SingleCommonTask.this, EditCommonTask.class);
                Bundle bundle=new Bundle();
                bundle.putString("account", accountName);
                bundle.putInt("taskid", TaskId);
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(R.anim.push_right_in,
                        R.anim.push_left_out);
                SingleCommonTask.this.finish();

                Toast.makeText(context, "Edit", Toast.LENGTH_SHORT).show();
            }else if(v.getTag().equals("hasJoined")){
                RequestParams params = new RequestParams();
                params.put("taskid", TaskId);
                params.put("operation", "cancel");
                params.put("userid", accountName);

                AsyncHttpClient client = new AsyncHttpClient();
                client.post("http://task-1123.appspot.com/join", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] response) {
                        Log.w("async", "success!!!!");
                        ImageView ManageCommonTask= (ImageView)findViewById(R.id.manage_common);
                        ManageCommonTask.setImageResource(R.drawable.ic_star_border_white_18dp);
                        ManageCommonTask.setTag("notJoined");
                        Toast.makeText(context, "cancel join", Toast.LENGTH_SHORT).show();
                        ImageView showcomment  = (ImageView) findViewById(R.id.show_comments);
                        showcomment.setTag("0");
                        hasJoined =false;
                        Intent intent =new Intent(SingleCommonTask.this, SingleCommonTask.class);
                        Bundle bundle =new Bundle();
                        bundle.putString("account", accountName);
                        bundle.putInt("CTaskID", TaskId);
                        bundle.putString("email", email);
                        bundle.putBoolean("hasJoined", hasJoined);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        SingleCommonTask.this.finish();
                    }

                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] errorResponse, Throwable e) {
                        Log.e("Posting_to_blob", "There was a problem in retrieving the url : " + e.toString());
                    }
                });


            }else{
                showNoticeDialog();

            }
        }else if(v.getId() == R.id.collapseMember){
            GridView gv = (GridView) findViewById(R.id.gridview);
            if(v.getTag()=="show"){
                gv.setVisibility(View.GONE);
                v.setTag("notshow");
            }else{
                gv.setVisibility(View.VISIBLE);
                v.setTag("show");
            }
        } else{
            if(myToolTipView != null){
                myToolTipView.remove();
                myToolTipView = null;
            }
        }
    }

    public void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new AlertTimeSelect();
        dialog.show(getSupportFragmentManager(), "AlertTimeSelect");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        EditText days = (EditText)(dialog.getDialog().findViewById(R.id.day));
        EditText hours = (EditText)dialog.getDialog().findViewById(R.id.hour);
        EditText minutes = (EditText)dialog.getDialog().findViewById(R.id.minute);
        Days = days.getText().toString();
        Hours = hours.getText().toString();
        Minutes = minutes.getText().toString();
        RequestParams params = new RequestParams();
        params.put("taskid", TaskId);
        params.put("operation","join");
        params.put("userid", accountName);
        params.put("d", Days);
        params.put("h", Hours);
        params.put("m", Minutes);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://task-1123.appspot.com/join", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] response) {
                Log.w("async", "success!!!!");
                ImageView ManageCommonTask = (ImageView) findViewById(R.id.manage_common);
                ManageCommonTask.setImageResource(R.drawable.ic_star_white_18dp);
                ManageCommonTask.setTag("hasJoined");
                ImageView showcomment  = (ImageView) findViewById(R.id.show_comments);
                showcomment.setTag("minus");
                Intent intent =new Intent(SingleCommonTask.this, SingleCommonTask.class);
                Bundle bundle =new Bundle();
                hasJoined = true;
                bundle.putString("account", accountName);
                bundle.putInt("CTaskID", TaskId);
                bundle.putString("email", email);
                bundle.putBoolean("hasJoined", true);

                intent.putExtras(bundle);
                startActivity(intent);
                SingleCommonTask.this.finish();
                Toast.makeText(context, "join", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e("Posting_to_blob", "There was a problem in retrieving the url : " + e.toString());
            }
        });

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button

    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<String> imageURLs;


        public ImageAdapter(Context c, ArrayList<String> imageURLs) {
            mContext = c;
            this.imageURLs = imageURLs;

        }

        public int getCount() {
            return imageURLs.size();

        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;

            if (convertView == null) {  // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);

                imageView.setLayoutParams(new GridView.LayoutParams(130, 130));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                imageView = (ImageView) convertView;

            }

            Picasso.with(mContext).load(imageURLs.get(position)).into(imageView);

            return imageView;
        }

    }

    @Override
    public void onToolTipViewClicked(final ToolTipView toolTipView) {
        if(thisbubblesend){
            DialogFragment dialog = new AlertEmail();
            dialog.show(getSupportFragmentManager(), "AlertEmail");
//            RequestParams params = new RequestParams();
        }


    }

    @Override
    public void onDialogPositiveClickEmail(DialogFragment dialog) {
        // User touched the dialog's positive button
        EditText subject = (EditText)(dialog.getDialog().findViewById(R.id.Subject));
        EditText content = (EditText)dialog.getDialog().findViewById(R.id.Content);



        RequestParams params = new RequestParams();
        params.put("receiver", thisbubblereceiver);
        params.put("sender",email);
        params.put("subject", subject.getText().toString());
        params.put("body", content.getText().toString());
        params.put("sendername", accountName);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://task-1123.appspot.com/send_email", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] response) {
                Log.w("async", "success!!!!");
                Toast.makeText(context, "Successfully Sent", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e("Posting_to_blob", "There was a problem in retrieving the url : " + e.toString());
            }
        });

    }

    @Override
    public void onDialogNegativeClickEmail(DialogFragment dialog) {
        // User touched the dialog's negative button

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


