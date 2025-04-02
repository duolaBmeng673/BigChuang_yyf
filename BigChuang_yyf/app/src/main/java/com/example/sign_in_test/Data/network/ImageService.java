package com.example.sign_in_test.Data.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ImageService {

    @POST("/upload_image") // 确保你的后端 API 能够解析 JSON 格式的 Base64 图片
    Call<ImageResponse> uploadImage(@Body ImageRequest imageRequest);
}
