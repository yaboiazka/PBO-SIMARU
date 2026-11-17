package com.simaru.controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

import com.simaru.dao.PeminjamanDAO;
import com.simaru.dao.RuanganDAO;
import com.simaru.model.Ruangan;
import com.simaru.util.Alerts;
import com.simaru.util.Session;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public class AjukanPeminjamanController implements Initializable {

    private static final DateTimeFormatter FORMAT_JAM = DateTimeFormatter.ofPattern("H:mm");

    @FXML
    private ComboBox<Ruangan> cbRuangan;
    @FXML
    private DatePicker dpTanggal;
    @FXML
    private TextField tfJamMulai;
    @FXML
    private TextField tfJamSelesai;
    @FXML
    private TextArea taKeperluan;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (Session.getCurrent() == null || Session.isAdmin()) {
            Alerts.error("Halaman ini untuk Mahasiswa dan Dosen.");
            return;
        }

        cbRuangan.setItems(FXCollections.observableArrayList(new RuanganDAO().tampilkanTersedia()));
        cbRuangan.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Ruangan item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null
                        : item.getNamaRuangan() + " - " + item.getLokasi() + " (kap. " + item.getKapasitas() + ")");
            }
        });
        cbRuangan.setConverter(new StringConverter<>() {
            @Override
            public String toString(Ruangan item) {
                return item == null ? "" : item.getNamaRuangan();
            }

            @Override
            public Ruangan fromString(String string) {
                return null;
            }
        });
        dpTanggal.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(empty || item.isBefore(LocalDate.now()));
            }
        });
    }

    @FXML
    private void onAjukan(ActionEvent event) {
        Ruangan ruangan = cbRuangan.getValue();
        LocalDate tanggal = dpTanggal.getValue();
        String keperluan = taKeperluan.getText() == null ? "" : taKeperluan.getText().trim();

        if (ruangan == null || tanggal == null || keperluan.isEmpty()) {
            Alerts.warning("Ruangan, tanggal, dan keperluan wajib diisi.");
            return;
        }

        LocalTime jamMulai;
        LocalTime jamSelesai;
        try {
            jamMulai = LocalTime.parse(tfJamMulai.getText().trim(), FORMAT_JAM);
            jamSelesai = LocalTime.parse(tfJamSelesai.getText().trim(), FORMAT_JAM);
        } catch (DateTimeParseException | NullPointerException e) {
            Alerts.warning("Format jam harus HH:mm, contoh 08:00.");
            return;
        }
        if (!jamSelesai.isAfter(jamMulai)) {
            Alerts.error("Jam selesai harus setelah jam mulai.");
            return;
        }

        boolean berhasil = new PeminjamanDAO().ajukan(ruangan, tanggal, jamMulai, jamSelesai, keperluan);
        if (berhasil) {
            Alerts.info("Pengajuan peminjaman berhasil dikirim dan menunggu verifikasi admin.");
            cbRuangan.setValue(null);
            dpTanggal.setValue(null);
            tfJamMulai.clear();
            tfJamSelesai.clear();
            taKeperluan.clear();
        }
    }
}
