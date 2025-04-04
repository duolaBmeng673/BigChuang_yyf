package com.example.sign_in_test.UI.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.example.sign_in_test.Data.dao.MsgDao;
import com.example.sign_in_test.Data.dao.UserDao;
import com.example.sign_in_test.Data.network.ChatRequest;
import com.example.sign_in_test.Data.network.ChatResponse;
import com.example.sign_in_test.Data.network.ChatService;
import com.example.sign_in_test.Data.model.Msg;
import com.example.sign_in_test.UI.Adapter.MsgAdapter;
import com.example.sign_in_test.R;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private List<Msg> msgList = new ArrayList<>();
    private MsgDao msgdao;
    private UserDao userDao;
    private RecyclerView msgRecyclerView;
    private EditText inputText;
    private Button send;
    private LinearLayoutManager layoutManager;
    private MsgAdapter adapter;

    private Retrofit retrofit;
    private ChatService chatService;

    private int userId;
    private int conversationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 初始化 RecyclerView 和输入框
        msgRecyclerView = findViewById(R.id.msg_recycler_view);
        inputText = findViewById(R.id.input_text);
        send = findViewById(R.id.send);
        layoutManager = new LinearLayoutManager(this);
        adapter = new MsgAdapter(msgList);

        msgRecyclerView.setLayoutManager(layoutManager);
        msgRecyclerView.setAdapter(adapter);

        userId = getIntent().getIntExtra("user_id", -1); // 获取 user_id
        System.out.println(userId);

        msgdao = new MsgDao();


        new Thread(() -> {
            conversationId = msgdao.getOrCreateConversationId(userId);
            System.out.println(conversationId);
            List<Msg> messages = msgdao.getMsgsByConversation(userId, conversationId);
            System.out.println(messages.size());

            runOnUiThread(() -> {
                msgList.addAll(messages);
                adapter.notifyItemInserted(msgList.size() - 1);
                msgRecyclerView.scrollToPosition(msgList.size() - 1); // 更新 UI
            });
        }).start();




        // 创建 OkHttpClient，设置超时时间
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)  // 连接超时
                .readTimeout(120, TimeUnit.SECONDS)     // 读取超时
                .writeTimeout(120, TimeUnit.SECONDS)    // 写入超时
                .build();

        // 初始化 Retrofit，使用自定义的 OkHttpClient
        retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5000")  // 后端的地址，替换成你实际的地址
                .client(okHttpClient)              // 设置 OkHttpClient
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        chatService = retrofit.create(ChatService.class);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                if (!content.equals("")) {
                    // 显示用户消息
                    Msg msg =new Msg(content, Msg.TYPE_SEND,userId,conversationId);
                    new Thread(() -> msgdao.addMsg(msg)).start();

                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size() - 1);
                    msgRecyclerView.scrollToPosition(msgList.size() - 1);
                    inputText.setText("");  // 清空输入框



                    // 创建请求对象
                    ChatRequest request = new ChatRequest(content);

                    // 发起网络请求
                    chatService.sendMessage(request).enqueue(new Callback<ChatResponse>() {
                        @Override
                        public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                            if (response.isSuccessful()) {
                                // 获取后端返回的响应
                                String botReply = response.body().getResponse();
                                // 显示后端模型的回复
                                Msg msg1 = new Msg(botReply, Msg.TYPE_RECEIVED,userId,conversationId);

                                new Thread(() -> msgdao.addMsg(msg1)).start();

                                new Handler(Looper.getMainLooper()).post(() -> {
                                    msgList.add(msg1);
                                    adapter.notifyItemInserted(msgList.size() - 1);
                                    msgRecyclerView.scrollToPosition(msgList.size() - 1);
                                });

                            }
                        }

                        @Override
                        public void onFailure(Call<ChatResponse> call, Throwable t) {
                            // 网络请求失败时的处理
                            t.printStackTrace();
                        }
                    });
                }
            }
        });
    }

//    private List<Msg> getData() {
//        List<Msg> list = new ArrayList<>();
//        list.add(new Msg("Hello", Msg.TYPE_RECEIVED));  // 初始消息
//        return list;
//    }
}
