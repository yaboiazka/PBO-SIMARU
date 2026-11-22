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
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

public class navController implements Initializable {

    @FXML
    private BorderPane root;

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

        // Tampilkan halaman default awal (Lihat Jadwal)
        SwitchToRuangan(null);
    }

    @FXML
    void SwitchToRuangan(ActionEvent event) {
        Navigator.loadInto(root, "LihatJadwal");
        setMenuAktif(ruanganButton);
    }

    @FXML
    void SwitchToPeminjaman(ActionEvent event) {
        Navigator.loadInto(root, "AjukanPeminjaman");
        setMenuAktif(peminjamanButton);
    }

    @FXML
    void SwitchToStatusPeminjaman(ActionEvent event) {
        Navigator.loadInto(root, "StatusPeminjaman");
        setMenuAktif(statusPeminjamanButton);
    }

    @FXML
    void SwitchToListRuangan(ActionEvent event) {
        Navigator.loadInto(root, "LihatRuangan");
        setMenuAktif(listRuanganButton);
    }

    private void setMenuAktif(Button buttonAktif) {
        setActive(ruanganButton, ruanganButton == buttonAktif);
        setActive(peminjamanButton, peminjamanButton == buttonAktif);
        setActive(statusPeminjamanButton, statusPeminjamanButton == buttonAktif);
        setActive(listRuanganButton, listRuanganButton == buttonAktif);
    }

    // Mengatur warna menu aktif
    private void setActive(Button button, boolean active) {
        if (button == null) return;
        button.getStyleClass().remove("nav-button-active");

        if (active) {
            button.getStyleClass().add("nav-button-active");
        }
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
}
