package com.bookstore.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
	 private static final String URL = "jdbc:sqlserver://DESKTOP-JBDM2OH\\SQLEXPRESS;databaseName=QLnhasachpart2;encrypt=true;trustServerCertificate=true";
	    private static final String USER = "sa";
	    private static final String PASSWORD = "vietdepzai";

	    public static Connection getConnection() {
	        try {
	            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
	            System.out.println("✅ Kết nối database thành công!");
	            return conn;
	        } catch (ClassNotFoundException e) {
	            System.err.println("❌ Không tìm thấy driver JDBC SQL Server!");
	            e.printStackTrace();
	        } catch (SQLException e) {
	            System.err.println("❌ Lỗi SQL khi kết nối: " + e.getMessage());
	            e.printStackTrace();
	        }
	        return null;
	    }
}