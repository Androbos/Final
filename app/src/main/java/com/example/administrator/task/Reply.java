package com.example.administrator.task;

/**
 * Created by JocelynYu on 12/8/15.
 */
public class Reply {
    int CommentId;
    String Content;
    String Creator;
    String ReplyTime;
    String ReplyTo;

    public Reply(int CommentId_, String Content_, String Creator_, String ReplyTime_, String ReplyTo_){
        this.CommentId = CommentId_;
        this.Content = Content_;
        this.Creator = Creator_;
        this.ReplyTime = ReplyTime_;
        this.ReplyTo = ReplyTo_;
    }
}
