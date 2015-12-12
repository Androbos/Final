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
public class SAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> comments;

    public SAdapter(Context context, ArrayList<String> comments_) {
        super(context, R.layout.slidingitem, comments_);
        this.context = context;
        this.comments = comments_;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.slidingitem, parent, false);
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
        if(comments.get(0).equals("Search")) {
            if (position == 0) {
                holder.IView.setImageResource(R.drawable.ic_search_white_24dp);
            } else if (position == 1) {
                holder.IView.setImageResource(R.drawable.ic_lightbulb_outline_white_24dp);
            } else if (position == 3) {
                holder.IView.setImageResource(R.drawable.ic_crop_free_white_24dp);
            }else if (position == 4) {
                holder.IView.setImageResource(R.drawable.ic_fiber_new_white_18dp);
            }else if (position == 7) {
                holder.IView.setImageResource(R.drawable.ic_person_outline_white_18dp);
            }else if (position == 2) {
                holder.IView.setImageResource(R.drawable.ic_compare_arrows_white_18dp);
            }else if (position == 6) {
                holder.IView.setImageResource(R.drawable.ic_settings_white_18dp);
            }else if (position == 5) {
                holder.IView.setImageResource(R.drawable.ic_list_white_18dp);
            }
        }

        return convertView;
    }

    static class ViewHolder {
        TextView TView;
        ImageView IView;
    }
}
