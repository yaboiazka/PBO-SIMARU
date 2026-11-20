package com.simaru.controller;

import com.simaru.dao.PenggunaDAO;
import com.simaru.util.Alerts;
import com.simaru.util.Navigator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML
    private TextField namaField;

    @FXML
    private TextField nimNipField;

    @FXML
    private TextField emailField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField konfirmasiPasswordField;

    // Mengisi pilihan role ketika halaman dibuka
    @FXML
    private void initialize() {
        roleComboBox.getItems().setAll(
            "MAHASISWA",
            "DOSEN"
        );
    }

    // Proses registrasi
    @FXML
    private void handleRegister(ActionEvent event) {

        String nama = namaField.getText().trim();
        String nimNip = nimNipField.getText().trim();
        String email = emailField.getText().trim();
        String role = roleComboBox.getValue();
        String password = passwordField.getText();
        String konfirmasi = konfirmasiPasswordField.getText();

        // Validasi input kosong
        if (nama.isEmpty() || nimNip.isEmpty()
                || email.isEmpty() || role == null
                || password.isEmpty() || konfirmasi.isEmpty()) {

            Alerts.warning("Semua kolom wajib diisi!");
            return;
        }

        // Validasi panjang data
        if (nama.length() > 100 || nimNip.length() > 20
                || email.length() > 100) {
            Alerts.warning("Panjang data melebihi batas!");
            return;
        }

        // Validasi format email
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            Alerts.warning("Format email tidak valid!");
            return;
        }

        // Validasi password
        if (password.length() < 8) {
            Alerts.warning("Password minimal 8 karakter!");
            return;
        }

        // Validasi konfirmasi password
        if (!password.equals(konfirmasi)) {
            Alerts.warning("Konfirmasi password tidak cocok!");
            return;
        }

        // Penyimpanan ke database akan menggunakan DAO
        boolean berhasil = PenggunaDAO.register(
            nama, nimNip, email, password, role
        );

        if (berhasil) {
            Alerts.info("Registrasi berhasil! Silakan login.");

            Navigator.show(
                "login",
                "SIMARU - Login",
                420,
                460
            );

        } else {
            Alerts.error(
                "Registrasi gagal. Pastikan email dan NIM/NIP belum terdaftar."
            );
        }
    }

    // Kembali ke halaman Login
    @FXML
    private void handleBackToLogin(ActionEvent event) {

        Navigator.show(
            "login",
            "SIMARU - Login",
            420,
            460
        );
    }
}
