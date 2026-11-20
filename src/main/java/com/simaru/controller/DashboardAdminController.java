    package com.simaru.controller;

import com.simaru.model.Pengguna;
import com.simaru.util.Alerts;
import com.simaru.util.Navigator;
import com.simaru.util.Session;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class DashboardAdminController {

    @FXML
    private BorderPane root;
    @FXML
    private Label lblNama;
    @FXML
    private void initialize() {
        Pengguna pengguna = Session.getCurrent();
        if (pengguna != null) {
            lblNama.setText(pengguna.getNama());
        } else {
            lblNama.setText("Admin");
        }
    }

    @FXML
    private void onKelolaRuangan() {
        Navigator.loadInto(root, "KelolaRuangan");
    }

    // Handler sementara untuk menu di luar
    // pekerjaan CRUD Ruangan kita.


    @FXML
    private void onVerifikasi() {
        Navigator.loadInto(root, "VerifikasiPeminjaman");
    }

    @FXML
    private void onJadwal() {
        Navigator.loadInto(root, "LihatJadwal");
    }

    @FXML
    private void onKelolaPengguna() {
        Navigator.loadInto(root, "KelolaPengguna");
    }

    @FXML
    private void onLaporan() {
        Navigator.loadInto(root, "LaporanPenggunaan");
    }

    @FXML
    private void onLogout() {
        Pengguna pengguna = Session.getCurrent();
        if (pengguna != null) {
            pengguna.logout();
        } else {
            Navigator.show(
                "Login",
                "SIMARU - Login",
                420,
                460
            );
        }
    }
}
