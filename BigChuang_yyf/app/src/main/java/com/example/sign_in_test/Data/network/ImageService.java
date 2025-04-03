package com.example.sign_in_test.Data.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ImageService {

    @POST("/upload_image") // 确保你的后端 API 能够解析 JSON 格式的 Base64 图片
    Call<ImageResponse> uploadImage(@Body ImageRequest imageRequest);

    public static Bitmap base64ToBitmap(String base64Str) {
        try {
            byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Bitmap 转 Base64（如果你有上传功能）
    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
