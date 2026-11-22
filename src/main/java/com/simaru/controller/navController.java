package com.simaru.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.simaru.model.Pengguna;
import com.simaru.util.Navigator;
import com.simaru.util.Session;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class navController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Pengguna userAktif = Session.getCurrent();

        if (userAktif != null) {
            // Mengisi Text dengan data asli dari database/session
            nameText.setText(userAktif.getNama());
            roleText.setText(userAktif.getClass().getSimpleName());
        } else {
            nameText.setText("Guest");
            roleText.setText("-");
        }
                // Menentukan menu yang sedang aktif
        String halaman = Navigator.getCurrentView();

        setActive(ruanganButton, "LihatJadwal".equals(halaman));

        setActive(peminjamanButton, "AjukanPeminjaman".equals(halaman));

        setActive(statusPeminjamanButton, "StatusPeminjaman".equals(halaman));

        setActive(listRuanganButton, "LihatRuangan".equals(halaman));
    }

    @FXML
    private Button listRuanganButton;

    @FXML
    private Button logOutButton;

    @FXML
    private Text nameText;

    @FXML
    private Button peminjamanButton;

    @FXML
    private Text roleText;

    @FXML
    private Button ruanganButton;

    @FXML
    private Button statusPeminjamanButton;

    @FXML
    void SwitchToPeminjaman(ActionEvent event) {
        Navigator.show(
            "AjukanPeminjaman",
            "SIMARU - Peminjaman",
            1300,
            700
        );
    }

    @FXML
    void SwitchToStatusPeminjaman(ActionEvent event) {
        Navigator.show(
            "StatusPeminjaman",
            "SIMARU - Status Peminjaman",
            1300,
            700
        );
    }

    @FXML
    void SwitchToRuangan(ActionEvent event) {
        Navigator.show("LihatJadwal", "SIMARU - Lihat Jadwal", 1300, 700);
    }
    @FXML
    void SwitchToListRuangan(ActionEvent event) {
        Navigator.show("LihatRuangan", "SIMARU - Lihat Ruangan", 1300, 700);
    }

    @FXML
    void doLogOut(ActionEvent event) {
        Pengguna currentUser = Session.getCurrent();
        if (currentUser != null) {
            currentUser.logout();
        } else {
            System.out.println("Tidak ada pengguna yang sedang login.");
        }
    }
        // Mengatur warna menu aktif
    private void setActive(Button button, boolean active) {
        button.getStyleClass().remove("nav-button-active");

        if (active) {
            button.getStyleClass().add("nav-button-active");
        }
    }
}
