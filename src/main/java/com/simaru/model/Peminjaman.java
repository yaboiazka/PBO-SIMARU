package com.simaru.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Peminjaman {
    private int id_peminjaman;
    private int id_pengguna;
    private int id_ruangan;
    private String nama_pengguna;
    private String nama_ruangan;
    private LocalDate tanggal;
    private LocalTime jam_mulai;
    private LocalTime jam_selesai;
    private LocalDate tanggal_pengajuan;
    private String keperluan;
    private String status;
    private String catatan;

    public Peminjaman(int id_pengguna, int id_ruangan, String nama_pengguna, String nama_ruangan, LocalDate tanggal, LocalTime jam_mulai,  LocalTime jam_selesai, String keperluan) {
        // this.id_peminjaman = id_peminjaman;
        this.id_pengguna = id_pengguna;
        this.id_ruangan = id_ruangan;
        this.nama_pengguna = nama_pengguna;
        this.nama_ruangan = nama_ruangan;
        this.tanggal = tanggal;
        this.jam_mulai = jam_mulai;
        this.jam_selesai = jam_selesai;
        this.keperluan = keperluan;
        // this.status = status;
        // this.catatan = catatan;
    }

    // Getter dan Setter
    public int getIdPeminjaman() {
        return id_peminjaman;
    }
    public void setIdPeminjaman(int id_peminjaman) {
        this.id_peminjaman = id_peminjaman;
    }

    public int getIdPengguna() {
        return id_pengguna;
    }
    public void setIdPengguna(int id_pengguna) {
        this.id_pengguna = id_pengguna;
    }

    public int getIdRuangan() {
        return id_ruangan;
    }
    public void setIdRuangan(int id_ruangan) {
        this.id_ruangan = id_ruangan;
    }

    public String getNamaPengguna() {
        return nama_pengguna;
    }
    public void setNamaPengguna(String nama_pengguna) {
        this.nama_pengguna = nama_pengguna;
    }

    public String getNamaRuangan() {
        return nama_ruangan;
    }
    public void setNamaRuangan(String nama_ruangan) {
        this.nama_ruangan = nama_ruangan;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }
    public void setTanggal(LocalDate tanggal) {
        this.tanggal = tanggal;
    }

    public LocalDate getTanggalPengajuan() {
        return tanggal_pengajuan;
    }
    public void setTanggalPengajuan(LocalDate tanggal) {
        this.tanggal = tanggal_pengajuan;
    }

    public LocalTime getJamMulai() {
        return jam_mulai;
    }
    public void setJamMulai(LocalTime jam_mulai) {
        this.jam_mulai = jam_mulai;
    }

    public LocalTime getJamSelesai() {
        return jam_selesai;
    }
    public void setJamSelesai(LocalTime jam_selesai) {
        this.jam_selesai = jam_selesai;
    }

    public String getKeperluan() {
        return keperluan;
    }
    public void setKeperluan(String keperluan) {
        this.keperluan = keperluan;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getCatatan() {
        return catatan;
    }
    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public String getTimeRange() {
        return jam_mulai + " - " + jam_selesai;
    }
}
