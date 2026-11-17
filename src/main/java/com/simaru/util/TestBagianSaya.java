package com.simaru.util;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

import com.simaru.dao.Database;
import com.simaru.dao.LaporanDAO;
import com.simaru.dao.PeminjamanDAO;
import com.simaru.dao.VerifikasiDAO;
import com.simaru.model.Peminjaman;
import com.simaru.model.RingkasanRuangan;
import com.simaru.model.Verifikasi;

/**
 * Uji manual berbasis console (bukan JUnit). Jalankan setelah seed uji.
 * Fokus: memastikan query DAO mengembalikan data sesuai skenario.
 */
public final class TestBagianSaya {

    private TestBagianSaya() {
    }

    public static void main(String[] args) {
        Connection koneksi = Database.getConnection();
        if (koneksi == null) {
            System.out.println("GAGAL: tidak dapat terhubung ke database.");
            return;
        }
        try (Connection c = koneksi) {
            System.out.println("Koneksi database: OK");
        } catch (Exception e) {
            System.out.println("GAGAL menutup koneksi: " + e.getMessage());
        }

        VerifikasiDAO verifikasiDAO = new VerifikasiDAO();
        List<Peminjaman> menunggu = verifikasiDAO.tampilkanMenunggu();
        System.out.println("Menunggu verifikasi: " + menunggu.size());
        for (Peminjaman p : menunggu) {
            System.out.println("  - #" + p.getIdPeminjaman() + " " + p.getNamaPengguna()
                    + " / " + p.getNamaRuangan() + " / " + p.getTanggal()
                    + " " + p.getJamMulai() + "-" + p.getJamSelesai());
        }

        List<Verifikasi> riwayat = verifikasiDAO.tampilkanRiwayat(Status.SEMUA);
        System.out.println("Riwayat verifikasi: " + riwayat.size());
        for (Verifikasi v : riwayat) {
            System.out.println("  - #" + v.getPeminjaman().getIdPeminjaman()
                    + " " + v.getStatusVerifikasi() + " oleh " + v.getAdmin().getNama()
                    + " catatan=" + v.getCatatan());
        }

        PeminjamanDAO peminjamanDAO = new PeminjamanDAO();
        System.out.println("Semua peminjaman: " + peminjamanDAO.tampilkan().size());

        LaporanDAO laporanDAO = new LaporanDAO();
        List<RingkasanRuangan> ringkasan = laporanDAO.ringkasan(
                LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30), null);
        System.out.println("Ringkasan laporan September 2026:");
        for (RingkasanRuangan r : ringkasan) {
            System.out.println("  - " + r.getNamaRuangan() + ": total=" + r.getTotalPengajuan()
                    + " disetujui=" + r.getDisetujui() + " ditolak=" + r.getDitolak()
                    + " pending=" + r.getPending() + " jam=" + r.getTotalJamTerpakai());
        }

        List<Peminjaman> rincian = laporanDAO.rincian(LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30), null, Status.DISETUJUI);
        System.out.println("Rincian disetujui: " + rincian.size());
    }
}
