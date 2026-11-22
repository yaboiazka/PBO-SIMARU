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

public class DashboardMahasiswaController implements Initializable {

    @FXML
    private BorderPane root;
    @FXML
    private Label lblNama;
    @FXML
    private Label lblPeran;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Pengguna pengguna = Session.getCurrent();
        if (pengguna == null || Session.isAdmin()) {
            Alerts.error("Halaman ini untuk Mahasiswa dan Dosen.");
            Navigator.show("Login", "SIMARU - Login", 900, 560);
            return;
        }
        lblNama.setText(pengguna.getNama());
        lblPeran.setText(pengguna.getClass().getSimpleName());
    }

    @FXML
    private void onBeranda(ActionEvent event) {
        Navigator.show("DashboardMahasiswa", "SIMARU - Dashboard", 1060, 680);
    }

    @FXML
    private void onAjukan(ActionEvent event) {
        Navigator.loadInto(root, "AjukanPeminjaman");
    }

    @FXML
    private void onStatus(ActionEvent event) {
        Navigator.loadInto(root, "StatusPeminjaman");
    }

    @FXML
    private void onJadwal(ActionEvent event) {
        Navigator.loadInto(root, "LihatJadwal");
    }

    @FXML
    private void onRuangan(ActionEvent event) {
        Navigator.loadInto(root, "LihatRuangan");
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
