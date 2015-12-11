package com.example.administrator.task;
import com.example.administrator.task.Reply;

import java.util.ArrayList;

/**
 * Created by JocelynYu on 12/8/15.
 */
public class myComment {
    String Content;
    int Id;
    String Creator;
    String CommentTime;
    ArrayList<Reply> Replies;

    public myComment(String Content_, int Id_, String Creator_, String CommentTime_){
        this.Content = Content_;
        this.Id = Id_;
        this.Creator = Creator_;
        this.CommentTime = CommentTime_;
        this.Replies =new ArrayList<Reply>();
    }
}
