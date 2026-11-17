package com.simaru.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.simaru.model.ProgramStudi;
import com.simaru.util.Alerts;

/**
 * DAO baca-saja untuk daftar program studi (dipakai form register dan kelola
 * pengguna).
 */
public class ProgramStudiDAO {

    public List<ProgramStudi> tampilkan() {
        List<ProgramStudi> hasil = new ArrayList<>();
        Connection conn = Database.getConnection();
        if (conn == null) {
            Alerts.error("Tidak dapat terhubung ke database. Pastikan server MySQL berjalan.");
            return hasil;
        }
        String sql = "SELECT id_prodi, fakultas, program_studi FROM program_studi "
                + "ORDER BY program_studi";
        try (Connection koneksi = conn;
                PreparedStatement ps = koneksi.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                hasil.add(new ProgramStudi(
                        rs.getInt("id_prodi"),
                        rs.getString("fakultas"),
                        rs.getString("program_studi")));
            }
        } catch (SQLException e) {
            Alerts.error("Gagal memuat program studi: " + e.getMessage());
            System.err.println("Gagal memuat program studi: " + e.getMessage());
        }
        return hasil;
    }
}
