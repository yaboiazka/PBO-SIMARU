package com.simaru.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.simaru.util.Alerts;


public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/manajemen_ruangan";
    private static final String USER = "root";
    private static final String PASS = "Keisuke3S";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    public static Connection getConnection() {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException | SQLException e) {
            Alerts.error("Error connecting to database: " + e.getMessage());
            return null;
        }
    }
}
