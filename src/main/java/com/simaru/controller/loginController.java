package com.simaru.controller;

import com.simaru.dao.PenggunaDAO;
import com.simaru.model.Pengguna;
import com.simaru.util.Alerts;
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
    void handleLogin(ActionEvent event) {

        String email = emailArea.getText().trim();
        String password = passwordArea.getText();

        if (email.isEmpty() || password.isEmpty()) {
            Alerts.error("Email dan password wajib diisi.");
            return;
        }

        try {

            Pengguna pengguna = PenggunaDAO.login(email, password);

            if (pengguna == null) {
                Alerts.error("Email atau password salah.");
                return;
            }

            // Menyimpan pengguna ke Session
            Session.setCurrent(pengguna);

            System.out.println("Login berhasil!");
            System.out.println(
                "Tipe pengguna: " +
                pengguna.getClass().getSimpleName()
            );

            if (Session.isAdmin()) {

                // Membuka Dashboard Admin
                Navigator.show(
                    "DashboardAdmin",
                    "SIMARU - Dashboard Admin",
                    1300,
                    700
                );

            } else {

                // Membuka halaman pengguna
                Navigator.show(
                    "Nav",
                    "SIMARU",
                    1300,
                    700
                );
            }

        } catch (Exception e) {

            Alerts.error(
                "Terjadi kesalahan saat login: " + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    @FXML
    void handleRegister(ActionEvent event) {

        Navigator.show(
            "Register",
            "SIMARU - Register",
            480,
            650
        );
    }
}
