package com.wangwangyz.LiteChatClient.Model.dao;


import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class DBCPUtil {
private static Properties properties = new Properties();
private static DataSource dataSource;
    DBCPUtil(){
    }
    static {
        try{
            InputStream is = DBCPUtil.class.getResourceAsStream("dbcp.properties");
            properties.load(is);
            dataSource = BasicDataSourceFactory.createDataSource(properties);
        }catch (IOException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * @Func
     * @Description 从连接池返回一个数据库连接,且设置不自动提交
     * @Pram
     * @Return  Connection
    */
    public static Connection getConnection(){
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
    /**
     * @Func
     * @Description 静态方法,释放数据库资源,回收到数据库连接池
     * @Pram
     * @Return
    */
    public static void releaseSource(@NotNull Connection con, @NotNull PreparedStatement prep, @NotNull ResultSet rs){
        try {
            prep.close();
            prep.close();
            con.close();
        } catch (SQLException troubles) {
            troubles.printStackTrace();
        }
    }
    public static void releaseSource(@NotNull Connection con, @NotNull PreparedStatement prep){
        try {
            prep.close();
            con.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public static void releaseSource(@NotNull Connection con){
        try {
            con.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
