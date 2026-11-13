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
            nameText.setText(userAktif.getNama());
            roleText.setText(userAktif.getClass().getSimpleName());
        } else {
            nameText.setText("Guest");
            roleText.setText("-");
        }
    }

    @FXML
    void SwitchToPeminjaman(ActionEvent event) {
        Navigator.show("AjukanPeminjaman", "SIMARU - Ajukan peminjaman", 800, 600);
    }
    
    @FXML
    void SwitchToRuangan(ActionEvent event) {
        Navigator.show("LihatJadwal", "SIMARU - Lihat Jadwal", 800, 600);
    }
    @FXML
    void SwitchToListRuangan(ActionEvent event) {
        Navigator.show("LihatRuangan", "SIMARU - Lihat Ruangan", 800, 600);
    }

    @FXML
    void SwitchToStatusPeminjaman(ActionEvent event) {

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
