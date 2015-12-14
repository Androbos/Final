package com.example.administrator.task;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
//import com.example.administrator.task.myComment;

import org.w3c.dom.Comment;

import java.util.ArrayList;

/**
 * Created by JocelynYu on 12/8/15.
 */
public class SMSAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> keys;
    private final ArrayList<String> body;

    public SMSAdapter(Context context, ArrayList<String> keys,ArrayList<String> body) {
        super(context, R.layout.smsitem, keys);
        this.context = context;
        this.keys = keys;
        this.body=body;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.smsitem, parent, false);
            holder =new ViewHolder();
            holder.tv1 = (TextView) convertView.findViewById(R.id.tv1);
            holder.tv2 = (TextView) convertView.findViewById(R.id.tv2);
            convertView.setTag(holder);

        }
//        TView.setOnClickListener((View.OnClickListener)context);
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv1.setText(keys.get(position));
        holder.tv2.setText(body.get(position));
        if(position%2 == 0){
            convertView.setBackgroundColor(convertView.getResources().getColor(R.color.discript));
        }

        return convertView;
    }

    static class ViewHolder {
        TextView tv1;
        TextView tv2;
    }
}
