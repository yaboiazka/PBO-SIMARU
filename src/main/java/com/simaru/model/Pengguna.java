package com.simaru.model;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.simaru.dao.Database;
import com.simaru.util.Hash;
import com.simaru.util.Navigator;
import com.simaru.util.Session;

public abstract class Pengguna {
    private int id_pengguna;
    private String nama;
    private String email;
    private String password;

    public Pengguna(int id_pengguna, String nama, String email, String password) {
        this.id_pengguna = id_pengguna;
        this.nama = nama;
        this.email = email;
        this.password = password;
    }


    public int getIdPengguna() {
        return id_pengguna;
    }
    public void setIdPengguna(int id_pengguna) {
        this.id_pengguna = id_pengguna;
    }

    public String getNama() {
        return nama;
    }
    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }


    public boolean login(String email, String password) {
        Connection conn = Database.getConnection();
        String query = "SELECT * FROM pengguna WHERE email = ? AND password = ?";
        try {
            password = Hash.hashSHA256(password);
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            if (pstmt.executeQuery().next()) {
                System.out.println("Login berhasil!");
                return true;
            } else {
                System.out.println("Email atau password salah.");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            return false;
        }
    }
    public boolean logout(){
        try {
            Session.clear();
            Navigator.show("Login", "SIMARU - Login", 420, 460);
            return true;
        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
            return false;
        }
    }
        
    public abstract String getHakAkses();
    
}
