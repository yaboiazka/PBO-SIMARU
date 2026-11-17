package com.simaru.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.simaru.model.Admin;
import com.simaru.model.Peminjaman;
import com.simaru.model.Verifikasi;
import com.simaru.util.Alerts;
import com.simaru.util.Status;

/**
 * DAO untuk verifikasi peminjaman. Operasi setujui/tolak mengubah lebih dari
 * satu tabel sehingga dijalankan dalam satu transaksi.
 */
public class VerifikasiDAO implements CrudOperation<Verifikasi> {

    private static final String SELECT_MENUNGGU =
        "SELECT p.id_peminjaman, p.id_pengguna, p.id_ruangan, p.tanggal_pinjam, " +
        "p.jam_mulai, p.jam_selesai, p.keperluan, p.status_verifikasi, p.tanggal_pengajuan, " +
        "u.nama AS nama_pengguna, u.nim_nip, u.role, " +
        "r.nama_ruangan, r.lokasi, r.kapasitas, r.status AS status_ruangan " +
        "FROM peminjaman p " +
        "JOIN pengguna u ON u.id_pengguna = p.id_pengguna " +
        "JOIN ruangan r ON r.id_ruangan = p.id_ruangan " +
        "WHERE p.status_verifikasi = ? " +
        "ORDER BY p.tanggal_pengajuan ASC";

    private static final String SELECT_RIWAYAT =
        "SELECT v.id_verifikasi, v.status_verifikasi, v.catatan, v.tanggal_verifikasi, " +
        "p.id_peminjaman, p.id_pengguna, p.id_ruangan, p.tanggal_pinjam, p.jam_mulai, " +
        "p.jam_selesai, p.keperluan, p.tanggal_pengajuan, " +
        "u.nama AS nama_pengguna, u.nim_nip, u.role, " +
        "r.nama_ruangan, r.lokasi, r.kapasitas, r.status AS status_ruangan, " +
        "a.id_pengguna AS id_admin, a.nama AS nama_admin " +
        "FROM verifikasi v " +
        "JOIN peminjaman p ON p.id_peminjaman = v.id_peminjaman " +
        "JOIN pengguna u ON u.id_pengguna = p.id_pengguna " +
        "JOIN ruangan r ON r.id_ruangan = p.id_ruangan " +
        "JOIN pengguna a ON a.id_pengguna = v.id_admin ";

    // =====================================================================
    // CRUDOperation<Verifikasi>
    // =====================================================================

