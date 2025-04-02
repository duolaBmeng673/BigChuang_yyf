package com.example.sign_in_test.Data.network;

public class ImageRequest {
    private String content; // 消息文本
    private String imageBase64; // 图片的Base64编码

    public ImageRequest( String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}
