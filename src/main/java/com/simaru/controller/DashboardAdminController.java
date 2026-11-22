package com.simaru.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.simaru.model.Pengguna;
import com.simaru.util.Alerts;
import com.simaru.util.Navigator;
import com.simaru.util.Session;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class DashboardAdminController implements Initializable {

    @FXML
    private BorderPane root;
    @FXML
    private Label lblNama;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Pengguna pengguna = Session.getCurrent();
        if (pengguna == null || !Session.isAdmin()) {
            Alerts.error("Halaman ini hanya untuk Admin.");
            Navigator.show("Login", "SIMARU - Login", 420, 460);
            return;
        }
        lblNama.setText(pengguna.getNama());
    }

    @FXML
    private void onBeranda(ActionEvent event) {
        Navigator.show("DashboardAdmin", "SIMARU - Dashboard Admin", 1060, 680);
    }

    @FXML
    private void onVerifikasi(ActionEvent event) {
        Navigator.loadInto(root, "VerifikasiPeminjaman");
    }

    @FXML
    private void onJadwal(ActionEvent event) {
        Navigator.loadInto(root, "LihatJadwal");
    }

    @FXML
    private void onKelolaRuangan(ActionEvent event) {
        Navigator.loadInto(root, "KelolaRuangan");
    }

    @FXML
    private void onKelolaPengguna(ActionEvent event) {
        Navigator.loadInto(root, "KelolaPengguna");
    }

    @FXML
    private void onLaporan(ActionEvent event) {
        Navigator.loadInto(root, "LaporanPenggunaan");
    }

    @FXML
    private void onLogout(ActionEvent event) {
        if (!Alerts.confirm("Yakin ingin keluar dari aplikasi?")) {
            return;
        }
        Session.clear();
        Navigator.show("Login", "SIMARU - Login", 420, 460);
    }
}
