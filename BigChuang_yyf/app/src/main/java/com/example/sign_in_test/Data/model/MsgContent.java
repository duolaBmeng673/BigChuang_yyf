package com.example.sign_in_test.Data.model;

public class MsgContent extends Msg {
    private String content;

    public MsgContent(String content, int type, int user_id, int conversation_id) {
        super(type, user_id, conversation_id);
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
