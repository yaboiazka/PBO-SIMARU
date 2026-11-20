
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

    // Data tambahan untuk CRUD Pengguna
    private String nimNip;
    private Integer idProdi;
    private String informasiProdi;

    public Pengguna(int id_pengguna, String nama,
                    String email, String password) {
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

    public String getNimNip() {
        return nimNip;
    }

    public void setNimNip(String nimNip) {
        this.nimNip = nimNip;
    }

    public Integer getIdProdi() {
        return idProdi;
    }

    public void setIdProdi(Integer idProdi) {
        this.idProdi = idProdi;
    }

    public String getInformasiProdi() {
        return informasiProdi;
    }

    public void setInformasiProdi(String informasiProdi) {
        this.informasiProdi = informasiProdi;
    }

    public boolean login(String email, String password) {
        String query = "SELECT * FROM pengguna WHERE email = ? AND password = ?";

        try (Connection conn = Database.getConnection()) {

            if (conn == null) return false;

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, email);
                pstmt.setString(2, Hash.hashSHA256(password));

                return pstmt.executeQuery().next();
            }

        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            return false;
        }
    }

    public boolean logout() {
        try {
            Session.clear();
            Navigator.show("Login", "SIMARU - Login", 420, 460);
            return true;
        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
            return false;
        }
    }

    public abstract String getRole();

    public abstract String getHakAkses();
}
