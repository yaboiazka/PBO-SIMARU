package com.simaru.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestKoneksi {
    // Sesuaikan URL, USER, dan PASS dengan yang ada di kodemu sebelumnya
    private static final String URL = "jdbc:mysql://localhost:3306/manajemen_ruangan";
    private static final String USER = "root";
    private static final String PASS = "Keisuke3S";

    public static void main(String[] args) {
        System.out.println("Mencoba koneksi ke database...");

        // Coba membuka koneksi
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            System.out.println("✅ KONEKSI BERHASIL! Aplikasi sudah terhubung ke MySQL.");
        } catch (SQLException e) {
            System.out.println("❌ KONEKSI GAGAL!");
            e.printStackTrace(); // Menampilkan detail errornya
        }
    }
}