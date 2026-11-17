package com.simaru.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Jadwal {
    private int id_jadwal;
    private int id_ruangan;
    private int id_peminjaman;
    private LocalDate tanggal;
    private LocalTime jam_mulai; 
    private LocalTime jam_selesai;

    public Jadwal(int id_ruangan, int id_peminjaman){
        this.id_ruangan = id_ruangan;
        this.id_peminjaman = id_peminjaman;
    }
    
    // Getter dan Setter
    public int getIdJadwal() {
        return id_jadwal;
    }
    public void setIdJadwal(int id_jadwal) {
        this.id_jadwal = id_jadwal;
    }

    public int getIdRuangan() {
        return id_ruangan;
    }
    public void setIdRuangan(int id_ruangan) {
        this.id_ruangan = id_ruangan;
    }

    public int getIdPeminjaman() {
        return id_peminjaman;
    }
    public void setIdPeminjaman(int id_peminjaman) {
        this.id_peminjaman = id_peminjaman;
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
}
