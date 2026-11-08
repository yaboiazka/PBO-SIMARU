package com.simaru.model;

public class Mahasiswa extends Pengguna {
    private String nim;
    private String jurusan;

    public Mahasiswa(int id_pengguna, String nama, String email, String password, String nim, String jurusan) {
        super(id_pengguna, nama, email, password);
        this.nim = nim;
        this.jurusan = jurusan;
    }

    public String getNim() {
        return nim;
    }
    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getJurusan() {
        return jurusan;
    }
    public void setJurusan(String jurusan) {
        this.jurusan = jurusan;
    }
    
    @Override
    public String getHakAkses() {
        return "LIHAT_RUANGAN, AJUKAN_PEMINJAMAN, LIHAT_STATUS, LIHAT_JADWAL";
    }
}
