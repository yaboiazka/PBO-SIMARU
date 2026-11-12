package com.simaru.model;

public class Dosen extends Pengguna {
    private String nip;
    private String fakultas;

    public Dosen(int id, String nama, String email, String passwordHash, String nip, String fakultas) {
        super(id, nama, email, passwordHash);
        this.nip = nip;
        this.fakultas = fakultas;
    }

    @Override
    public String getHakAkses() {
        return "LIHAT_RUANGAN, LIHAT_JADWAL";
    }

    public String getRole() {
        return "DOSEN";
    }

}
