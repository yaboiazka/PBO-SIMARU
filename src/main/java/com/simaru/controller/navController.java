package com.simaru.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.simaru.model.Pengguna;
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

    }

    @FXML
    void SwitchToRuangan(ActionEvent event) {

    }

    @FXML
    void SwitchToStatusPeminjaman(ActionEvent event) {

    }

    @FXML
    void doLogOut(ActionEvent event) {

    }

}
