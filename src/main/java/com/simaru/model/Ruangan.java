package com.simaru.model;

public class Ruangan {
    private int id_ruangan;
    private String nama_ruangan;
    private String lokasi;
    private int kapasitas;
    private String fasilitas;
    private String status;

    public Ruangan(int id_ruangan, String nama_ruangan, int kapasitas, String fasilitas, String lokasi) {
        this.id_ruangan = id_ruangan;
        this.nama_ruangan = nama_ruangan;
        this.kapasitas = kapasitas;
        this.lokasi = lokasi;
        this.fasilitas = fasilitas;
    }

    public int getIdRuangan() {
        return id_ruangan;
    }
    public void setIdRuangan(int id_ruangan) {
        this.id_ruangan = id_ruangan;
    }

    public String getNamaRuangan() {
        return nama_ruangan;
    }
    public void setNamaRuangan(String nama_ruangan) {
        this.nama_ruangan = nama_ruangan;
    }

    public int getKapasitas() {
        return kapasitas;
    }
    public void setKapasitas(int kapasitas) {
        this.kapasitas = kapasitas;
    }

    public String getFasilitas() {
        return fasilitas;
    }
    public void setFasilitas(String fasilitas) {
        this.fasilitas = fasilitas;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getLokasi() {
        return lokasi;
    }
    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public boolean cekKetersediaan() {
        return "tersedia".equalsIgnoreCase(status);
    }
    @Override
    public String toString() {
        return this.nama_ruangan + " (" + lokasi + ")";
    }
}
