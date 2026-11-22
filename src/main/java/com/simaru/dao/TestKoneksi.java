package com.simaru.dao;

import java.sql.Connection;
import java.sql.SQLException;

public class TestKoneksi {

    public static void main(String[] args) {
        System.out.println("Mencoba koneksi ke database...");

        Connection conn = Database.getConnection();
        if (conn == null) {
            System.out.println("KONEKSI GAGAL! Periksa kredensial di Database.java dan pastikan MySQL berjalan.");
            return;
        }
        try (conn) {
            System.out.println("KONEKSI BERHASIL! Aplikasi sudah terhubung ke MySQL.");
        } catch (SQLException e) {
            System.out.println("KONEKSI GAGAL!");
            e.printStackTrace();
        }
    }
}
