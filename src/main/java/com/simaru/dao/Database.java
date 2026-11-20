package com.simaru.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/manajemen_ruangan";
    private static final String USER = "root";
    private static final String PASS =
        System.getenv("SIMARU_DB_PASSWORD");
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    public static Connection getConnection() {

            if (PASS == null || PASS.isBlank()) {
                System.err.println(
                        "SIMARU_DB_PASSWORD belum diatur!"
                );
                return null;
            }try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
