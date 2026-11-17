package com.simaru.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import com.simaru.model.Ruangan;
import com.simaru.util.Alerts;
import com.simaru.util.Status;

public class RuanganDAO implements CrudOperation<Ruangan> {

    private List<Ruangan> query(String sql, String param) throws SQLException {
        List<Ruangan> hasil = new ArrayList<>();
        Connection conn = Database.getConnection();
        if (conn == null) {
            Alerts.error("Tidak dapat terhubung ke database. Pastikan server MySQL berjalan.");
            return hasil;
        }
        try (Connection koneksi = conn;
                PreparedStatement ps = koneksi.prepareStatement(sql)) {
            if (param != null) {
                ps.setString(1, param);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hasil.add(map(rs));
                }
            }
        }
        return hasil;
    }

    private Ruangan map(ResultSet rs) throws SQLException {
        Ruangan ruangan = new Ruangan(
                rs.getInt("id_ruangan"),
                rs.getString("nama_ruangan"),
                rs.getInt("kapasitas"),
                rs.getString("fasilitas"),
                rs.getString("lokasi"));
        ruangan.setStatus(rs.getString("status"));
        return ruangan;
    }

    @Override
    public void tambah(Ruangan ruangan) {
        tambahAman(ruangan);
    }

    public boolean tambahAman(Ruangan ruangan) {
        if (ruangan == null || kosong(ruangan.getNamaRuangan()) || kosong(ruangan.getLokasi())
                || ruangan.getKapasitas() <= 0) {
            Alerts.warning("Nama ruangan, lokasi, dan kapasitas yang valid wajib diisi.");
            return false;
        }
        String status = kosong(ruangan.getStatus()) ? Status.TERSEDIA : ruangan.getStatus();
        Connection conn = Database.getConnection();
        if (conn == null) {
            Alerts.error("Tidak dapat terhubung ke database. Pastikan server MySQL berjalan.");
            return false;
        }
        String sql = "INSERT INTO ruangan (nama_ruangan, lokasi, kapasitas, fasilitas, status) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection koneksi = conn;
                PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, ruangan.getNamaRuangan().trim());
            ps.setString(2, ruangan.getLokasi().trim());
            ps.setInt(3, ruangan.getKapasitas());
            ps.setString(4, ruangan.getFasilitas());
            ps.setString(5, status);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Alerts.error("Gagal menambah ruangan: " + e.getMessage());
            System.err.println("Gagal menambah ruangan: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void ubah(Ruangan ruangan) {
        ubahAman(ruangan);
    }

    public boolean ubahAman(Ruangan ruangan) {
        if (ruangan == null || ruangan.getIdRuangan() <= 0) {
            Alerts.warning("Pilih ruangan yang akan diubah terlebih dahulu.");
            return false;
        }
        if (kosong(ruangan.getNamaRuangan()) || kosong(ruangan.getLokasi()) || ruangan.getKapasitas() <= 0) {
            Alerts.warning("Nama ruangan, lokasi, dan kapasitas yang valid wajib diisi.");
            return false;
        }
        String status = kosong(ruangan.getStatus()) ? Status.TERSEDIA : ruangan.getStatus();
        Connection conn = Database.getConnection();
        if (conn == null) {
            Alerts.error("Tidak dapat terhubung ke database. Pastikan server MySQL berjalan.");
            return false;
        }
        String sql = "UPDATE ruangan SET nama_ruangan = ?, lokasi = ?, kapasitas = ?, "
                + "fasilitas = ?, status = ? WHERE id_ruangan = ?";
        try (Connection koneksi = conn;
                PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, ruangan.getNamaRuangan().trim());
            ps.setString(2, ruangan.getLokasi().trim());
            ps.setInt(3, ruangan.getKapasitas());
            ps.setString(4, ruangan.getFasilitas());
            ps.setString(5, status);
            ps.setInt(6, ruangan.getIdRuangan());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Alerts.error("Gagal mengubah ruangan: " + e.getMessage());
            System.err.println("Gagal mengubah ruangan: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void hapus(int id_ruangan) {
        hapusAman(id_ruangan);
    }

    public boolean hapusAman(int id_ruangan) {
        if (id_ruangan <= 0) {
            Alerts.warning("Pilih ruangan yang akan dihapus terlebih dahulu.");
            return false;
        }
        Connection conn = Database.getConnection();
        if (conn == null) {
            Alerts.error("Tidak dapat terhubung ke database. Pastikan server MySQL berjalan.");
            return false;
        }
        String sql = "DELETE FROM ruangan WHERE id_ruangan = ?";
        try (Connection koneksi = conn;
                PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setInt(1, id_ruangan);
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            Alerts.error("Ruangan tidak bisa dihapus karena masih dipakai pada data peminjaman.");
            System.err.println("Hapus ruangan ditolak FK: " + e.getMessage());
            return false;
        } catch (SQLException e) {
            Alerts.error("Gagal menghapus ruangan: " + e.getMessage());
            System.err.println("Gagal menghapus ruangan: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Ruangan> tampilkan() {
        try {
            return query("SELECT * FROM ruangan ORDER BY nama_ruangan", null);
        } catch (SQLException e) {
            Alerts.error("Gagal memuat ruangan: " + e.getMessage());
            System.err.println("Gagal memuat ruangan: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Ruangan cari(int id_ruangan) {
        Connection conn = Database.getConnection();
        if (conn == null) {
            Alerts.error("Tidak dapat terhubung ke database. Pastikan server MySQL berjalan.");
            return null;
        }
        String sql = "SELECT * FROM ruangan WHERE id_ruangan = ?";
        try (Connection koneksi = conn;
                PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setInt(1, id_ruangan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            Alerts.error("Gagal mencari ruangan: " + e.getMessage());
            System.err.println("Gagal mencari ruangan: " + e.getMessage());
        }
        return null;
    }

    public List<Ruangan> filter(String status) throws SQLException {
        if (status == null || status.isBlank() || status.equalsIgnoreCase(Status.SEMUA)) {
            return tampilkan();
        }
        return query("SELECT * FROM ruangan WHERE status = ? ORDER BY nama_ruangan", status);
    }

    public List<Ruangan> tampilkanTersedia() {
        try {
            return query("SELECT * FROM ruangan WHERE status = ? ORDER BY nama_ruangan", Status.TERSEDIA);
        } catch (SQLException e) {
            Alerts.error("Gagal memuat ruangan tersedia: " + e.getMessage());
            System.err.println("Gagal memuat ruangan tersedia: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private static boolean kosong(String nilai) {
        return nilai == null || nilai.isBlank();
    }
}
