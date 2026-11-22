package com.simaru.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.simaru.model.Jadwal;
import com.simaru.util.Alerts;

public class JadwalDAO implements CrudOperation<Jadwal>{

    @Override
    public void tambah(Jadwal data) {
        Connection conn = Database.getConnection();
        String query = "INSERT INTO jadwal (id_ruangan, id_peminjaman, tanggal, jam_mulai, jam_selesai) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, data.getIdJadwal());
            pstmt.setInt(2, data.getIdPeminjaman());
            pstmt.setObject(3, data.getTanggal());
            pstmt.setObject(4, data.getJamMulai());
            pstmt.setObject(5, data.getJamSelesai());
            int status = pstmt.executeUpdate();
            if (status != 0) {
                Alerts.info("Penambahan jadwal berhasil");
            }
        } catch (SQLException e) {
            Alerts.error("Error during requesting Peminjaman: " + e.getMessage());
        }
        // throw new UnsupportedOperationException("Unimplemented method 'tambah'");
    }

    @Override
    public void ubah(Jadwal jadwal) {
        Connection conn = Database.getConnection();
        String query = "UPDATE jadwal id_ruangan = ?, id_peminjaman = ?, tanggal = ?, jam_mulai = ?, jam_selesai = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, jadwal.getIdJadwal());
            pstmt.setInt(2, jadwal.getIdPeminjaman());
            pstmt.setObject(3, jadwal.getTanggal());
            pstmt.setObject(4, jadwal.getJamMulai());
            pstmt.setObject(5, jadwal.getJamSelesai());
            int status = pstmt.executeUpdate();
            if (status != 0) {
                Alerts.info("Penambahan jadwal berhasil");
            }
        } catch (SQLException e) {
            Alerts.error("Error during requesting Peminjaman: " + e.getMessage());
        }
        throw new UnsupportedOperationException("Unimplemented method 'ubah'");
    }

    @Override
    public void hapus(int id_jadwal) {
        Connection conn = Database.getConnection();
        try {
            String query = "DELETE FROM jadwal WHERE id_jadwal = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id_jadwal);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Alerts.error("Error during deleting Peminjaman: " + e.getMessage());
            System.err.println("Error during deleting Peminjaman: " + e.getMessage());
        }
        // throw new UnsupportedOperationException("Unimplemented method 'hapus'");
    }

    @Override
    public List<Jadwal> tampilkan() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'tampilkan'");
    }

}
