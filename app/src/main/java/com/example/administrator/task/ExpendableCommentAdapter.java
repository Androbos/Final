package com.example.administrator.task;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by JocelynYu on 12/10/15.
 */
public class ExpendableCommentAdapter extends BaseExpandableListAdapter {

    private Activity context;
//    private Map<myComment, ArrayList<Reply>> Comment;
    private ArrayList<myComment> Comments;

    public ExpendableCommentAdapter(Activity context_, ArrayList<myComment> Comments_){
        this.context = context_;
        this.Comments = Comments_;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return Comments.get(groupPosition).Replies.get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final Reply oneReply = (Reply) getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.reply_item, null);
        }

        TextView ReplyContent = (TextView) convertView.findViewById(R.id.reply_content);
        TextView ReplyTime = (TextView) convertView.findViewById(R.id.reply_time);
        ReplyContent.setText(oneReply.Creator+" @"+oneReply.ReplyTo+" : "+oneReply.Content);
        ReplyTime.setText(oneReply.ReplyTime);
        return convertView;
    }

    public int getChildrenCount(int groupPosition) {
        return Comments.get(groupPosition).Replies.size();
    }

    public Object getGroup(int groupPosition) {
        return Comments.get(groupPosition);
    }

    public int getGroupCount() {
        return Comments.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(final int groupPosition, final boolean isExpanded,
                             View convertView, final ViewGroup parent) {
        myComment oneComment = Comments.get(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.comment_item,
                    null);
        }
        TextView CommentView = (TextView) convertView.findViewById(R.id.comment);
        TextView CreatorView = (TextView) convertView.findViewById(R.id.comment_creator);
        TextView TimeView = (TextView) convertView.findViewById(R.id.comment_time);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.reply);
        ImageView collapse =(ImageView) convertView.findViewById(R.id.collapse);
        CommentView.setText(oneComment.Content);
        CreatorView.setText(oneComment.Creator+":");
        TimeView.setText(oneComment.CommentTime);
        imageView.setOnClickListener((View.OnClickListener)context);
        int imageResourceId = isExpanded ? R.drawable.ic_keyboard_arrow_up_white_18dp : R.drawable.ic_keyboard_arrow_down_white_18dp;
        collapse.setImageResource(imageResourceId);

        collapse.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (isExpanded) ((ExpandableListView) parent).collapseGroup(groupPosition);
                else ((ExpandableListView) parent).expandGroup(groupPosition, true);

            }
        });

        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }



    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }



}
