package com.simaru.model;

/**
 * DTO ringkasan laporan penggunaan per ruangan.
 */
public class RingkasanRuangan {
    private int idRuangan;
    private String namaRuangan;
    private int totalPengajuan;
    private int disetujui;
    private int ditolak;
    private int pending;
    private double totalJamTerpakai;

    public RingkasanRuangan(int idRuangan, String namaRuangan, int totalPengajuan,
            int disetujui, int ditolak, int pending, double totalJamTerpakai) {
        this.idRuangan = idRuangan;
        this.namaRuangan = namaRuangan;
        this.totalPengajuan = totalPengajuan;
        this.disetujui = disetujui;
        this.ditolak = ditolak;
        this.pending = pending;
        this.totalJamTerpakai = totalJamTerpakai;
    }

    public int getIdRuangan() {
        return idRuangan;
    }
    public void setIdRuangan(int idRuangan) {
        this.idRuangan = idRuangan;
    }

    public String getNamaRuangan() {
        return namaRuangan;
    }
    public void setNamaRuangan(String namaRuangan) {
        this.namaRuangan = namaRuangan;
    }

    public int getTotalPengajuan() {
        return totalPengajuan;
    }
    public void setTotalPengajuan(int totalPengajuan) {
        this.totalPengajuan = totalPengajuan;
    }

    public int getDisetujui() {
        return disetujui;
    }
    public void setDisetujui(int disetujui) {
        this.disetujui = disetujui;
    }

    public int getDitolak() {
        return ditolak;
    }
    public void setDitolak(int ditolak) {
        this.ditolak = ditolak;
    }

    public int getPending() {
        return pending;
    }
    public void setPending(int pending) {
        this.pending = pending;
    }

    public double getTotalJamTerpakai() {
        return totalJamTerpakai;
    }
    public void setTotalJamTerpakai(double totalJamTerpakai) {
        this.totalJamTerpakai = totalJamTerpakai;
    }
}
