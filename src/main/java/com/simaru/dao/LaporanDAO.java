package com.simaru.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.simaru.model.Peminjaman;
import com.simaru.model.RingkasanRuangan;
import com.simaru.util.Alerts;
import com.simaru.util.Status;

/**
 * DAO baca-saja untuk laporan penggunaan ruangan.
 */
public class LaporanDAO {

    public List<RingkasanRuangan> ringkasan(LocalDate dari, LocalDate sampai, Integer idRuangan) {
        List<RingkasanRuangan> hasil = new ArrayList<>();
        Connection conn = Database.getConnection();
        if (conn == null) {
            Alerts.error("Tidak dapat terhubung ke database. Pastikan server MySQL berjalan.");
            return hasil;
        }

        StringBuilder sql = new StringBuilder(
                "SELECT r.id_ruangan, r.nama_ruangan, COUNT(*) AS total_pengajuan, " +
                "SUM(CASE WHEN p.status_verifikasi = ? THEN 1 ELSE 0 END) AS disetujui, " +
                "SUM(CASE WHEN p.status_verifikasi = ? THEN 1 ELSE 0 END) AS ditolak, " +
                "SUM(CASE WHEN p.status_verifikasi = ? THEN 1 ELSE 0 END) AS pending, " +
                "ROUND(SUM(CASE WHEN p.status_verifikasi = ? " +
                " THEN TIME_TO_SEC(TIMEDIFF(p.jam_selesai, p.jam_mulai)) ELSE 0 END) / 3600, 2) " +
                " AS total_jam_terpakai " +
                "FROM peminjaman p " +
                "JOIN ruangan r ON r.id_ruangan = p.id_ruangan " +
                "WHERE p.tanggal_pinjam BETWEEN ? AND ? ");
        if (idRuangan != null) {
            sql.append("AND r.id_ruangan = ? ");
        }
        sql.append("GROUP BY r.id_ruangan, r.nama_ruangan ");
        sql.append("ORDER BY total_jam_terpakai DESC, r.nama_ruangan");

        try (Connection koneksi = conn;
                PreparedStatement ps = koneksi.prepareStatement(sql.toString())) {
            ps.setString(1, Status.DISETUJUI);
            ps.setString(2, Status.DITOLAK);
            ps.setString(3, Status.PENDING);
            ps.setString(4, Status.DISETUJUI);
            ps.setObject(5, dari);
            ps.setObject(6, sampai);
            if (idRuangan != null) {
                ps.setInt(7, idRuangan);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hasil.add(new RingkasanRuangan(
                            rs.getInt("id_ruangan"),
                            rs.getString("nama_ruangan"),
                            rs.getInt("total_pengajuan"),
                            rs.getInt("disetujui"),
                            rs.getInt("ditolak"),
                            rs.getInt("pending"),
                            rs.getDouble("total_jam_terpakai")));
                }
            }
        } catch (SQLException e) {
            Alerts.error("Gagal memuat ringkasan laporan: " + e.getMessage());
            System.err.println("Gagal memuat ringkasan laporan: " + e.getMessage());
        }
        return hasil;
    }

    public List<Peminjaman> rincian(LocalDate dari, LocalDate sampai, Integer idRuangan, String status) {
        List<Peminjaman> hasil = new ArrayList<>();
        Connection conn = Database.getConnection();
        if (conn == null) {
            Alerts.error("Tidak dapat terhubung ke database. Pastikan server MySQL berjalan.");
            return hasil;
        }

        StringBuilder sql = new StringBuilder(
                "SELECT p.id_peminjaman, p.id_pengguna, p.id_ruangan, r.nama_ruangan, " +
                "u.nama AS nama_pengguna, u.role, u.nim_nip, " +
                "p.tanggal_pinjam, p.jam_mulai, p.jam_selesai, p.keperluan, p.status_verifikasi " +
                "FROM peminjaman p " +
                "JOIN ruangan r ON r.id_ruangan = p.id_ruangan " +
                "JOIN pengguna u ON u.id_pengguna = p.id_pengguna " +
                "WHERE p.tanggal_pinjam BETWEEN ? AND ? ");
        boolean pakaiRuangan = idRuangan != null;
        boolean pakaiStatus = status != null && !status.isBlank() && !Status.SEMUA.equalsIgnoreCase(status);
        if (pakaiRuangan) {
            sql.append("AND p.id_ruangan = ? ");
        }
        if (pakaiStatus) {
            sql.append("AND p.status_verifikasi = ? ");
        }
        sql.append("ORDER BY p.tanggal_pinjam, p.jam_mulai");

        try (Connection koneksi = conn;
                PreparedStatement ps = koneksi.prepareStatement(sql.toString())) {
            int index = 1;
            ps.setObject(index++, dari);
            ps.setObject(index++, sampai);
            if (pakaiRuangan) {
                ps.setInt(index++, idRuangan);
            }
            if (pakaiStatus) {
                ps.setString(index++, status);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idPeminjaman = rs.getInt("id_peminjaman");
                    int idPengguna = rs.getInt("id_pengguna");
                    int idRuang = rs.getInt("id_ruangan");
                    String namaPengguna = rs.getString("nama_pengguna");
                    String namaRuangan = rs.getString("nama_ruangan");
                    LocalDate tanggal = rs.getObject("tanggal_pinjam", LocalDate.class);
                    LocalTime jamMulai = rs.getObject("jam_mulai", LocalTime.class);
                    LocalTime jamSelesai = rs.getObject("jam_selesai", LocalTime.class);
                    String keperluan = rs.getString("keperluan");
                    String statusVerifikasi = rs.getString("status_verifikasi");

                    Peminjaman p = new Peminjaman(idPeminjaman, idPengguna, idRuang, namaPengguna,
                            namaRuangan, tanggal, jamMulai, jamSelesai, keperluan, statusVerifikasi, null);
                    p.setPeran(rs.getString("role"));
                    p.setNomorIdentitas(rs.getString("nim_nip"));
                    hasil.add(p);
                }
            }
        } catch (SQLException e) {
            Alerts.error("Gagal memuat rincian laporan: " + e.getMessage());
            System.err.println("Gagal memuat rincian laporan: " + e.getMessage());
        }
        return hasil;
    }
}
