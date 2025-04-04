
package com.example.sign_in_test;

import com.example.sign_in_test.utils.JDBCUtils;
import com.example.sign_in_test.Data.dao.MsgDao;

import java.sql.Connection;

public class TestJDBCConnection {

    public static void main(String[] args) {
        Connection conn = JDBCUtils.getConn();
        if (conn != null) {
            System.out.println("Connection successful!");
        } else {
            System.out.println("Connection failed!");
        }
    }

}
