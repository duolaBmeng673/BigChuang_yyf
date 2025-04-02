package com.example.sign_in_test.Data.model;

import java.io.File;

public class Msg {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SEND = 1;

    public static final int TYPE_TEXT = 1;   // 文本消息
    public static final int TYPE_IMAGE = 2;  // 图片消息

    /*private int id;*/
    private int user_id;
    private int conversation_id;
    private String content;
    private int type;
    private String imageurl;
    private String imagebase64;
    private int Data_type;


    public Msg(String content,int type,int user_id,int conversation_id,int Data_type){
        this.content = content;
        this.type = type;
        this.user_id = user_id;
        this.conversation_id = conversation_id;
        this.Data_type = Data_type;
    }

    public Msg(String imagebase64,int type,int user_id,int conversation_id){
        this.imagebase64 = imagebase64;
        this.type = type;
        this.user_id = user_id;
        this.conversation_id = conversation_id;
    }

    //public int getId() { return id; }

    public int getUser_id() { return user_id; }

    public int getConversation_id() { return conversation_id; }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }

    public String getImageurl() { return imageurl; }

    public int getData_type(){return Data_type;}

    public String getImagebase64(){return imagebase64;}


    /*public void setId(int id) {
        this.id = id;
    }
*/
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setConversation_id(int conversation_id) {
        this.conversation_id = conversation_id;
    }
}