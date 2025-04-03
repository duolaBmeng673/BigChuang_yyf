package com.example.sign_in_test.Data.model;

public class Msg {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SEND = 1;

    public static final int TYPE_TEXT = 1;   // 文本消息
    public static final int TYPE_IMAGE = 2;  // 图片消息

    protected int user_id;
    protected int conversation_id;
    protected int type;

    public Msg(int type, int user_id, int conversation_id) {
        this.type = type;
        this.user_id = user_id;
        this.conversation_id = conversation_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getConversation_id() {
        return conversation_id;
    }

    public int getType() {
        return type;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setConversation_id(int conversation_id) {
        this.conversation_id = conversation_id;
    }
}