    @Override
    public void tambah(Verifikasi data) {
        Connection conn = Database.getConnection();
        if (conn == null) {
            Alerts.error("Tidak dapat terhubung ke database. Pastikan server MySQL berjalan.");
            return;
        }
        String sql = "INSERT INTO verifikasi (id_peminjaman, id_admin, status_verifikasi, catatan) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection koneksi = conn;
                PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setInt(1, data.getPeminjaman().getIdPeminjaman());
            ps.setInt(2, data.getAdmin().getIdPengguna());
            ps.setString(3, data.getStatusVerifikasi());
            ps.setString(4, data.getCatatan());
            ps.executeUpdate();
        } catch (SQLException e) {
            Alerts.error("Gagal menyimpan verifikasi: " + e.getMessage());
            System.err.println("Gagal menyimpan verifikasi: " + e.getMessage());
        }
    }

    @Override
    public void ubah(Verifikasi data) {
        Connection conn = Database.getConnection();
        if (conn == null) {
            Alerts.error("Tidak dapat terhubung ke database. Pastikan server MySQL berjalan.");
            return;
        }
        String sql = "UPDATE verifikasi SET status_verifikasi = ?, catatan = ? WHERE id_verifikasi = ?";
        try (Connection koneksi = conn;
                PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, data.getStatusVerifikasi());
            ps.setString(2, data.getCatatan());
            ps.setInt(3, data.getIdVerifikasi());
            ps.executeUpdate();
        } catch (SQLException e) {
            Alerts.error("Gagal mengubah verifikasi: " + e.getMessage());
            System.err.println("Gagal mengubah verifikasi: " + e.getMessage());
        }
    }

    @Override
    public void hapus(int id) {
        Connection conn = Database.getConnection();
        if (conn == null) {
            Alerts.error("Tidak dapat terhubung ke database. Pastikan server MySQL berjalan.");
            return;
        }
        try (Connection koneksi = conn;
                PreparedStatement ps = koneksi.prepareStatement(
                        "DELETE FROM verifikasi WHERE id_verifikasi = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            Alerts.error("Gagal menghapus verifikasi: " + e.getMessage());
            System.err.println("Gagal menghapus verifikasi: " + e.getMessage());
        }
    }

    @Override
    public List<Verifikasi> tampilkan() {
        return queryRiwayat(SELECT_RIWAYAT + "WHERE v.status_verifikasi IN (?, ?) ORDER BY v.tanggal_verifikasi DESC",
                null, true);
    }

    // =====================================================================
    // Query halaman verifikasi
    // =====================================================================

    public List<Peminjaman> tampilkanMenunggu() {
        List<Peminjaman> hasil = new ArrayList<>();
        Connection conn = Database.getConnection();
        if (conn == null) {
            Alerts.error("Tidak dapat terhubung ke database. Pastikan server MySQL berjalan.");
            return hasil;
        }
        try (Connection koneksi = conn;
                PreparedStatement ps = koneksi.prepareStatement(SELECT_MENUNGGU)) {
            ps.setString(1, Status.PENDING);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hasil.add(mapPeminjaman(rs));
                }
            }
        } catch (SQLException e) {
            Alerts.error("Gagal memuat pengajuan menunggu verifikasi: " + e.getMessage());
            System.err.println("Gagal memuat pengajuan menunggu verifikasi: " + e.getMessage());
        }
        return hasil;
    }

    public List<Verifikasi> tampilkanRiwayat(String status) {
        boolean semua = status == null || status.isBlank() || Status.SEMUA.equalsIgnoreCase(status);
        return queryRiwayat(SELECT_RIWAYAT
                + (semua ? "WHERE v.status_verifikasi IN (?, ?) " : "WHERE v.status_verifikasi = ? ")
                + "ORDER BY v.tanggal_verifikasi DESC", status, semua);
    }

    private List<Verifikasi> queryRiwayat(String sql, String status, boolean semua) {
        List<Verifikasi> hasil = new ArrayList<>();
        Connection conn = Database.getConnection();
        if (conn == null) {
            Alerts.error("Tidak dapat terhubung ke database. Pastikan server MySQL berjalan.");
            return hasil;
        }
        try (Connection koneksi = conn;
                PreparedStatement ps = koneksi.prepareStatement(sql)) {
            if (semua) {
                ps.setString(1, Status.DISETUJUI);
                ps.setString(2, Status.DITOLAK);
            } else {
                ps.setString(1, status);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hasil.add(mapVerifikasi(rs));
                }
            }
        } catch (SQLException e) {
            Alerts.error("Gagal memuat riwayat verifikasi: " + e.getMessage());
            System.err.println("Gagal memuat riwayat verifikasi: " + e.getMessage());
        }
        return hasil;
    }

    private Peminjaman mapPeminjaman(ResultSet rs) throws SQLException {
        int idPeminjaman = rs.getInt("id_peminjaman");
        int idPengguna = rs.getInt("id_pengguna");
        int idRuangan = rs.getInt("id_ruangan");
        String namaPengguna = rs.getString("nama_pengguna");
        String namaRuangan = rs.getString("nama_ruangan");
        LocalDate tanggal = rs.getObject("tanggal_pinjam", LocalDate.class);
        LocalTime jamMulai = rs.getObject("jam_mulai", LocalTime.class);
        LocalTime jamSelesai = rs.getObject("jam_selesai", LocalTime.class);
        String keperluan = rs.getString("keperluan");
        String status = rs.getString("status_verifikasi");

        Peminjaman p = new Peminjaman(idPeminjaman, idPengguna, idRuangan, namaPengguna, namaRuangan,
                tanggal, jamMulai, jamSelesai, keperluan, status, null);
        p.setNomorIdentitas(rs.getString("nim_nip"));
        p.setPeran(rs.getString("role"));
        p.setLokasi(rs.getString("lokasi"));
        p.setKapasitas(rs.getInt("kapasitas"));
        p.setStatusRuangan(rs.getString("status_ruangan"));
        p.setTanggalPengajuan(rs.getObject("tanggal_pengajuan", LocalDateTime.class));
        return p;
    }

    private Verifikasi mapVerifikasi(ResultSet rs) throws SQLException {
        Peminjaman p = mapPeminjaman(rs);
        int idVerifikasi = rs.getInt("id_verifikasi");
        String status = rs.getString("status_verifikasi");
        String catatan = rs.getString("catatan");
        LocalDateTime tanggalVerifikasi = rs.getObject("tanggal_verifikasi", LocalDateTime.class);
        int idAdmin = rs.getInt("id_admin");
        String namaAdmin = rs.getString("nama_admin");

        Admin admin = new Admin(idAdmin, namaAdmin, "", "", "Admin");
        return new Verifikasi(idVerifikasi, p, admin, status, catatan, tanggalVerifikasi);
    }

    // =====================================================================
    // Cek bentrok
    // =====================================================================

    /**
     * Cek bentrok untuk pratinjau di UI. Rentang yang hanya menempel tidak
     * dianggap bentrok.
     */
    public List<Peminjaman> cekBentrok(int idRuangan, LocalDate tanggal,
            LocalTime jamMulai, LocalTime jamSelesai) {
        List<Peminjaman> hasil = new ArrayList<>();
        Connection conn = Database.getConnection();
        if (conn == null) {
            Alerts.error("Tidak dapat terhubung ke database. Pastikan server MySQL berjalan.");
            return hasil;
        }
        try (Connection koneksi = conn) {
            hasil = cekBentrok(koneksi, idRuangan, tanggal, jamMulai, jamSelesai);
        } catch (SQLException e) {
            Alerts.error("Gagal memeriksa bentrok jadwal: " + e.getMessage());
            System.err.println("Gagal memeriksa bentrok jadwal: " + e.getMessage());
        }
        return hasil;
    }

    private List<Peminjaman> cekBentrok(Connection koneksi, int idRuangan, LocalDate tanggal,
            LocalTime jamMulai, LocalTime jamSelesai) throws SQLException {
        List<Peminjaman> hasil = new ArrayList<>();
        String sql = "SELECT j.jam_mulai, j.jam_selesai, p.keperluan, p.id_peminjaman, " +
                "p.id_pengguna, u.nama AS nama_pengguna, r.nama_ruangan " +
                "FROM jadwal j " +
                "JOIN peminjaman p ON p.id_peminjaman = j.id_peminjaman " +
                "JOIN pengguna u ON u.id_pengguna = p.id_pengguna " +
                "JOIN ruangan r ON r.id_ruangan = j.id_ruangan " +
                "WHERE j.id_ruangan = ? AND j.tanggal = ? " +
                "AND j.jam_mulai < ? AND j.jam_selesai > ? " +
                "ORDER BY j.jam_mulai";
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setInt(1, idRuangan);
            ps.setObject(2, tanggal);
            ps.setObject(3, jamSelesai);
            ps.setObject(4, jamMulai);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Peminjaman konflik = new Peminjaman(
                            rs.getInt("id_peminjaman"),
                            rs.getInt("id_pengguna"),
                            idRuangan,
                            rs.getString("nama_pengguna"),
                            rs.getString("nama_ruangan"),
                            tanggal,
                            rs.getObject("jam_mulai", LocalTime.class),
                            rs.getObject("jam_selesai", LocalTime.class),
                            rs.getString("keperluan"),
                            Status.DISETUJUI,
                            null);
                    hasil.add(konflik);
                }
            }
        }
        return hasil;
    }

    // =====================================================================
    // Transaksi setujui / tolak
    // =====================================================================

    /**
     * @return null jika berhasil, atau pesan kesalahan yang ramah untuk UI.
     */
    public String setujui(int idPeminjaman, int idAdmin, String catatan) {
        Connection conn = Database.getConnection();
        if (conn == null) {
            return "Tidak dapat terhubung ke database. Pastikan server MySQL berjalan.";
        }
        boolean autoCommit = true;
        Connection koneksi = conn;
        try {
            autoCommit = koneksi.getAutoCommit();
            koneksi.setAutoCommit(false);

            int idRuangan;
            String sqlPeminjaman = "SELECT id_ruangan FROM peminjaman WHERE id_peminjaman = ?";
            try (PreparedStatement ps = koneksi.prepareStatement(sqlPeminjaman)) {
                ps.setInt(1, idPeminjaman);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        koneksi.rollback();
                        return "Pengajuan tidak ditemukan.";
                    }
                    idRuangan = rs.getInt("id_ruangan");
                }
            }

            // Kunci ruangan agar dua admin tidak menyetujui bersamaan.
            String statusRuangan;
            try (PreparedStatement ps = koneksi.prepareStatement(
                    "SELECT status FROM ruangan WHERE id_ruangan = ? FOR UPDATE")) {
                ps.setInt(1, idRuangan);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        koneksi.rollback();
                        return "Ruangan tidak ditemukan.";
                    }
                    statusRuangan = rs.getString("status");
                }
            }
            if (Status.PERBAIKAN.equalsIgnoreCase(statusRuangan)) {
                koneksi.rollback();
                return "Ruangan sedang dalam perbaikan.";
            }

            String status;
            LocalDate tanggal;
            LocalTime jamMulai;
            LocalTime jamSelesai;
            String sqlCek = "SELECT status_verifikasi, tanggal_pinjam, jam_mulai, jam_selesai "
                    + "FROM peminjaman WHERE id_peminjaman = ? FOR UPDATE";
            try (PreparedStatement ps = koneksi.prepareStatement(sqlCek)) {
                ps.setInt(1, idPeminjaman);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        koneksi.rollback();
                        return "Pengajuan tidak ditemukan.";
                    }
                    status = rs.getString("status_verifikasi");
                    tanggal = rs.getObject("tanggal_pinjam", LocalDate.class);
                    jamMulai = rs.getObject("jam_mulai", LocalTime.class);
                    jamSelesai = rs.getObject("jam_selesai", LocalTime.class);
                }
            }
            if (!Status.PENDING.equalsIgnoreCase(status)) {
                koneksi.rollback();
                return "Pengajuan ini sudah diproses.";
            }
            if (!jamSelesai.isAfter(jamMulai)) {
                koneksi.rollback();
                return "Data jadwal tidak valid: jam selesai harus setelah jam mulai.";
            }

            List<Peminjaman> bentrok = cekBentrok(koneksi, idRuangan, tanggal, jamMulai, jamSelesai);
            if (!bentrok.isEmpty()) {
                Peminjaman b = bentrok.get(0);
                koneksi.rollback();
                return "Bentrok dengan jadwal: " + b.getNamaRuangan() + ", "
                        + b.getJamMulai() + "-" + b.getJamSelesai() + ", " + b.getNamaPengguna() + ".";
            }

            try (PreparedStatement ps = koneksi.prepareStatement(
                    "UPDATE peminjaman SET status_verifikasi = ? WHERE id_peminjaman = ? AND status_verifikasi = ?")) {
                ps.setString(1, Status.DISETUJUI);
                ps.setInt(2, idPeminjaman);
                ps.setString(3, Status.PENDING);
                if (ps.executeUpdate() != 1) {
                    koneksi.rollback();
                    return "Pengajuan ini sudah diproses.";
                }
            }

            try (PreparedStatement ps = koneksi.prepareStatement(
                    "INSERT INTO verifikasi (id_peminjaman, id_admin, status_verifikasi, catatan) "
                            + "VALUES (?, ?, ?, ?)")) {
                ps.setInt(1, idPeminjaman);
                ps.setInt(2, idAdmin);
                ps.setString(3, Status.DISETUJUI);
                ps.setString(4, catatan);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = koneksi.prepareStatement(
                    "INSERT INTO jadwal (id_ruangan, id_peminjaman, tanggal, jam_mulai, jam_selesai) "
                            + "VALUES (?, ?, ?, ?, ?)")) {
                ps.setInt(1, idRuangan);
                ps.setInt(2, idPeminjaman);
                ps.setObject(3, tanggal);
                ps.setObject(4, jamMulai);
                ps.setObject(5, jamSelesai);
                ps.executeUpdate();
            }

            koneksi.commit();
            return null;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Rollback gagal: " + ex.getMessage());
                }
            }
            System.err.println("Gagal menyetujui pengajuan: " + e.getMessage());
            return "Gagal menyetujui pengajuan. Silakan coba lagi.";
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(autoCommit);
                } catch (SQLException e) {
                    System.err.println("Gagal mengembalikan autoCommit: " + e.getMessage());
                }
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Gagal menutup koneksi: " + e.getMessage());
                }
            }
        }
    }

    /**
     * @return null jika berhasil, atau pesan kesalahan yang ramah untuk UI.
     */
    public String tolak(int idPeminjaman, int idAdmin, String catatan) {
        if (catatan == null || catatan.isBlank()) {
            return "Catatan wajib diisi saat menolak pengajuan.";
        }
        Connection conn = Database.getConnection();
        if (conn == null) {
            return "Tidak dapat terhubung ke database. Pastikan server MySQL berjalan.";
        }
        boolean autoCommit = true;
        Connection koneksi = conn;
        try {
            autoCommit = koneksi.getAutoCommit();
            koneksi.setAutoCommit(false);

            String status;
            String sqlCek = "SELECT status_verifikasi FROM peminjaman WHERE id_peminjaman = ? FOR UPDATE";
            try (PreparedStatement ps = koneksi.prepareStatement(sqlCek)) {
                ps.setInt(1, idPeminjaman);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        koneksi.rollback();
                        return "Pengajuan tidak ditemukan.";
                    }
                    status = rs.getString("status_verifikasi");
                }
            }
            if (!Status.PENDING.equalsIgnoreCase(status)) {
                koneksi.rollback();
                return "Pengajuan ini sudah diproses.";
            }

            try (PreparedStatement ps = koneksi.prepareStatement(
                    "UPDATE peminjaman SET status_verifikasi = ? WHERE id_peminjaman = ? AND status_verifikasi = ?")) {
                ps.setString(1, Status.DITOLAK);
                ps.setInt(2, idPeminjaman);
                ps.setString(3, Status.PENDING);
                if (ps.executeUpdate() != 1) {
                    koneksi.rollback();
                    return "Pengajuan ini sudah diproses.";
                }
            }

            try (PreparedStatement ps = koneksi.prepareStatement(
                    "INSERT INTO verifikasi (id_peminjaman, id_admin, status_verifikasi, catatan) "
                            + "VALUES (?, ?, ?, ?)")) {
                ps.setInt(1, idPeminjaman);
                ps.setInt(2, idAdmin);
                ps.setString(3, Status.DITOLAK);
                ps.setString(4, catatan);
                ps.executeUpdate();
            }

            koneksi.commit();
            return null;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Rollback gagal: " + ex.getMessage());
                }
            }
            System.err.println("Gagal menolak pengajuan: " + e.getMessage());
            return "Gagal menolak pengajuan. Silakan coba lagi.";
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(autoCommit);
                } catch (SQLException e) {
                    System.err.println("Gagal mengembalikan autoCommit: " + e.getMessage());
                }
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Gagal menutup koneksi: " + e.getMessage());
                }
            }
        }
    }
}
