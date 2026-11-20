package com.simaru.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.simaru.model.Ruangan;
import com.simaru.util.Alerts;

public class RuanganDAO implements CrudOperation<Ruangan> {

    // MAPPING DATA DATABASE KE MODEL RUANGAN
    private Ruangan mapRuangan(ResultSet rs) throws SQLException {

        Ruangan ruangan = new Ruangan(
                rs.getInt("id_ruangan"),
                rs.getString("nama_ruangan"),
                rs.getInt("kapasitas"),
                rs.getString("fasilitas"),
                rs.getString("lokasi")
        );

        ruangan.setStatus(rs.getString("status"));

        return ruangan;
    }

    // CREATE
    @Override
    public void tambah(Ruangan ruangan) {
        tambahRuangan(ruangan);
    }

    public boolean tambahRuangan(Ruangan ruangan) {

        String sql = "INSERT INTO ruangan "
                + "(nama_ruangan, lokasi, kapasitas, fasilitas, status) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection()) {

            if (conn == null) {
                Alerts.error("Koneksi database gagal!");
                return false;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, ruangan.getNamaRuangan());
                pstmt.setString(2, ruangan.getLokasi());
                pstmt.setInt(3, ruangan.getKapasitas());
                pstmt.setString(4, ruangan.getFasilitas());
                pstmt.setString(5, ruangan.getStatus());

                return pstmt.executeUpdate() > 0;
            }

        } catch (SQLException e) {

            Alerts.error("Gagal menambahkan ruangan: " + e.getMessage());
            System.err.println(e.getMessage());

            return false;
        }
    }

    // UPDATE

    @Override
    public void ubah(Ruangan ruangan) {
        ubahRuangan(ruangan);
    }

    public boolean ubahRuangan(Ruangan ruangan) {

        String sql = "UPDATE ruangan SET "
                + "nama_ruangan = ?, "
                + "lokasi = ?, "
                + "kapasitas = ?, "
                + "fasilitas = ?, "
                + "status = ? "
                + "WHERE id_ruangan = ?";

        try (Connection conn = Database.getConnection()) {

            if (conn == null) {
                Alerts.error("Koneksi database gagal!");
                return false;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, ruangan.getNamaRuangan());
                pstmt.setString(2, ruangan.getLokasi());
                pstmt.setInt(3, ruangan.getKapasitas());
                pstmt.setString(4, ruangan.getFasilitas());
                pstmt.setString(5, ruangan.getStatus());
                pstmt.setInt(6, ruangan.getIdRuangan());

                return pstmt.executeUpdate() > 0;
            }

        } catch (SQLException e) {

            Alerts.error("Gagal memperbarui ruangan: " + e.getMessage());
            System.err.println(e.getMessage());

            return false;
        }
    }


    // DELETE
    @Override
    public void hapus(int id_ruangan) {
        hapusRuangan(id_ruangan);
    }

    public boolean hapusRuangan(int id_ruangan) {

        String sql = "DELETE FROM ruangan WHERE id_ruangan = ?";

        try (Connection conn = Database.getConnection()) {

            if (conn == null) {
                Alerts.error("Koneksi database gagal!");
                return false;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, id_ruangan);

                return pstmt.executeUpdate() > 0;
            }

        } catch (SQLException e) {

            if (e.getErrorCode() == 1451) {

                Alerts.error(
                        "Ruangan tidak dapat dihapus karena masih "
                        + "digunakan dalam peminjaman atau jadwal."
                );

            } else {

                Alerts.error(
                        "Gagal menghapus ruangan: " + e.getMessage()
                );
            }

            System.err.println(e.getMessage());

            return false;
        }
    }


    // READ: MENAMPILKAN SELURUH RUANGAN
    @Override
    public List<Ruangan> tampilkan() {

        String sql = "SELECT * FROM ruangan ORDER BY id_ruangan";

        List<Ruangan> hasil = new ArrayList<>();

        try (Connection conn = Database.getConnection()) {

            if (conn == null) {
                Alerts.error("Koneksi database gagal!");
                return hasil;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    hasil.add(mapRuangan(rs));
                }
            }

        } catch (SQLException e) {

            Alerts.error("Gagal menampilkan ruangan: " + e.getMessage());
            System.err.println(e.getMessage());
        }

        return hasil;
    }

    // SEARCH: MENCARI RUANGAN BERDASARKAN ID
    public Ruangan cari(int id_ruangan) {

        String sql = "SELECT * FROM ruangan WHERE id_ruangan = ?";

        try (Connection conn = Database.getConnection()) {

            if (conn == null) {
                Alerts.error("Koneksi database gagal!");
                return null;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, id_ruangan);

                try (ResultSet rs = pstmt.executeQuery()) {

                    if (rs.next()) {
                        return mapRuangan(rs);
                    }
                }
            }

        } catch (SQLException e) {

            Alerts.error("Gagal mencari ruangan: " + e.getMessage());
            System.err.println(e.getMessage());
        }

        return null;
    }

    // FILTER: BERDASARKAN STATUS
    public List<Ruangan> filter(String status) throws SQLException {

        if (status == null
                || status.isBlank()
                || status.equalsIgnoreCase("Semua")) {

            return tampilkan();
        }

        String sql = "SELECT * FROM ruangan "
                + "WHERE status = ? ORDER BY nama_ruangan";

        List<Ruangan> hasil = new ArrayList<>();

        try (Connection conn = Database.getConnection()) {

            if (conn == null) {
                throw new SQLException("Koneksi database gagal.");
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, status);

                try (ResultSet rs = pstmt.executeQuery()) {

                    while (rs.next()) {
                        hasil.add(mapRuangan(rs));
                    }
                }
            }
        }

        return hasil;
    }
}
