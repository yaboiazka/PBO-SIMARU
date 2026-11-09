package com.simaru.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.simaru.model.Admin;
import com.simaru.model.Dosen;
import com.simaru.model.Mahasiswa;
import com.simaru.model.Pengguna;
import com.simaru.util.Hash;


public class PenggunaDAO implements CrudOperation<Pengguna> {

    public static Pengguna login(String email, String password) {
        Connection conn = Database.getConnection();
        String query = "SELECT p.*, ps.fakultas, ps.program_studi " + "FROM pengguna p " + "LEFT JOIN program_studi ps ON p.id_prodi = ps.id_prodi " + "WHERE p.email = ? AND p.password = ?";
            try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, email);
            pstmt.setString(2, Hash.hashSHA256(password));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int id_pengguna = rs.getInt("id_pengguna");
                String nama = rs.getString("nama");
                String db_email = rs.getString("email");
                String db_password = rs.getString("password");
                String role = rs.getString("role");
                if ("ADMIN".equals(role)) {
                    Admin admin = new Admin(id_pengguna, nama, db_email, db_password, role);
                    return admin;
                } else if ("DOSEN".equals(role)) {
                    String nip = rs.getString("nim_nip");
                    String fakultas = rs.getString("fakultas");
                    Dosen dosen = new Dosen(id_pengguna, nama, db_email, db_password, nip, fakultas);
                    return dosen;
                } else {
                    String nim = rs.getString("nim_nip");
                    String prodi = rs.getString("program_studi");
                    Mahasiswa mahasiswa = new Mahasiswa(id_pengguna, nama, db_email, db_password, nim, prodi);
                    return mahasiswa;
                }
            }
        } catch (Exception e) {
            System.err.println("Error during checking user: " + e.getMessage());
            return null;
        }
        return null;
    }
    public static boolean register(Pengguna pengguna) {
        Connection conn = Database.getConnection();
        String query = "INSERT INTO pengguna (nama, email, password) VALUES (?, ?, ?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, pengguna.getNama());
            pstmt.setString(2, pengguna.getEmail());
            pstmt.setString(3, Hash.hashSHA256(pengguna.getPassword()));
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                // System.out.println("Registrasi berhasil!");
                return true;
            } else {
                // System.out.println("Registrasi gagal.");
                return false;
            }
        } catch (Exception e) {
            // System.err.println("Error during registration: " + e.getMessage());
            return false;
        }
    }

    public void tambah(Pengguna pengguna) {
        Connection conn = Database.getConnection();
        String query = "INSERT INTO pengguna (nama, email, password) VALUES (?, ?, ?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, pengguna.getNama());
            pstmt.setString(2, pengguna.getEmail());
            pstmt.setString(3, Hash.hashSHA256(pengguna.getPassword()));
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error during adding user: " + e.getMessage());
        }

    }
    public void ubah(Pengguna pengguna) {
        Connection conn = Database.getConnection();
        String query = "UPDATE pengguna SET nama = ?, email = ?, password = ? WHERE id_pengguna = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, pengguna.getNama());
            pstmt.setString(2, pengguna.getEmail());
            pstmt.setString(3, Hash.hashSHA256(pengguna.getPassword()));
            pstmt.setInt(4, pengguna.getIdPengguna());
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error during updating user: " + e.getMessage());
        }
    }
    public void hapus(int id_pengguna) {
        Connection conn = Database.getConnection();
        String query = "DELETE FROM pengguna WHERE id_pengguna = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id_pengguna);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error during deleting user: " + e.getMessage());
        }
    }
    public List<Pengguna> tampilkan() {
        Connection conn = Database.getConnection();
        String query = "SELECT p.*, ps.fakultas, ps.program_studi " + "FROM pengguna p " + "LEFT JOIN program_studi ps ON p.id_prodi = ps.id_prodi";
        List<Pengguna> penggunaList = new ArrayList<>();
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id_pengguna = rs.getInt("id_pengguna");
                String nama = rs.getString("nama");
                String email = rs.getString("email");
                String password = rs.getString("password");
                String role = rs.getString("role");
                if ("ADMIN".equals(role)) {
                    Admin admin = new Admin(id_pengguna, nama, email, password, role);
                    penggunaList.add(admin);
                } else if ("DOSEN".equals(role)) {
                    String nip = rs.getString("nim_nip");
                    String fakultas = rs.getString("fakultas");
                    Dosen dosen = new Dosen(id_pengguna, nama, email, password, nip, fakultas);
                    penggunaList.add(dosen);
                } else {
                    String nim = rs.getString("nim_nip");
                    String prodi = rs.getString("program_studi");
                    Mahasiswa mahasiswa = new Mahasiswa(id_pengguna, nama, email, password, nim, prodi);
                    penggunaList.add(mahasiswa);
                }
            }
        } catch (Exception e) {
            System.err.println("Error during fetching users: " + e.getMessage());
        }
        return penggunaList;
    }
    
    public static boolean chekiAdaAtauGakAkunnya(String email, String password) {
        Connection conn = Database.getConnection();
        String query = "SELECT * FROM pengguna WHERE email = ? AND password = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, email);
            pstmt.setString(2, Hash.hashSHA256(password));
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.err.println("Error during checking user: " + e.getMessage());
            return false;
        }
    }
    public static String getRole(String email) {
        Connection conn = Database.getConnection();
        String query = "SELECT role FROM pengguna WHERE email = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("role");
            } else {
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error during fetching user role: " + e.getMessage());
            return null;
        }
    }
}
