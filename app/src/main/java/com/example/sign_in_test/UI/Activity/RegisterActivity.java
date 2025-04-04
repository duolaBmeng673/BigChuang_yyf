package com.example.sign_in_test.UI.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sign_in_test.Data.dao.UserDao;
import com.example.sign_in_test.Data.model.User;
import com.example.sign_in_test.R;

import java.lang.ref.WeakReference;

public class RegisterActivity extends AppCompatActivity {
    EditText name = null;
    EditText username = null;
    EditText password = null;
    EditText phone = null;
    EditText age = null;

    // 将 Handler 提取为静态类，避免内存泄漏
    private static class MyHandler extends Handler {
        private final WeakReference<RegisterActivity> mActivity;

        public MyHandler(RegisterActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            RegisterActivity activity = mActivity.get();
            if (activity != null) {
                if (msg.what == 0) {
                    Toast.makeText(activity.getApplicationContext(), "注册失败", Toast.LENGTH_LONG).show();
                }
                if (msg.what == 1) {
                    Toast.makeText(activity.getApplicationContext(), "该账号已经存在，请换一个账号", Toast.LENGTH_LONG).show();
                }
                if (msg.what == 2) {
                    Intent intent = new Intent();
                    intent.putExtra("a", "註冊");
                    activity.setResult(RESULT_CANCELED, intent);
                    activity.finish();
                }
            }
        }
    }

    private MyHandler hand = new MyHandler(this); // 使用静态 Handler 类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        phone = findViewById(R.id.phone);
        age = findViewById(R.id.age);
    }

    public void register(View view) {
        String cname = name.getText().toString();
        String cusername = username.getText().toString();
        String cpassword = password.getText().toString();
        String cphone = phone.getText().toString();
        int cgae = Integer.parseInt(age.getText().toString());

        if (cname.length() < 2 || cusername.length() < 2 || cpassword.length() < 2) {
            Toast.makeText(getApplicationContext(), "输入信息不符合要求请重新输入", Toast.LENGTH_LONG).show();
            return;
        }

        User user = new User();
        user.setName(cname);
        user.setUsername(cusername);
        user.setPassword(cpassword);
        user.setAge(cgae);
        user.setPhone(cphone);

        new Thread() {
            @Override
            public void run() {
                int msg = 0;
                UserDao userDao = new UserDao();
                User uu = userDao.findUser(user.getName());

                if (uu != null) {
                    msg = 1;
                }

                boolean flag = userDao.register(user);
                if (flag) {
                    msg = 2;
                }
                hand.sendEmptyMessage(msg);
            }
        }.start();
    }
}
