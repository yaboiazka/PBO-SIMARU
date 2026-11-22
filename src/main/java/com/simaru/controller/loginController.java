package com.simaru.controller;

import com.simaru.dao.PenggunaDAO;
import com.simaru.model.Pengguna;
import com.simaru.util.Alerts;
import com.simaru.util.Navigator;
import com.simaru.util.Session;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class loginController {

    @FXML
    private TextField emailArea;

    @FXML
    private PasswordField passwordArea;

    @FXML
    void handleLogin(ActionEvent event) {
        String email = emailArea.getText() == null ? "" : emailArea.getText().trim();
        String password = passwordArea.getText() == null ? "" : passwordArea.getText();

        if (email.isEmpty() || password.isEmpty()) {
            Alerts.warning("Email dan password wajib diisi.");
            return;
        }

        Pengguna pengguna = PenggunaDAO.login(email, password);
        if (pengguna == null) {
            Alerts.error("Email atau password salah.");
            return;
        }

        new Session(pengguna);
        if (Session.isAdmin()) {
            Navigator.show("DashboardAdmin", "SIMARU - Dashboard Admin", 1300, 680);
        } else {
            Navigator.show("DashboardMahasiswa", "SIMARU - Dashboard", 1300, 680);
        }
    }

    @FXML
    void handleRegister(ActionEvent event) {
        Navigator.show("Register", "SIMARU - Registrasi Mahasiswa", 1000, 680);
    }
}
