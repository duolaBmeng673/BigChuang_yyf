package com.example.sign_in_test.Data.network;

public class ImageResponse {
    private String message;  // 服务器返回的消息，例如 "Image uploaded successfully"
    private String file_path; // 服务器返回的图片 URL

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFilePath() {
        return file_path;  // 让 Retrofit 解析服务器返回的图片 URL
    }

    public void setFilePath(String file_path) {
        this.file_path = file_path;
    }
}
