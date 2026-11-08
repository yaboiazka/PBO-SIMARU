package com.simaru.controller;

import java.io.UnsupportedEncodingException;

import com.simaru.App;
import com.simaru.dao.PenggunaDAO;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class loginController {

    @FXML
    private TextField emailArea;

    @FXML
    private Button loginButton;

    @FXML
    private TextField passwordArea;

    @FXML
    void handleLogin(ActionEvent event) throws UnsupportedEncodingException, Exception {
        String email = emailArea.getText();
        String password = passwordArea.getText();

        boolean isValid = PenggunaDAO.chekiAdaAtauGakAkunnya(email, password);
        
        if (isValid) {
            if (PenggunaDAO.getHakAkses(email).equals("ADMIN")) {
                // Redirect to admin dashboard
                System.out.println("Login berhasil! Redirecting to admin dashboard...");
            } else if (PenggunaDAO.getHakAkses(email).equals("DOSEN")) {
                // Redirect to dosen dashboard
                App.setRoot("secondary");
                System.out.println("Login berhasil! Redirecting to dosen dashboard...");
            } else if (PenggunaDAO.getHakAkses(email).equals("MAHASISWA")) {
                // Redirect to mahasiswa dashboard
                App.setRoot("primary");
                System.out.println("Login berhasil! Redirecting to mahasiswa dashboard...");
            }
            System.out.println("Login berhasil!");
        } else {
            System.out.println("Email atau password salah.");
        }
    }

    @FXML
    void handleRegister(ActionEvent event) {

    }

}
