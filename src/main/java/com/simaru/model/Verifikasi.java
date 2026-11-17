package com.simaru.model;

import java.time.LocalDateTime;

import com.simaru.util.Status;

/**
 * Hasil verifikasi atas sebuah peminjaman. Komposisi: objek ini tidak
 * bermakna tanpa Peminjaman (di database memakai ON DELETE CASCADE).
 */
public class Verifikasi {
    private int id_verifikasi;
    private Peminjaman peminjaman;
    private Admin admin;
    private String statusVerifikasi;
    private String catatan;
    private LocalDateTime tanggalVerifikasi;

    public Verifikasi(Peminjaman peminjaman, Admin admin) {
        this.peminjaman = peminjaman;
        this.admin = admin;
        this.statusVerifikasi = Status.PENDING;
    }

    public Verifikasi(int id_verifikasi, Peminjaman peminjaman, Admin admin,
            String statusVerifikasi, String catatan, LocalDateTime tanggalVerifikasi) {
        this.id_verifikasi = id_verifikasi;
        this.peminjaman = peminjaman;
        this.admin = admin;
        this.statusVerifikasi = statusVerifikasi;
        this.catatan = catatan;
        this.tanggalVerifikasi = tanggalVerifikasi;
    }

    public void setujui(String catatan) {
        this.statusVerifikasi = Status.DISETUJUI;
        this.catatan = catatan;
    }

    public void tolak(String catatan) {
        this.statusVerifikasi = Status.DITOLAK;
        this.catatan = catatan;
    }

    public int getIdVerifikasi() {
        return id_verifikasi;
    }
    public void setIdVerifikasi(int id_verifikasi) {
        this.id_verifikasi = id_verifikasi;
    }

    public Peminjaman getPeminjaman() {
        return peminjaman;
    }
    public void setPeminjaman(Peminjaman peminjaman) {
        this.peminjaman = peminjaman;
    }

    public Admin getAdmin() {
        return admin;
    }
    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public String getStatusVerifikasi() {
        return statusVerifikasi;
    }
    public void setStatusVerifikasi(String statusVerifikasi) {
        this.statusVerifikasi = statusVerifikasi;
    }

    public String getCatatan() {
        return catatan;
    }
    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public LocalDateTime getTanggalVerifikasi() {
        return tanggalVerifikasi;
    }
    public void setTanggalVerifikasi(LocalDateTime tanggalVerifikasi) {
        this.tanggalVerifikasi = tanggalVerifikasi;
    }
}
