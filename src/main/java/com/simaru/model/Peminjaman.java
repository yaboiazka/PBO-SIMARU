package com.simaru.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private String keperluan;
    private String status;
    private String catatan;

    // Atribut tambahan (append-only) untuk halaman status, verifikasi, dan laporan.
    private String lokasi;
    private String peran;
    private String statusRuangan;
    private int kapasitas;
    private LocalDateTime tanggalPengajuan;
    private String namaAdmin;
    private LocalDateTime tanggalVerifikasi;
    private String nomorIdentitas;

    public Peminjaman(int id_peminjaman, int id_pengguna, int id_ruangan, String nama_pengguna, String nama_ruangan, LocalDate tanggal, LocalTime jam_mulai,  LocalTime jam_selesai, String keperluan, String status, String catatan) {
        this.id_peminjaman = id_peminjaman;
        this.id_pengguna = id_pengguna;
        this.id_ruangan = id_ruangan;
        this.nama_pengguna = nama_pengguna;
        this.nama_ruangan = nama_ruangan;
        this.tanggal = tanggal;
        this.jam_mulai = jam_mulai;
        this.jam_selesai = jam_selesai;
        this.keperluan = keperluan;
        this.status = status;
        this.catatan = catatan;
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

    public String getLokasi() {
        return lokasi;
    }
    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getPeran() {
        return peran;
    }
    public void setPeran(String peran) {
        this.peran = peran;
    }

    public String getStatusRuangan() {
        return statusRuangan;
    }
    public void setStatusRuangan(String statusRuangan) {
        this.statusRuangan = statusRuangan;
    }

    public int getKapasitas() {
        return kapasitas;
    }
    public void setKapasitas(int kapasitas) {
        this.kapasitas = kapasitas;
    }

    public LocalDateTime getTanggalPengajuan() {
        return tanggalPengajuan;
    }
    public void setTanggalPengajuan(LocalDateTime tanggalPengajuan) {
        this.tanggalPengajuan = tanggalPengajuan;
    }

    public String getNamaAdmin() {
        return namaAdmin;
    }
    public void setNamaAdmin(String namaAdmin) {
        this.namaAdmin = namaAdmin;
    }

    public LocalDateTime getTanggalVerifikasi() {
        return tanggalVerifikasi;
    }
    public void setTanggalVerifikasi(LocalDateTime tanggalVerifikasi) {
        this.tanggalVerifikasi = tanggalVerifikasi;
    }

    public String getNomorIdentitas() {
        return nomorIdentitas;
    }
    public void setNomorIdentitas(String nomorIdentitas) {
        this.nomorIdentitas = nomorIdentitas;
    }
}
