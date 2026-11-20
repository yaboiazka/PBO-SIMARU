package com.simaru.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.simaru.model.Admin;
import com.simaru.model.Dosen;
import com.simaru.model.Mahasiswa;
import com.simaru.model.Pengguna;
import com.simaru.util.Hash;
import com.simaru.util.Session;
import com.simaru.util.Alerts;

public class KelolaPenggunaDAO {

    // MENAMPILKAN SELURUH PENGGUNA
    public List<Pengguna> tampilkan() {

        List<Pengguna> daftar = new ArrayList<>();

        String sql = "SELECT p.*, ps.fakultas, ps.program_studi "
                + "FROM pengguna p "
                + "LEFT JOIN program_studi ps "
                + "ON p.id_prodi = ps.id_prodi "
                + "ORDER BY p.id_pengguna ASC";

        try (Connection conn = Database.getConnection()) {

            if (conn == null) return daftar;

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    int id = rs.getInt("id_pengguna");
                    String nama = rs.getString("nama");
                    String email = rs.getString("email");
                    String password = rs.getString("password");
                    String role = rs.getString("role");
                    String nimNip = rs.getString("nim_nip");

                    int idValue = rs.getInt("id_prodi");
                    Integer idProdi = rs.wasNull() ? null : idValue;

                    String fakultas = rs.getString("fakultas");
                    String prodi = rs.getString("program_studi");

                    Pengguna pengguna;

                    switch (role) {

                        case "ADMIN":
                            pengguna = new Admin(
                                    id, nama, email, password, role
                            );
                            break;

                        case "DOSEN":
                            pengguna = new Dosen(
                                    id, nama, email, password,
                                    nimNip, fakultas
                            );
                            break;

                        case "MAHASISWA":
                            pengguna = new Mahasiswa(
                                    id, nama, email, password,
                                    nimNip, prodi
                            );
                            break;

                        default:
                            continue;
                    }

                    pengguna.setNimNip(nimNip);
                    pengguna.setIdProdi(idProdi);

                    String informasi = "-";

                    if (fakultas != null && prodi != null) {
                        informasi = fakultas + " / " + prodi;
                    }

                    pengguna.setInformasiProdi(informasi);

                    daftar.add(pengguna);
                }
            }

        } catch (SQLException e) {
            System.err.println("Gagal mengambil pengguna: " + e.getMessage());
            Alerts.error("Gagal mengambil data pengguna.");
        }

        return daftar;
    }

    // MENGAMBIL DAFTAR PROGRAM STUDI
    public Map<Integer, String> tampilkanProgramStudi() {

        Map<Integer, String> daftar = new LinkedHashMap<>();

        String sql = "SELECT id_prodi, fakultas, program_studi "
                + "FROM program_studi ORDER BY fakultas, program_studi";

        try (Connection conn = Database.getConnection()) {

            if (conn == null) return daftar;

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    String nama = rs.getString("fakultas")
                            + " / " + rs.getString("program_studi");

                    daftar.put(rs.getInt("id_prodi"), nama);
                }
            }

        } catch (SQLException e) {
            Alerts.error("Gagal mengambil program studi.");
            System.err.println(e.getMessage());
        }

        return daftar;
    }

    // MENAMBAHKAN PENGGUNA
    public boolean tambah(
            String nama,
            String nimNip,
            String email,
            String password,
            String role,
            Integer idProdi) {

        if (!validasi(nama, nimNip, email, role)
                || password == null || password.length() < 8) {
            Alerts.warning("Periksa data pengguna dan password minimal 8 karakter.");
            return false;
        }

        if ("ADMIN".equals(role)) idProdi = null;

        String sql = "INSERT INTO pengguna "
                + "(nama, nim_nip, email, password, role, id_prodi) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection()) {

            if (conn == null) return false;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, nama.trim());
                ps.setString(2, nimNip.trim());
                ps.setString(3, email.trim());
                ps.setString(4, hashPassword(password));
                ps.setString(5, role);

                setProdi(ps, 6, idProdi);

                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            tampilkanError(e);
            return false;
        }
    }

    // MENGEDIT PENGGUNA
    public boolean ubah(
            int id,
            String nama,
            String nimNip,
            String email,
            String role,
            Integer idProdi,
            String passwordBaru) {

        if (!validasi(nama, nimNip, email, role)) {
            Alerts.warning("Periksa kembali data pengguna.");
            return false;
        }

        boolean ubahPassword =
                passwordBaru != null && !passwordBaru.isBlank();

        if (ubahPassword && passwordBaru.length() < 8) {
            Alerts.warning("Password minimal 8 karakter.");
            return false;
        }

        if ("ADMIN".equals(role)) idProdi = null;

        String sql = "UPDATE pengguna SET "
                + "nama = ?, nim_nip = ?, email = ?, "
                + "role = ?, id_prodi = ?";

        if (ubahPassword) {
            sql += ", password = ?";
        }

        sql += " WHERE id_pengguna = ?";

        try (Connection conn = Database.getConnection()) {

            if (conn == null) return false;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                int i = 1;

                ps.setString(i++, nama.trim());
                ps.setString(i++, nimNip.trim());
                ps.setString(i++, email.trim());
                ps.setString(i++, role);

                setProdi(ps, i++, idProdi);

                if (ubahPassword) {
                    ps.setString(i++, hashPassword(passwordBaru));
                }

                ps.setInt(i, id);

                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            tampilkanError(e);
            return false;
        }
    }

    // MENGHAPUS PENGGUNA
    public boolean hapus(int id) {

        Pengguna aktif = Session.getCurrent();

        if (aktif != null && aktif.getIdPengguna() == id) {
            Alerts.warning("Tidak dapat menghapus akun sendiri.");
            return false;
        }

        String sql = "DELETE FROM pengguna WHERE id_pengguna = ?";

        try (Connection conn = Database.getConnection()) {

            if (conn == null) return false;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, id);

                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {

            if (e.getErrorCode() == 1451) {
                Alerts.warning(
                        "Pengguna masih memiliki data terkait "
                        + "sehingga tidak dapat dihapus."
                );
            } else {
                Alerts.error("Gagal menghapus pengguna.");
            }

            System.err.println(e.getMessage());
            return false;
        }
    }

    // VALIDASI INPUT
    private boolean validasi(
            String nama,
            String nimNip,
            String email,
            String role) {

        return nama != null && !nama.isBlank()
                && nama.length() <= 100
                && nimNip != null && !nimNip.isBlank()
                && nimNip.length() <= 20
                && email != null
                && email.length() <= 100
                && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
                && ("ADMIN".equals(role)
                || "DOSEN".equals(role)
                || "MAHASISWA".equals(role));
    }

    private void setProdi(
            PreparedStatement ps,
            int index,
            Integer idProdi) throws SQLException {

        if (idProdi == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, idProdi);
        }
    }

    private void tampilkanError(SQLException e) {

        if (e.getErrorCode() == 1062) {
            Alerts.warning("Email atau NIM/NIP sudah digunakan.");
        } else if (e.getErrorCode() == 1452) {
            Alerts.warning("Program studi tidak ditemukan.");
        } else {
            Alerts.error("Terjadi kesalahan database: " + e.getMessage());
        }

        System.err.println(e.getMessage());
    }
    private String hashPassword(String password) {
    try {
        return Hash.hashSHA256(password);
    } catch (java.io.UnsupportedEncodingException e) {
        throw new IllegalStateException(
            "Gagal melakukan hashing password.", e
        );
    }
}
}
