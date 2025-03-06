package com.example.sign_in_test.Data.model;

public class Msg {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SEND = 1;

    private int id;
    private String content;
    private int type;

    public Msg(String content,int type){
        this.content = content;
        this.type = type;
    }

    public int getId() { return id; }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }


}