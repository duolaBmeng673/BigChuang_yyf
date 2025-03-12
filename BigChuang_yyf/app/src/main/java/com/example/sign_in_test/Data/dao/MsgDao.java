package com.example.sign_in_test.Data.dao;

import android.util.Log;

import com.example.sign_in_test.Data.model.Msg;
import com.example.sign_in_test.utils.JDBCUtils;
import com.google.gson.Gson;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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


        try ( Connection conn = JDBCUtils.getConn();
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
    // 开启新对话时，生成一个 conversationId（示例用时间戳或 UUID）
    public int createNewConversationId() {
        // 这里可以用自增ID，也可以用 System.currentTimeMillis()，或数据库表 conversation 自增
        return (int) (System.currentTimeMillis() / 1000);
    }

    // 插入消息
    public boolean addMsg(Msg msg) {

        Gson gson = new Gson();
        String jsonMessage = gson.toJson(msg);
        System.out.println(jsonMessage);
        String sql = "INSERT INTO chat_history_ai (user_id, conversation_id, message,type) VALUES (?, ?, ?,?)";


        try (Connection conn = JDBCUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, msg.getUser_id());
            pstmt.setInt(2, msg.getConversation_id());
            pstmt.setString(3, msg.getContent());
            pstmt.setInt(4,msg.getType());
            System.out.println("insert successfully");
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 根据 conversationId 获取消息列表
    public List<Msg> getMsgsByConversation(int userId, int conversationId) {
        List<Msg> msgList = new ArrayList<>();
        String sql = "SELECT * FROM chat_history_ai WHERE user_id=? AND conversation_id=? ";


        try (Connection conn = JDBCUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, conversationId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                msgList.add(new Msg(
                        rs.getString("message"),
                        rs.getInt("type"),
                        userId,
                        conversationId
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return msgList;
    }
}
