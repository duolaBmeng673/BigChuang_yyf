package com.example.sign_in_test;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ChatService {
    @POST("/chat")  // 后端的聊天接口
    Call<ChatResponse> sendMessage(@Body ChatRequest request);
}

