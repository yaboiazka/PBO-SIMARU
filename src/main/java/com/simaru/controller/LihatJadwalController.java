package com.simaru.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.simaru.dao.PeminjamanDAO;
import com.simaru.model.Peminjaman;
import com.simaru.util.Alerts;

import static javafx.collections.FXCollections.observableArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class LihatJadwalController implements Initializable{

    @FXML
    private TableColumn<Peminjaman, LocalDate> colJam;

    @FXML
    private TableColumn<Peminjaman, String> colKeperluan;

    @FXML
    private TableColumn<Peminjaman, String> colPengguna;

    @FXML
    private TableColumn<Peminjaman, String> colRuangan;

    @FXML
    private TableColumn<Peminjaman, LocalDate> colTanggal;

    @FXML
    private DatePicker dpTanggal;

    @FXML
    private TableView<Peminjaman> tvJadwal;

    private PeminjamanDAO peminjaman = new PeminjamanDAO();

    @Override 
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Hubungkan kolom FXML dengan getter di class Ruangan
        colRuangan.setCellValueFactory(new PropertyValueFactory<>("namaRuangan"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colJam.setCellValueFactory(new PropertyValueFactory<>("timeRange"));
        colPengguna.setCellValueFactory(new PropertyValueFactory<>("namaPengguna"));
        colKeperluan.setCellValueFactory(new PropertyValueFactory<>("keperluan"));

        // colRuangan.setCellValueFactory(new PropertyValueFactory<>("namaRuangan"));
        // colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        // colJam.setCellValueFactory(new PropertyValueFactory<>("jam"));
        // colPengguna.setCellValueFactory(new PropertyValueFactory<>("namaPengguna"));
        // colKeperluan.setCellValueFactory(new PropertyValueFactory<>("keperluan"));

        // 2. Load data dari database ke TableView
        loadData(null);
    }

    private void loadData(LocalDate tanggal) {
        try {
            tvJadwal.setItems(observableArrayList(peminjaman.jadwal(tanggal)));
        } catch (Exception e) {
            Alerts.error("Gagal memuat data ruangan: " + e.getMessage());
        }
    }
    @FXML
    void onSemua(ActionEvent event) {
        dpTanggal.setValue(null);
        loadData(null);
    }

    @FXML
    void onTampilkan(ActionEvent event) {
        loadData(dpTanggal.getValue());
    }

}
