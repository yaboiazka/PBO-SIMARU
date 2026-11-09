package com.simaru.controller;

import java.io.UnsupportedEncodingException;

import com.simaru.dao.PenggunaDAO;
import com.simaru.model.Pengguna;
import com.simaru.util.Navigator;
import com.simaru.util.Session;

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

        Pengguna pengguna = PenggunaDAO.login(email, password);
        
        if (pengguna != null) {
            Session session = new Session(pengguna);

            if (Session.isAdmin()) {
                // Redirect to admin dashboard
                System.out.println("Login berhasil! Redirecting to admin dashboard...");
            } else{
                // setUserData((session.getCurrent()).getNama(), (session.getCurrent()).getRole());
                Navigator.show("LihatJadwal", "SIMARU - Lihat Jadwal", 800,600);
            }
            System.out.println((session.getCurrent()).getClass().getSimpleName());
            System.out.println("Login berhasil!");
        } else {
            System.out.println("Email atau password salah.");
        }
    }

    @FXML
    void handleRegister(ActionEvent event) {

    }

}
