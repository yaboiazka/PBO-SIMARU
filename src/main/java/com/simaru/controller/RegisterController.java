package com.simaru.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.simaru.dao.PenggunaDAO;
import com.simaru.dao.ProgramStudiDAO;
import com.simaru.model.ProgramStudi;
import com.simaru.util.Alerts;
import com.simaru.util.Navigator;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController implements Initializable {

    @FXML
    private TextField tfNama;
    @FXML
    private TextField tfNimNip;
    @FXML
    private TextField tfEmail;
    @FXML
    private PasswordField pfPassword;
    @FXML
    private PasswordField pfKonfirmasi;
    @FXML
    private ComboBox<ProgramStudi> cbProdi;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbProdi.setItems(FXCollections.observableArrayList(new ProgramStudiDAO().tampilkan()));
    }

    @FXML
    private void onDaftar(ActionEvent event) {
        String nama = teks(tfNama);
        String nimNip = teks(tfNimNip);
        String email = teks(tfEmail);
        String password = pfPassword.getText() == null ? "" : pfPassword.getText();
        String konfirmasi = pfKonfirmasi.getText() == null ? "" : pfKonfirmasi.getText();
        ProgramStudi prodi = cbProdi.getValue();

        if (nama.isEmpty() || nimNip.isEmpty() || email.isEmpty() || password.isEmpty() || prodi == null) {
            Alerts.warning("Semua data wajib diisi, termasuk program studi.");
            return;
        }
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            Alerts.warning("Format email tidak valid.");
            return;
        }
        if (password.length() < 6) {
            Alerts.warning("Password minimal 6 karakter.");
            return;
        }
        if (!password.equals(konfirmasi)) {
            Alerts.warning("Konfirmasi password tidak sama.");
            return;
        }

        String pesan = PenggunaDAO.registrasi(nama, nimNip, email, password, prodi.getIdProdi());
        if (pesan == null) {
            Alerts.info("Registrasi berhasil. Silakan login.");
            Navigator.show("Login", "SIMARU - Login", 420, 460);
        } else {
            Alerts.error(pesan);
        }
    }

    @FXML
    private void onBatal(ActionEvent event) {
        Navigator.show("Login", "SIMARU - Login", 420, 460);
    }

    private static String teks(TextField field) {
        return field.getText() == null ? "" : field.getText().trim();
    }
}
