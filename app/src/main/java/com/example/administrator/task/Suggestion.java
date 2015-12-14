package com.example.administrator.task;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Suggestion extends Activity {

    String accountName;
    ArrayList<String> keys = new ArrayList<String>();
    ArrayList<String> body = new ArrayList<String>();
    ArrayList<String> contact = new ArrayList<String>();
    ArrayList<String> time = new ArrayList<String>();
    ArrayList<String> week = new ArrayList<String>();

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);
        Intent intentstream = getIntent();
        accountName =intentstream.getStringExtra("account");
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bar)));
        int abTitleId = getResources().getIdentifier("action_bar_title", "id", "android");
        findViewById(abTitleId).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Suggestion.this, ManageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("account", accountName);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.push_right_in,
                        R.anim.push_left_out);
                finish();
            }
        });



        final ListView lv = (ListView) findViewById(R.id.suggestlist);

        final String SMS_URI_ALL = "content://sms/";
        final String SMS_URI_INBOX = "content://sms/inbox";

        try {
            Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);
            int contactIdIndex = 0;
            int nameIndex = 0;

            if(cursor.getCount() > 0) {
                contactIdIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            }

            while(cursor.moveToNext()) {
                contact.add(cursor.getString(nameIndex));
            }

//                String name = cursor.getString(nameIndex);
//                Log.i(TAG, contactId);
//                Log.i(TAG, name);

            //=================================
            Uri uri = Uri.parse(SMS_URI_INBOX);
            String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
            Cursor cur = getContentResolver().query(uri, projection, null, null, "date desc");        // 获取手机内部短信

            if (cur.moveToFirst()) {
//                int index_Address = cur.getColumnIndex("address");
                int index_Person = cur.getColumnIndex("person");
                int index_Body = cur.getColumnIndex("body");
//                int index_Date = cur.getColumnIndex("date");
//                int index_Type = cur.getColumnIndex("type");

                do {
//                    String strAddress = cur.getString(index_Address);
                    int intPerson = cur.getInt(index_Person);
                    String strbody = cur.getString(index_Body);
//                    long longDate = cur.getLong(index_Date);
//                    int intType = cur.getInt(index_Type);

//                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//                    Date d = new Date(longDate);
//                    String strDate = dateFormat.format(d);


                    ArrayList<String> keyword = new ArrayList<String>();
                    keyword.add("Meeting");
                    keyword.add("meeting");
                    keyword.add("Appointment");
                    keyword.add("appointment");
                    keyword.add("Invitation");
                    keyword.add("invitation");
                    keyword.add("Visit");
                    keyword.add("visit");
                    keyword.add("Class");
                    keyword.add("class");
                    keyword.add("Exam");
                    keyword.add("exam");
                    keyword.add("Test");
                    keyword.add("test");
                    keyword.add("Presentation");
                    keyword.add("presentation");
                    keyword.add("Due");
                    keyword.add("due");
                    keyword.add("Deadline");
                    keyword.add("deadline");
                    keyword.add("Bill");
                    keyword.add("bill");
                    keyword.add("Party");
                    keyword.add("appt");
                    keyword.add("party");
                    keyword.add("Conference");
                    keyword.add("conference");
                    ArrayList<String> weekday =new ArrayList<String>();
                    weekday.add("Monday");
                    weekday.add("Tuesday");
                    weekday.add("Wednesday");
                    weekday.add("Thursday");
                    weekday.add("Friday");
                    weekday.add("Saturday");
                    weekday.add("Sunday");
                    weekday.add("Mon");
                    weekday.add("Tue");
                    weekday.add("Wed");
                    weekday.add("Thu");
                    weekday.add("Fri");
                    weekday.add("Sat");
                    weekday.add("Sun");


                    for (int i = 0; i < keyword.size(); i++) {
                        if (strbody.contains(keyword.get(i))) {
                            body.add(strbody);
                            keys.add(keyword.get(i));

                            for (int j =0;j<weekday.size();j++){
                                if(strbody.contains(weekday.get(j))){
                                    week.add(weekday.get(j));
                                }
                            }

                            if(week.size()<i){
                                week.add("");
                            }



                            String pattern = "\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}?";

                            // Create a Pattern object
                            Pattern r = Pattern.compile(pattern);
                            // Now create matcher object.
                            Matcher m = r.matcher(strbody);
                            if (m.find()) {
                                time.add(m.group(0));
                            } else {
                                time.add("");
                            }

                            break;
                        }

                    }

                } while (cur.moveToNext());

                if (!cur.isClosed()) {
                    cur.close();
                    cur = null;
                }
            }

            //=======================



        } catch (SQLiteException ex) {
            Log.d("SQLiteException in", ex.getMessage());
        }
//=============================

        SMSAdapter adapter = new SMSAdapter(this, keys,body);
        System.out.println(keys.size());
        lv.setAdapter(adapter);

        // ListView Item Click Listener
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent intent = new Intent(Suggestion.this, CreatePrivateTask.class);
                Bundle bundle = new Bundle();
                bundle.putString("account", accountName);
                bundle.putString("name",keys.get(position));
                bundle.putString("des", body.get(position));
                bundle.putString("time",time.get(position));
                bundle.putString("week",week.get(position));
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);

                // ListView Clicked item value
//                String  itemValue    = (String) lv.getItemAtPosition(position);
//
//                // Show Alert
//                Toast.makeText(getApplicationContext(),
//                        "Position :" + position + "  ListItem : " + itemValue, Toast.LENGTH_LONG)
//                        .show();
            }

        });

        final TextView load =(TextView) findViewById(R.id.loading);
        load.setVisibility(View.GONE);


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
