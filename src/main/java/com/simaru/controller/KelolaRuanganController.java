package com.simaru.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.simaru.dao.RuanganDAO;
import com.simaru.model.Ruangan;
import com.simaru.util.Alerts;
import com.simaru.util.Session;
import com.simaru.util.Status;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class KelolaRuanganController implements Initializable {

    @FXML
    private TableView<Ruangan> tvRuangan;
    @FXML
    private TableColumn<Ruangan, String> colNama;
    @FXML
    private TableColumn<Ruangan, String> colLokasi;
    @FXML
    private TableColumn<Ruangan, Integer> colKapasitas;
    @FXML
    private TableColumn<Ruangan, String> colFasilitas;
    @FXML
    private TableColumn<Ruangan, String> colStatus;

    @FXML
    private TextField tfNama;
    @FXML
    private TextField tfLokasi;
    @FXML
    private TextField tfKapasitas;
    @FXML
    private TextField tfFasilitas;
    @FXML
    private ComboBox<String> cbStatus;

    private final RuanganDAO ruanganDAO = new RuanganDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!Session.isAdmin()) {
            Alerts.error("Halaman ini hanya untuk Admin.");
            return;
        }

        colNama.setCellValueFactory(new PropertyValueFactory<>("namaRuangan"));
        colLokasi.setCellValueFactory(new PropertyValueFactory<>("lokasi"));
        colKapasitas.setCellValueFactory(new PropertyValueFactory<>("kapasitas"));
        colFasilitas.setCellValueFactory(new PropertyValueFactory<>("fasilitas"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        cbStatus.setItems(FXCollections.observableArrayList(
                Status.TERSEDIA, Status.DIPAKAI, Status.PERBAIKAN));
        cbStatus.setValue(Status.TERSEDIA);

        tvRuangan.setPlaceholder(new Label("Belum ada data ruangan."));
        tvRuangan.getSelectionModel().selectedItemProperty().addListener((obs, lama, baru) -> {
            if (baru != null) {
                isiForm(baru);
            }
        });

        muatUlang();
    }

    private void muatUlang() {
        tvRuangan.setItems(FXCollections.observableArrayList(ruanganDAO.tampilkan()));
    }

    private void isiForm(Ruangan ruangan) {
        tfNama.setText(ruangan.getNamaRuangan());
        tfLokasi.setText(ruangan.getLokasi());
        tfKapasitas.setText(String.valueOf(ruangan.getKapasitas()));
        tfFasilitas.setText(ruangan.getFasilitas());
        cbStatus.setValue(ruangan.getStatus());
    }

    @FXML
    private void onTambah(ActionEvent event) {
        Ruangan ruangan = bacaForm(0);
        if (ruangan == null) {
            return;
        }
        if (ruanganDAO.tambahAman(ruangan)) {
            Alerts.info("Ruangan berhasil ditambahkan.");
            bersihkan();
            muatUlang();
        }
    }

    @FXML
    private void onUbah(ActionEvent event) {
        Ruangan terpilih = tvRuangan.getSelectionModel().getSelectedItem();
        if (terpilih == null) {
            Alerts.warning("Pilih salah satu ruangan terlebih dahulu.");
            return;
        }
        Ruangan ruangan = bacaForm(terpilih.getIdRuangan());
        if (ruangan == null) {
            return;
        }
        if (ruanganDAO.ubahAman(ruangan)) {
            Alerts.info("Data ruangan berhasil diubah.");
            bersihkan();
            muatUlang();
        }
    }

    @FXML
    private void onHapus(ActionEvent event) {
        Ruangan terpilih = tvRuangan.getSelectionModel().getSelectedItem();
        if (terpilih == null) {
            Alerts.warning("Pilih salah satu ruangan terlebih dahulu.");
            return;
        }
        if (!Alerts.confirm("Hapus ruangan " + terpilih.getNamaRuangan() + "?")) {
            return;
        }
        if (ruanganDAO.hapusAman(terpilih.getIdRuangan())) {
            Alerts.info("Ruangan berhasil dihapus.");
            bersihkan();
            muatUlang();
        }
    }

    @FXML
    private void onBersihkan(ActionEvent event) {
        bersihkan();
    }

    private Ruangan bacaForm(int idRuangan) {
        String nama = teks(tfNama);
        String lokasi = teks(tfLokasi);
        String fasilitas = teks(tfFasilitas);
        String status = cbStatus.getValue();

        if (nama.isEmpty() || lokasi.isEmpty() || status == null) {
            Alerts.warning("Nama, lokasi, dan status ruangan wajib diisi.");
            return null;
        }
        int kapasitas;
        try {
            kapasitas = Integer.parseInt(teks(tfKapasitas));
        } catch (NumberFormatException e) {
            Alerts.warning("Kapasitas harus berupa angka bulat.");
            return null;
        }
        if (kapasitas <= 0) {
            Alerts.warning("Kapasitas harus lebih dari 0.");
            return null;
        }

        Ruangan ruangan = new Ruangan(idRuangan, nama, kapasitas, fasilitas, lokasi);
        ruangan.setStatus(status);
        return ruangan;
    }

    private void bersihkan() {
        tfNama.clear();
        tfLokasi.clear();
        tfKapasitas.clear();
        tfFasilitas.clear();
        cbStatus.setValue(Status.TERSEDIA);
        tvRuangan.getSelectionModel().clearSelection();
    }

    private static String teks(TextField field) {
        return field.getText() == null ? "" : field.getText().trim();
    }
}
