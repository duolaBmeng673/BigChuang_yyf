package com.example.sign_in_test.Data.model;

public class MsgImage extends Msg {
    private String imageUrl;
    private String imageBase64;

    public MsgImage(String imageBase64, int type, int user_id, int conversation_id) {
        super(type, user_id, conversation_id);
        this.imageBase64 = imageBase64;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}
