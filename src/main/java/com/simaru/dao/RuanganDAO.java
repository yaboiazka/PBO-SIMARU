package com.simaru.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.simaru.model.Ruangan;
import com.simaru.util.Alerts;

public class RuanganDAO implements CrudOperation<Ruangan> {
    // Implement CRUD operations for Ruangan here
    private List<Ruangan> query(String sql, String param) throws SQLException {
        Connection conn = Database.getConnection();
        List<Ruangan> hasil = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (param != null) {
                ps.setString(1, param);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id_ruangan");
                    String nama = rs.getString("nama_ruangan");
                    int kapasitas = rs.getInt("kapasitas");
                    String lokasi = rs.getString("lokasi");
                    String fasilitas = rs.getString("fasilitas");
                    String status = rs.getString("status");
                    Ruangan ruangan = new Ruangan(id, nama, kapasitas, fasilitas, lokasi);
                    ruangan.setStatus(status);
                    hasil.add(ruangan);
                }
            }
        }
        return hasil;
    }
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
            Alerts.error("Error during adding Ruangan: " + e.getMessage());
            System.err.println("Error during adding Ruangan: " + e.getMessage());
        }
    }
    @Override
    public void ubah(Ruangan ruangan) {
        Connection conn = Database.getConnection();
        try {
            // Tambahkan status = ? ke dalam query UPDATE
            String query = "UPDATE ruangan SET nama_ruangan = ?, kapasitas = ?, fasilitas = ?, lokasi = ?, status = ? WHERE id_ruangan = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, ruangan.getNamaRuangan());
            pstmt.setInt(2, ruangan.getKapasitas());
            pstmt.setString(3, ruangan.getFasilitas());
            pstmt.setString(4, ruangan.getLokasi());
            pstmt.setString(5, ruangan.getStatus());
            pstmt.setInt(6, ruangan.getIdRuangan());
            pstmt.executeUpdate();
        } catch (Exception e) {
            Alerts.error("Error during updating Ruangan: " + e.getMessage());
            System.err.println("Error during updating Ruangan: " + e.getMessage());
        }
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
            Alerts.error("Error during deleting Ruangan: " + e.getMessage());
            System.err.println("Error during deleting Ruangan: " + e.getMessage());
        }
        // Implement the logic to delete a Ruangan from the database based on its ID
    }
    @Override
    public List<Ruangan> tampilkan() {
        String query = "SELECT * FROM ruangan";
        Connection conn = Database.getConnection();
        try {
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
            Alerts.error("Error during displaying Ruangan: " + e.getMessage());
            System.err.println("Error during displaying Ruangan: " + e.getMessage());
        }
        return null;
    }
    public List<Integer> tampilkanIdSaja() {
        String query = "SELECT id_ruangan FROM ruangan";
        Connection conn = Database.getConnection();
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            if (rs != null) {
                List<Integer> idRuanganList = new ArrayList<>();
                while (rs.next()) {
                    int id = rs.getInt("id_ruangan");
                    idRuanganList.add(id);
                }
                return idRuanganList;
            }
        } catch (Exception e) {
            Alerts.error("Error during displaying Ruangan: " + e.getMessage());
            System.err.println("Error during displaying Ruangan: " + e.getMessage());
        }
        return null;
    }

    public Ruangan cari(int id_ruangan) {
        Connection conn = Database.getConnection();
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
            Alerts.error("Error during searching Ruangan: " + e.getMessage());
            System.err.println("Error during displaying Ruangan: " + e.getMessage());
        }
        return null;
    }
    public List<Ruangan> filter(String status) throws SQLException {
        if (status == null || status.isBlank() || status.equalsIgnoreCase("Semua")) {
            return tampilkan();
        }
        return query("SELECT * FROM ruangan WHERE status= ? ORDER BY nama_ruangan", status);
    }

    public static void refresh() {
        Connection conn = Database.getConnection();
        
        String sqlDipakai = "SELECT * FROM ruangan " + 
                    "WHERE id_ruangan IN (" +
                    "  SELECT id_ruangan FROM peminjaman " +
                    "  WHERE tanggal_pinjam = CURDATE() " +
                    "  AND CURTIME() BETWEEN jam_mulai AND jam_selesai " +
                    "  AND status_verifikasi = 'Disetujui')";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sqlDipakai)) {
            
            RuanganDAO dao = new RuanganDAO();
            while (rs.next()) {
                Ruangan r = new Ruangan(
                    rs.getInt("id_ruangan"),
                    rs.getString("nama_ruangan"),
                    rs.getInt("kapasitas"),
                    rs.getString("fasilitas"),
                    rs.getString("lokasi")
                );
                r.setStatus("Dipakai");
                dao.ubah(r);
            }
        } catch (Exception e) {
            Alerts.error("Error refresh dipakai: " + e.getMessage());
        }

        String sqlTersedia = "SELECT * FROM ruangan " + 
                    "WHERE status = 'Dipakai' AND id_ruangan NOT IN (" +
                    "  SELECT id_ruangan FROM peminjaman " +
                    "  WHERE tanggal_pinjam = CURDATE() " +
                    "  AND CURTIME() BETWEEN jam_mulai AND jam_selesai " +
                    "  AND status_verifikasi = 'Disetujui')";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sqlTersedia)) {
            
            RuanganDAO dao = new RuanganDAO();
            while (rs.next()) {
                Ruangan r = new Ruangan(
                    rs.getInt("id_ruangan"),
                    rs.getString("nama_ruangan"),
                    rs.getInt("kapasitas"),
                    rs.getString("fasilitas"),
                    rs.getString("lokasi")
                );
                r.setStatus("Tersedia");
                dao.ubah(r);
            }
        } catch (Exception e) {
            Alerts.error("Error refresh tersedia: " + e.getMessage());
        }
    }
}
