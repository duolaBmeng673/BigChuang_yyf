package com.example.sign_in_test.Data.model;

public class Msg {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SEND = 1;

    private int id;
    private int user_id;
    private int conversation_id;
    private String content;
    private int type;

    public Msg(String content,int type){
        this.content = content;
        this.type = type;
    }

    public int getId() { return id; }

    public int getUser_id() { return user_id; }

    public int getConversation_id() { return conversation_id; }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }


}