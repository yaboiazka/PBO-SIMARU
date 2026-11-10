package com.simaru.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public final class Alerts {

    private Alerts() {
    }

    public static void info(String pesan) {
        tampil(Alert.AlertType.INFORMATION, "Informasi", pesan);
    }

    public static void warning(String pesan) {
        tampil(Alert.AlertType.WARNING, "Perhatian", pesan);
    }

    public static void error(String pesan) {
        tampil(Alert.AlertType.ERROR, "Kesalahan", pesan);
    }

    public static boolean confirm(String pesan) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, pesan, ButtonType.YES, ButtonType.NO);
        alert.setHeaderText(null);
        alert.setTitle("Konfirmasi");
        return alert.showAndWait().filter(b -> b == ButtonType.YES).isPresent();
    }

    private static void tampil(Alert.AlertType tipe, String judul, String pesan) {
        Alert alert = new Alert(tipe, pesan, ButtonType.OK);
        alert.setHeaderText(null);
        alert.setTitle(judul);
        alert.showAndWait();
    }
}