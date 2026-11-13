package com.simaru.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.simaru.model.Peminjaman;
import com.simaru.model.Ruangan;
import com.simaru.util.Alerts;
import com.simaru.util.Session;

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
        String query = "INSERT INTO peminjaman (id_pengguna, id_ruangan, tanggal_pinjam, jam_mulai, jam_selesai, keperluan) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, peminjaman.getIdPengguna());
            pstmt.setInt(2, peminjaman.getIdRuangan());
            pstmt.setObject(3, peminjaman.getTanggal());
            pstmt.setObject(4, peminjaman.getJamMulai());
            pstmt.setObject(5, peminjaman.getJamSelesai());
            pstmt.setString(6, peminjaman.getKeperluan());
            int status = pstmt.executeUpdate();
            if (status != 0) {
                Alerts.info("Peminjaman berhasil, harap tunggu verifikasi admin");
            }
        } catch (SQLException e) {
            Alerts.error("Error during requesting Peminjaman: " + e.getMessage());
        }
    }

    @Override
    public void ubah(Peminjaman data) {
        Connection conn = Database.getConnection();
        String query = "UPDATE peminjaman SET id_pengguna = ?, id_ruangan = ?, tanggal_pinjam = ?, jam_mulai = ?, jam_selesai = ?, keperluan = ?, status_verifikasi = ?, tanggal_pengajuan = ? WHERE id_peminjaman = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, data.getIdPengguna());
            pstmt.setInt(2, data.getIdRuangan());
            pstmt.setObject(3, data.getTanggal());
            pstmt.setObject(4, data.getJamMulai());
            pstmt.setObject(5, data.getJamSelesai());
            pstmt.setString(6, data.getKeperluan());
            pstmt.setString(7, data.getStatus());
            pstmt.setObject(8, data.getTanggalPengajuan());
            pstmt.setInt(9, data.getIdPeminjaman());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Alerts.error("Error during updating Peminjaman: " + e.getMessage());
            System.err.println("Error during updating Peminjaman: " + e.getMessage());
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

        Peminjaman p = new Peminjaman(idPengguna, idRuangan, namaPengguna, namaRuangan, tanggal, jamMulai, jamSelesai, keperluan);
        p.setIdPeminjaman(idPeminjaman);
        p.setCatatan(catatan);
        p.setStatus(status);
        return p;
    }

    public void preTambah(Ruangan ruangTerpilih, LocalDate tanggal, LocalTime jamMulai, LocalTime jamSelesai, String Keperluan){
        
        int id_peminjam = (Session.getCurrent()).getIdPengguna();
        int id_ruangan = ruangTerpilih.getIdRuangan();
        String nama_peminjam = (Session.getCurrent()).getNama();
        String nama_ruang_dipinjam = ruangTerpilih.getNamaRuangan();
        LocalDate tanggal_dipinjam = tanggal;
        LocalTime jam_mulai = jamMulai;
        LocalTime jam_selesai = jamSelesai;
        String keperluan = Keperluan;
        Peminjaman p = new Peminjaman(id_peminjam, id_ruangan, nama_peminjam, nama_ruang_dipinjam, tanggal_dipinjam, jam_mulai, jam_selesai, keperluan);

        tambah(p);
    }
}
