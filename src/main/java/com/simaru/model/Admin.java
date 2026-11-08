package com.simaru.model;

public class Admin extends Pengguna {
    private String level;

    public Admin(int id_pengguna, String nama, String email, String password, String level) {
        super(id_pengguna, nama, email, password);
        this.level = level;
    }

    public String getLevel() {
        return level;
    }
    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public String getHakAkses() {
        return "LIHAT_RUANGAN, LIHAT_JADWAL";
    }
}
