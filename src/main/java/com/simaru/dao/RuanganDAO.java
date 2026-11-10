package com.simaru.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.simaru.model.Ruangan;

public class RuanganDAO implements CrudOperation<Ruangan> {
    // Implement CRUD operations for Ruangan here
    @Override
    public void tambah(Ruangan ruangan) {
        Connection conn = Database.getConnection();
        try {
            String query = "INSERT INTO ruangan (nama_ruangan, kapasitas, fasilitas) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, ruangan.getNamaRuangan());
            pstmt.setInt(2, ruangan.getKapasitas());
            pstmt.setString(3, ruangan.getFasilitas());
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error during adding Ruangan: " + e.getMessage());
        }
    }
    @Override
    public void ubah(Ruangan ruangan) {
        Connection conn = Database.getConnection();
        try {
            String query = "UPDATE ruangan SET nama_ruangan = ?, kapasitas = ?, fasilitas = ?, lokasi = ? WHERE id_ruangan = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, ruangan.getNamaRuangan());
            pstmt.setInt(2, ruangan.getKapasitas());
            pstmt.setString(3, ruangan.getFasilitas());
            pstmt.setString(4, ruangan.getLokasi());
            pstmt.setInt(5, ruangan.getIdRuangan());
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error during updating Ruangan: " + e.getMessage());
        }
        // Implement the logic to update an existing Ruangan in the database
    }
    @Override
    public void hapus(int id_ruangan) {
        Connection conn = Database.getConnection();
        try {
            String query = "DELETE FROM ruangan WHERE id_ruangan = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id_ruangan);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error during deleting Ruangan: " + e.getMessage());
        }
        // Implement the logic to delete a Ruangan from the database based on its ID
    }
    @Override
    public List<Ruangan> tampilkan() {
        Connection conn = Database.getConnection();
        try {
            String query = "SELECT * FROM ruangan";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            if (rs != null) {
                List<Ruangan> ruanganList = new ArrayList<>();
                while (rs.next()) {
                    int id = rs.getInt("id_ruangan");
                    String nama = rs.getString("nama_ruangan");
                    int kapasitas = rs.getInt("kapasitas");
                    String lokasi = rs.getString("lokasi");
                    String fasilitas = rs.getString("fasilitas");
                    String status = rs.getString("status");
                    Ruangan ruangan = new Ruangan(id, nama, kapasitas, fasilitas, lokasi);
                    ruangan.setStatus(status);
                    ruanganList.add(ruangan);
                }
                return ruanganList;
            }
        } catch (Exception e) {
            System.err.println("Error during displaying Ruangan: " + e.getMessage());
        }
        return null;
    }

    public Ruangan cari(int id_ruangan) {Connection conn = Database.getConnection();
        try {
            String query = "SELECT * FROM ruangan WHERE id_ruangan = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id_ruangan);
            ResultSet rs = pstmt.executeQuery();
            if (rs != null && rs.next()) {
                int id = rs.getInt("id_ruangan");
                String nama = rs.getString("nama_ruangan");
                int kapasitas = rs.getInt("kapasitas");
                String lokasi = rs.getString("lokasi");
                String fasilitas = rs.getString("fasilitas");
                String status = rs.getString("status");
                Ruangan ruangan = new Ruangan(id, nama, kapasitas, fasilitas, lokasi);
                ruangan.setStatus(status);
                return ruangan;
            }
        } catch (Exception e) {
            System.err.println("Error during displaying Ruangan: " + e.getMessage());
        }
        return null;
    }
}
