package com.example.sign_in_test.Data.dao;

import com.example.sign_in_test.Data.model.Msg;
import com.example.sign_in_test.Data.model.MsgContent;
import com.example.sign_in_test.Data.model.MsgImage;
import com.example.sign_in_test.Data.network.ImageRequest;
import com.example.sign_in_test.Data.network.ImageResponse;
import com.example.sign_in_test.Data.network.ImageService;
import com.example.sign_in_test.utils.JDBCUtils;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MsgDao {

    public int getOrCreateConversationId(int userId) {
        int conversationId = getActiveConversationId(userId);
        if (conversationId == -1) {
            conversationId = createNewConversationId();
        }
        return conversationId;
    }

    private int getActiveConversationId(int userId) {
        String sql = "SELECT conversation_id FROM chat_history_ai WHERE user_id = ?";
        try (Connection conn = JDBCUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("conversation_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int createNewConversationId() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    public boolean addMsg(Msg msg) {
        String sql;
        boolean isImage = msg instanceof MsgImage;

        if (isImage) {
            MsgImage msgImage = (MsgImage) msg;
            String imageUrl = uploadImage(msgImage.getImageBase64());
            if (imageUrl == null) {
                System.out.println("Image upload failed.");
                return false;
            }
            msgImage.setImageUrl(imageUrl);

            sql = "INSERT INTO chat_history_ai (user_id, conversation_id, image_url, type, Data_type) VALUES (?, ?, ?, ?, ?)";
        } else {
            sql = "INSERT INTO chat_history_ai (user_id, conversation_id, message, type, Data_type) VALUES (?, ?, ?, ?, ?)";
        }

        try (Connection conn = JDBCUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, msg.getUser_id());
            pstmt.setInt(2, msg.getConversation_id());

            if (isImage) {
                pstmt.setString(3, ((MsgImage) msg).getImageUrl());
            } else {
                pstmt.setString(3, ((MsgContent) msg).getContent());
            }

            pstmt.setInt(4, msg.getType());
            pstmt.setInt(5, isImage ? Msg.TYPE_IMAGE : Msg.TYPE_TEXT);  // 确保 Data_type 存入数据库
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<Msg> getMsgsByConversation(int userId, int conversationId) {
        List<Msg> msgList = new ArrayList<>();
        String sql = "SELECT * FROM chat_history_ai WHERE user_id=? AND conversation_id=?";

        try (Connection conn = JDBCUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, conversationId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int Data_type = rs.getInt("Data_type");

                if (Data_type == Msg.TYPE_TEXT) {
                    msgList.add(new MsgContent(
                            rs.getString("message"),
                            rs.getInt("type"),
                            userId,
                            conversationId
                    ));
                } else if (Data_type == Msg.TYPE_IMAGE) {
                    msgList.add(new MsgImage(
                            rs.getString("image_url"),
                            rs.getInt("type"),
                            userId,
                            conversationId
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return msgList;
    }

    private String uploadImage(String imageBase64) {
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:5000/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ImageService service = retrofit.create(ImageService.class);
            Call<ImageResponse> call = service.uploadImage(new ImageRequest(imageBase64));

            Response<ImageResponse> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body().getFilePath();  // 服务器返回的图片 URL
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
