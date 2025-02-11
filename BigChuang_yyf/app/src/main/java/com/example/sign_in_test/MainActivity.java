package com.example.sign_in_test;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.example.sign_in_test.dao.UserDao;
import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private MyHandler hand1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hand1 = new MyHandler(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 移除所有回调及消息
        hand1.removeCallbacksAndMessages(null);
    }

    public void reg(View view) {
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
    }

    public void login(View view) {
        EditText editTextName = findViewById(R.id.name);
        EditText editTextPassword = findViewById(R.id.password);

        final String name = editTextName.getText().toString();
        final String password = editTextPassword.getText().toString();

        new Thread(() -> {
            UserDao userDao = new UserDao();
            boolean success = userDao.login(name, password);
            hand1.sendEmptyMessage(success ? 1 : 0);
        }).start();
    }

    // 静态Handler内部类+弱引用
    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        MyHandler(MainActivity activity) {
            super(Looper.getMainLooper());
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null && !activity.isFinishing()) {
                String message = msg.what == 1 ? "登录成功" : "登录失败";
                Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_LONG).show();

                if (msg.what == 1) {
                    // 登录成功后进入 ChatActivity
                    Intent intent = new Intent(activity, ChatActivity.class);
                    activity.startActivity(intent);
                    activity.finish(); // 可选：关闭当前的 MainActivity
                }
            }
        }
    }
}