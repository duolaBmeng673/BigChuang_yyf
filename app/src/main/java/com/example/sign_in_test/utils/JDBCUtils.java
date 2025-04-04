package com.example.sign_in_test.utils;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCUtils {

    static {

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static Connection getConn() {
        Connection  conn = null;
        try {
//            conn= DriverManager.getConnection("jdbc:mysql://192.168.2.230:3306/test","rjw_user","20050318Az");
            conn = DriverManager.getConnection("jdbc:mysql://10.68.0.254:3306/test?useSSL=false", "root", "20050318Az");

            System.out.println("Database connection established: " + conn);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return conn;
    }

    public static void close(Connection conn){
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
