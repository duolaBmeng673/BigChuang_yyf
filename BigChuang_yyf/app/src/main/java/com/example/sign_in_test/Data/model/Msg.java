package com.example.sign_in_test.Data.model;

import com.google.gson.Gson;

public class Msg {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SEND = 1;

    /*private int id;*/
    private int user_id;
    private int conversation_id;
    private String content;
    private int type;

    public Msg(String content,int type,int user_id,int conversation_id){
        this.content = content;
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