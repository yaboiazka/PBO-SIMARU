package com.simaru.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import com.simaru.model.Peminjaman;
import com.simaru.model.RingkasanRuangan;

import javafx.print.PrinterJob;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * Membantu ekspor CSV dan pencetakan laporan.
 */
public final class LaporanExporter {

    private static final DateTimeFormatter TANGGAL = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter JAM = DateTimeFormatter.ofPattern("HH:mm");
    private static final Locale LOKAL = Locale.forLanguageTag("id-ID");
    private static final char PEMISAH = ';';

    private LaporanExporter() {
    }

    public static void eksporCsv(File file, LocalDate dari, LocalDate sampai, String namaRuangan,
            String status, List<RingkasanRuangan> ringkasan, List<Peminjaman> rincian) throws IOException {
        try (BufferedWriter penulis = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8))) {
            // BOM agar Excel mengenali UTF-8.
            penulis.write('\uFEFF');

            tulisBaris(penulis, "Laporan Penggunaan Ruangan");
            tulisBaris(penulis, "Periode", format(dari) + " s.d. " + format(sampai));
            tulisBaris(penulis, "Ruangan", namaRuangan == null ? "Semua Ruangan" : namaRuangan);
            tulisBaris(penulis, "Status rincian", status == null ? Status.SEMUA : status);
            penulis.newLine();

            tulisBaris(penulis, "Ringkasan per Ruangan");
            tulisBaris(penulis, "Ruangan", "Total Pengajuan", "Disetujui", "Ditolak", "Pending",
                    "Total Jam Terpakai");
            for (RingkasanRuangan r : ringkasan) {
                tulisBaris(penulis, r.getNamaRuangan(),
                        String.valueOf(r.getTotalPengajuan()),
                        String.valueOf(r.getDisetujui()),
                        String.valueOf(r.getDitolak()),
                        String.valueOf(r.getPending()),
                        formatJam(r.getTotalJamTerpakai()));
            }
            penulis.newLine();

            tulisBaris(penulis, "Rincian Peminjaman");
            tulisBaris(penulis, "No", "Ruangan", "Peminjam", "Peran", "Tanggal", "Jam",
                    "Keperluan", "Status");
            int no = 1;
            for (Peminjaman p : rincian) {
                tulisBaris(penulis, String.valueOf(no++),
                        p.getNamaRuangan(),
                        p.getNamaPengguna(),
                        p.getPeran(),
                        format(p.getTanggal()),
                        formatJam(p.getJamMulai(), p.getJamSelesai()),
                        p.getKeperluan(),
                        p.getStatus());
            }
        }
    }

    public static boolean cetak(Parent laporan) {
        PrinterJob pekerjaan = PrinterJob.createPrinterJob();
        if (pekerjaan == null) {
            Alerts.error("Tidak ada printer yang tersedia.");
            return false;
        }
        if (!pekerjaan.showPrintDialog(laporan.getScene() == null ? null : laporan.getScene().getWindow())) {
            return false;
        }
        // Pastikan node yang belum terpasang di scene sudah di-layout.
        if (laporan.getScene() == null) {
            new Scene(new Group(laporan));
            laporan.applyCss();
            laporan.layout();
        }
        boolean sukses = pekerjaan.printPage(laporan);
        if (sukses) {
            pekerjaan.endJob();
        }
        return sukses;
    }

    private static void tulisBaris(BufferedWriter penulis, String... kolom) throws IOException {
        for (int i = 0; i < kolom.length; i++) {
            if (i > 0) {
                penulis.write(PEMISAH);
            }
            penulis.write(escape(kolom[i]));
        }
        penulis.newLine();
    }

    private static String escape(String nilai) {
        if (nilai == null) {
            return "";
        }
        String teks = nilai;
        boolean perluKutip = teks.indexOf(PEMISAH) >= 0
                || teks.indexOf('"') >= 0
                || teks.indexOf('\n') >= 0
                || teks.indexOf('\r') >= 0;
        if (!perluKutip) {
            return teks;
        }
        return '"' + teks.replace("\"", "\"\"") + '"';
    }

    private static String format(LocalDate tanggal) {
        return tanggal == null ? "-" : tanggal.format(TANGGAL);
    }

    private static String formatJam(LocalTime waktu) {
        return waktu == null ? "-" : waktu.format(JAM);
    }

    private static String formatJam(LocalTime mulai, LocalTime selesai) {
        return formatJam(mulai) + " - " + formatJam(selesai);
    }

    private static String formatJam(double jam) {
        return String.format(LOKAL, "%.2f", jam);
    }
}
