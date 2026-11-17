package com.simaru.model;

public class ProgramStudi {
    private int idProdi;
    private String fakultas;
    private String programStudi;

    public ProgramStudi(int idProdi, String fakultas, String programStudi) {
        this.idProdi = idProdi;
        this.fakultas = fakultas;
        this.programStudi = programStudi;
    }

    public int getIdProdi() {
        return idProdi;
    }
    public void setIdProdi(int idProdi) {
        this.idProdi = idProdi;
    }

    public String getFakultas() {
        return fakultas;
    }
    public void setFakultas(String fakultas) {
        this.fakultas = fakultas;
    }

    public String getProgramStudi() {
        return programStudi;
    }
    public void setProgramStudi(String programStudi) {
        this.programStudi = programStudi;
    }

    @Override
    public String toString() {
        return programStudi == null ? "-" : programStudi;
    }
}
