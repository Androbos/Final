package com.example.administrator.task;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

//import com.example.administrator.task.myComment;

/**
 * Created by JocelynYu on 12/8/15.
 */
public class SAdapter2 extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> comments;
    private final ArrayList<String> pics;

    public SAdapter2(Context context, ArrayList<String> comments_,ArrayList<String> pic_) {
        super(context, R.layout.slidingitem2, comments_);
        this.context = context;
        this.comments = comments_;
        this.pics=pic_;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.slidingitem2, parent, false);
            holder =new ViewHolder();
            holder.TView = (TextView) convertView.findViewById(R.id.slidetext1);
            holder.IView = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(holder);

        }
//        TView.setOnClickListener((View.OnClickListener)context);
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.TView.setText(comments.get(position));
        Picasso.with(this.context).load(pics.get(position)).into(holder.IView);

        return convertView;
    }

    static class ViewHolder {
        TextView TView;
        ImageView IView;
    }
}
