package com.simaru.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.simaru.model.Peminjaman;
import com.simaru.model.Ruangan;
import com.simaru.util.Alerts;
import com.simaru.util.Session;
import com.simaru.util.Status;

public class PeminjamanDAO implements CrudOperation<Peminjaman>{
    private static final String SELECT_PEMINJAMAN =
        "SELECT p.id_peminjaman, p.id_pengguna, p.id_ruangan, u.nama AS nama_pengguna, r.nama_ruangan, "+
        "p.tanggal_pinjam AS tanggal, p.jam_mulai, p.jam_selesai, p.keperluan, "+
        "p.status_verifikasi AS status, "+
        "(SELECT v.catatan FROM verifikasi v WHERE v.id_peminjaman = p.id_peminjaman "+
        "ORDER BY v.id_verifikasi DESC LIMIT 1) AS catatan "+
        "FROM peminjaman p "+
        "JOIN pengguna u ON u.id_pengguna = p.id_pengguna "+
        "JOIN ruangan r ON r.id_ruangan = p.id_ruangan ";

    private static final String SELECT_JADWAL =
        "SELECT p.id_peminjaman, p.id_pengguna, p.id_ruangan, u.nama AS nama_pengguna, r.nama_ruangan, "+
        "j.tanggal AS tanggal, j.jam_mulai, j.jam_selesai, p.keperluan, "+
        "p.status_verifikasi AS status, NULL AS catatan "+
        "FROM jadwal j "+
        "JOIN peminjaman p ON p.id_peminjaman = j.id_peminjaman "+
        "JOIN pengguna u ON u.id_pengguna = p.id_pengguna "+
        "JOIN ruangan r ON r.id_ruangan = j.id_ruangan ";
    
