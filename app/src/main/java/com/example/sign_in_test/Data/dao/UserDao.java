package com.example.sign_in_test.Data.dao;


import com.example.sign_in_test.Data.model.User;
import com.example.sign_in_test.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {


    public boolean login(String name, String password) {
        String sql = "SELECT * FROM users WHERE name = ? AND password = ?";
        try (Connection con = JDBCUtils.getConn();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, name);
            pst.setString(2, password);

            return pst.executeQuery().next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean register(User user){

        String sql = "insert into users(name,username,password,age,phone) values (?,?,?,?,?)";



        try(Connection  con = JDBCUtils.getConn();
            PreparedStatement pst=con.prepareStatement(sql)) {

            pst.setString(1,user.getName());
            pst.setString(2,user.getUsername());
            pst.setString(3,user.getPassword());
            pst.setInt(4,user.getAge());
            pst.setString(5,user.getPhone());

            int value = pst.executeUpdate();

            if(value>0){
                return true;
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public User findUser(String name){

        String sql = "select * from users where name = ?";


        User user = null;
        try {Connection  con = JDBCUtils.getConn();
            PreparedStatement pst=con.prepareStatement(sql);

            pst.setString(1,name);

            ResultSet rs = pst.executeQuery();

            while (rs.next()){

                int id = rs.getInt(1);
                String namedb = rs.getString(2);
                String username = rs.getString(3);
                String passworddb  = rs.getString(4);
                int age = rs.getInt(5);
                String phone = rs.getString(6);
                user = new User(id,namedb,username,passworddb,age,phone);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return user;
    }




}


