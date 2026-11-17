package com.simaru.util;

/**
 * Konstanta status dipusatkan di satu tempat agar tidak tersebar sebagai
 * literal string di seluruh kode.
 */
public final class Status {
    public static final String PENDING = "Pending";
    public static final String DISETUJUI = "Disetujui";
    public static final String DITOLAK = "Ditolak";
    public static final String SEMUA = "Semua";

    public static final String TERSEDIA = "Tersedia";
    public static final String DIPAKAI = "Dipakai";
    public static final String PERBAIKAN = "Perbaikan";

    private Status() {
    }
}
