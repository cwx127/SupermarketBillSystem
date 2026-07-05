package com.example.supermarketbillsystem.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtil {
    private static final String URL = "jdbc:oracle:thin:@127.0.0.1:1521:ORCL";
    private static final String USER = "scott";
    private static final String PASSWORD = "91994534";

    // 加载Oracle驱动，修复类名下划线错误
    static {
        try{
            Class.forName("oracle.jdbc.driver.OracleDriver");
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 获取连接
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // 查询专用：关闭连接、预编译对象、结果集
    public static void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 增删改专用重载方法，修复传参语法错误
    public static void close(Connection conn, PreparedStatement pstmt){
        close(conn, pstmt, null);
    }
}