    @Override 
    public void tambah(Peminjaman peminjaman){
        Connection conn = Database.getConnection();
        String query = "INSERT INTO peminjaman (id_pengguna, id_ruangan, tanggal_pinjam, jam_mulai, jam_selesai, keperluan, status_verifikasi) VALUES (?,?,?,?,?,?, 'Pending')";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Alerts.error("Error during adding user: " + e.getMessage());
        }
    }

    @Override
    public void ubah(Peminjaman data) {
        Connection conn = Database.getConnection();
        String query = "UPDATE peminjaman SET id_pengguna = ?, id_ruanngan = ?, tanggal_pinjam = ?, jam_mulai = ?, jam_selesai = ?, keperluan = ?, status_verifikasi = ?, tanggal_pengajuan = ? WHERE id_peminjaman = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, data.getIdPengguna());
            pstmt.setInt(2, data.getIdRuangan());
            pstmt.setObject(3, data.getTanggal());
            pstmt.setObject(4, data.getJamMulai());
            pstmt.setObject(5, data.getJamSelesai());
            pstmt.setString(6, data.getKeperluan());
            pstmt.setString(7, data.getStatus());
            pstmt.setString(8, data.getKeperluan());
            pstmt.setInt(9, data.getIdPeminjaman());
            // tolong masukin getter nya
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Alerts.error("Error during updating user: " + e.getMessage());
            System.err.println("Error during updating user: " + e.getMessage());
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void hapus(int id_peminjaman) {
        Connection conn = Database.getConnection();
        try {
            String query = "DELETE FROM peminjaman WHERE id_peminjaman = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id_peminjaman);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Alerts.error("Error during deleting Peminjaman: " + e.getMessage());
            System.err.println("Error during deleting Peminjaman: " + e.getMessage());
        }
        
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Peminjaman> tampilkan() {
        return query(SELECT_PEMINJAMAN + "ORDER BY p.tanggal_pinjam DESC, p.jam_mulai", null, null);
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Peminjaman> jadwal(LocalDate tanggal){
        if (tanggal != null){
            return query(SELECT_JADWAL + "WHERE j.tanggal = ? ORDER BY j.jam_mulai", null,tanggal);
        }
        return query(SELECT_JADWAL + "ORDER BY j.tanggal, j.jam_mulai", null, null);
    }

    private List<Peminjaman> query(String sql, Integer intParam, LocalDate dateParam) {
        Connection conn = Database.getConnection();
        List<Peminjaman> hasil = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (intParam != null) {
                ps.setInt(1, intParam);
            }
            if (dateParam != null) {
                ps.setDate(1, Date.valueOf(dateParam));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Peminjaman pinjam = map(rs);
                    hasil.add(pinjam);
                }
            }
        } catch (SQLException e) {
            Alerts.error("Error during adding user: " + e.getMessage());
        }
        return hasil;
    }

    private Peminjaman map(ResultSet rs) throws SQLException {
        int idPeminjaman = rs.getInt("id_peminjaman");
        int idPengguna = rs.getInt("id_pengguna");
        int idRuangan = rs.getInt("id_ruangan");

        String namaPengguna = rs.getString("nama_pengguna");
        String namaRuangan = rs.getString("nama_ruangan");

        LocalDate tanggal = rs.getObject("tanggal", LocalDate.class);
        LocalTime jamMulai = rs.getObject("jam_mulai", LocalTime.class);
        LocalTime jamSelesai = rs.getObject("jam_selesai", LocalTime.class);

        String keperluan = rs.getString("keperluan");
        String status = rs.getString("status");
        String catatan = rs.getString("catatan");

        Peminjaman p = new Peminjaman(idPeminjaman, idPengguna, idRuangan, namaPengguna, namaRuangan, tanggal, jamMulai, jamSelesai, keperluan, status, catatan);
        return p;
    }

    // =====================================================================
    // Tambahan untuk halaman Status Peminjaman dan dipakai modul Ajukan.
    // Append-only: tidak mengubah method yang sudah ada.
    // =====================================================================

    private static final String SELECT_MILIK =
        "SELECT p.id_peminjaman, p.id_pengguna, p.id_ruangan, u.nama AS nama_pengguna, r.nama_ruangan, " +
        "p.tanggal_pinjam AS tanggal, p.jam_mulai, p.jam_selesai, p.keperluan, " +
        "p.status_verifikasi AS status, p.tanggal_pengajuan, " +
        "r.lokasi, r.kapasitas, r.status AS status_ruangan, " +
        "(SELECT v.catatan FROM verifikasi v WHERE v.id_peminjaman = p.id_peminjaman " +
        " ORDER BY v.id_verifikasi DESC LIMIT 1) AS catatan, " +
        "(SELECT a.nama FROM verifikasi v JOIN pengguna a ON a.id_pengguna = v.id_admin " +
        " WHERE v.id_peminjaman = p.id_peminjaman ORDER BY v.id_verifikasi DESC LIMIT 1) AS nama_admin, " +
        "(SELECT v.tanggal_verifikasi FROM verifikasi v WHERE v.id_peminjaman = p.id_peminjaman " +
        " ORDER BY v.id_verifikasi DESC LIMIT 1) AS tanggal_verifikasi " +
        "FROM peminjaman p " +
        "JOIN pengguna u ON u.id_pengguna = p.id_pengguna " +
        "JOIN ruangan r ON r.id_ruangan = p.id_ruangan " +
        "WHERE p.id_pengguna = ? ";

    /**
     * Overloading: seluruh pengajuan milik satu pengguna.
     */
    public List<Peminjaman> tampilkanMilik(int idPengguna) {
        return queryMilik(SELECT_MILIK + "ORDER BY p.tanggal_pengajuan DESC", idPengguna, null);
    }

    /**
     * Overloading: pengajuan milik satu pengguna dengan filter status.
     */
    public List<Peminjaman> tampilkanMilik(int idPengguna, String status) {
        if (status == null || status.isBlank() || Status.SEMUA.equalsIgnoreCase(status)) {
            return tampilkanMilik(idPengguna);
        }
        return queryMilik(SELECT_MILIK + "AND p.status_verifikasi = ? ORDER BY p.tanggal_pengajuan DESC",
                idPengguna, status);
    }

    private List<Peminjaman> queryMilik(String sql, int idPengguna, String status) {
        List<Peminjaman> hasil = new ArrayList<>();
        Connection conn = Database.getConnection();
        if (conn == null) {
            Alerts.error("Tidak dapat terhubung ke database. Pastikan server MySQL berjalan.");
            return hasil;
        }
        try (Connection koneksi = conn;
                PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setInt(1, idPengguna);
            if (status != null) {
                ps.setString(2, status);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hasil.add(mapLengkap(rs));
                }
            }
        } catch (SQLException e) {
            Alerts.error("Gagal memuat status peminjaman: " + e.getMessage());
            System.err.println("Gagal memuat status peminjaman: " + e.getMessage());
        }
        return hasil;
    }

    private Peminjaman mapLengkap(ResultSet rs) throws SQLException {
        int idPeminjaman = rs.getInt("id_peminjaman");
        int idPengguna = rs.getInt("id_pengguna");
        int idRuangan = rs.getInt("id_ruangan");
        String namaPengguna = rs.getString("nama_pengguna");
        String namaRuangan = rs.getString("nama_ruangan");
        LocalDate tanggal = rs.getObject("tanggal", LocalDate.class);
        LocalTime jamMulai = rs.getObject("jam_mulai", LocalTime.class);
        LocalTime jamSelesai = rs.getObject("jam_selesai", LocalTime.class);
        String keperluan = rs.getString("keperluan");
        String status = rs.getString("status");
        String catatan = rs.getString("catatan");

        Peminjaman p = new Peminjaman(idPeminjaman, idPengguna, idRuangan, namaPengguna, namaRuangan,
                tanggal, jamMulai, jamSelesai, keperluan, status, catatan);
        p.setLokasi(rs.getString("lokasi"));
        p.setKapasitas(rs.getInt("kapasitas"));
        p.setStatusRuangan(rs.getString("status_ruangan"));
        p.setTanggalPengajuan(rs.getObject("tanggal_pengajuan", LocalDateTime.class));
        p.setNamaAdmin(rs.getString("nama_admin"));
        p.setTanggalVerifikasi(rs.getObject("tanggal_verifikasi", LocalDateTime.class));
        return p;
    }

    /**
     * Membatalkan pengajuan. Hanya baris milik pengguna dan berstatus Pending
     * yang akan terhapus. Mengembalikan jumlah baris terdampak.
     */
    public int batalkan(int idPeminjaman, int idPengguna) {
        Connection conn = Database.getConnection();
        if (conn == null) {
            Alerts.error("Tidak dapat terhubung ke database. Pastikan server MySQL berjalan.");
            return 0;
        }
        String sql = "DELETE FROM peminjaman WHERE id_peminjaman = ? AND id_pengguna = ? AND status_verifikasi = ?";
        try (Connection koneksi = conn;
                PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setInt(1, idPeminjaman);
            ps.setInt(2, idPengguna);
            ps.setString(3, Status.PENDING);
            return ps.executeUpdate();
        } catch (SQLException e) {
            Alerts.error("Gagal membatalkan pengajuan: " + e.getMessage());
            System.err.println("Gagal membatalkan pengajuan: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Dipakai form Ajukan Peminjaman (modul anggota lain) untuk menyimpan
     * pengajuan baru atas nama pengguna yang sedang login.
     */
    public void preTambah(Ruangan ruangan, LocalDate tanggal, LocalTime jamMulai,
            LocalTime jamSelesai, String keperluan) {
        if (Session.getCurrent() == null) {
            Alerts.error("Sesi pengguna tidak ditemukan. Silakan login kembali.");
            return;
        }
        if (ruangan == null || tanggal == null || jamMulai == null || jamSelesai == null
                || keperluan == null || keperluan.isBlank()) {
            Alerts.warning("Masih ada data yang belum lengkap.");
            return;
        }
        if (!jamSelesai.isAfter(jamMulai)) {
            Alerts.error("Jam selesai harus setelah jam mulai.");
            return;
        }
        Connection conn = Database.getConnection();
        if (conn == null) {
            Alerts.error("Tidak dapat terhubung ke database. Pastikan server MySQL berjalan.");
            return;
        }
        String sql = "INSERT INTO peminjaman (id_pengguna, id_ruangan, tanggal_pinjam, jam_mulai, "
                + "jam_selesai, keperluan, status_verifikasi) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection koneksi = conn;
                PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setInt(1, Session.getCurrent().getIdPengguna());
            ps.setInt(2, ruangan.getIdRuangan());
            ps.setObject(3, tanggal);
            ps.setObject(4, jamMulai);
            ps.setObject(5, jamSelesai);
            ps.setString(6, keperluan);
            ps.setString(7, Status.PENDING);
            int baris = ps.executeUpdate();
            if (baris > 0) {
                Alerts.info("Pengajuan peminjaman berhasil dikirim dan menunggu verifikasi admin.");
            }
        } catch (SQLException e) {
            Alerts.error("Gagal mengajukan peminjaman: " + e.getMessage());
            System.err.println("Gagal mengajukan peminjaman: " + e.getMessage());
        }
    }

    /**
     * Menyimpan pengajuan baru dan melaporkan keberhasilannya ke pemanggil.
     * Append-only: melengkapi {@link #preTambah} tanpa mengubah kontraknya.
     */
    public boolean ajukan(Ruangan ruangan, LocalDate tanggal, LocalTime jamMulai,
            LocalTime jamSelesai, String keperluan) {
        if (Session.getCurrent() == null) {
            Alerts.error("Sesi pengguna tidak ditemukan. Silakan login kembali.");
            return false;
        }
        if (ruangan == null || tanggal == null || jamMulai == null || jamSelesai == null
                || keperluan == null || keperluan.isBlank()) {
            Alerts.warning("Masih ada data yang belum lengkap.");
            return false;
        }
        if (!jamSelesai.isAfter(jamMulai)) {
            Alerts.error("Jam selesai harus setelah jam mulai.");
            return false;
        }
        if (!Status.TERSEDIA.equalsIgnoreCase(ruangan.getStatus())) {
            Alerts.error("Ruangan tidak tersedia untuk diajukan.");
            return false;
        }
        Connection conn = Database.getConnection();
        if (conn == null) {
            Alerts.error("Tidak dapat terhubung ke database. Pastikan server MySQL berjalan.");
            return false;
        }
        String sql = "INSERT INTO peminjaman (id_pengguna, id_ruangan, tanggal_pinjam, jam_mulai, "
                + "jam_selesai, keperluan, status_verifikasi) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection koneksi = conn;
                PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setInt(1, Session.getCurrent().getIdPengguna());
            ps.setInt(2, ruangan.getIdRuangan());
            ps.setObject(3, tanggal);
            ps.setObject(4, jamMulai);
            ps.setObject(5, jamSelesai);
            ps.setString(6, keperluan.trim());
            ps.setString(7, Status.PENDING);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Alerts.error("Gagal mengajukan peminjaman: " + e.getMessage());
            System.err.println("Gagal mengajukan peminjaman: " + e.getMessage());
            return false;
        }
    }
}
