package com.simaru.controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import com.simaru.dao.PeminjamanDAO;
import com.simaru.model.Peminjaman;
import com.simaru.util.Alerts;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class LihatJadwalController implements Initializable {

    private static final DateTimeFormatter TANGGAL = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter JAM = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    private TableColumn<Peminjaman, String> colJam;
    @FXML
    private TableColumn<Peminjaman, String> colKeperluan;
    @FXML
    private TableColumn<Peminjaman, String> colPengguna;
    @FXML
    private TableColumn<Peminjaman, String> colRuangan;
    @FXML
    private TableColumn<Peminjaman, String> colTanggal;
    @FXML
    private DatePicker dpTanggal;
    @FXML
    private TableView<Peminjaman> tvJadwal;
    @FXML
    private Label lblJumlah;

    private final PeminjamanDAO peminjaman = new PeminjamanDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colRuangan.setCellValueFactory(new PropertyValueFactory<>("namaRuangan"));
        colTanggal.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
                cell.getValue().getTanggal() == null ? "-" : cell.getValue().getTanggal().format(TANGGAL)));
        colJam.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
                cell.getValue().getJamMulai() == null || cell.getValue().getJamSelesai() == null ? "-"
                        : cell.getValue().getJamMulai().format(JAM) + " - "
                                + cell.getValue().getJamSelesai().format(JAM)));
        colPengguna.setCellValueFactory(new PropertyValueFactory<>("namaPengguna"));
        colKeperluan.setCellValueFactory(new PropertyValueFactory<>("keperluan"));
        tvJadwal.setPlaceholder(new Label("Tidak ada jadwal pemakaian ruangan."));
        loadData(null);
    }

    private void loadData(LocalDate tanggal) {
        try {
            var data = peminjaman.jadwal(tanggal);
            tvJadwal.setItems(FXCollections.observableArrayList(data));
            lblJumlah.setText("Total jadwal: " + data.size());
            if (data.isEmpty()) {
                tvJadwal.setPlaceholder(new Label(
                        tanggal == null ? "Belum ada jadwal pemakaian ruangan."
                                : "Tidak ada jadwal pada tanggal tersebut."));
            }
        } catch (Exception e) {
            Alerts.error("Gagal memuat data jadwal: " + e.getMessage());
            System.err.println("Gagal memuat data jadwal: " + e.getMessage());
        }
    }

    @FXML
    private void onSemua(ActionEvent event) {
        dpTanggal.setValue(null);
        loadData(null);
    }

    @FXML
    private void onTampilkan(ActionEvent event) {
        loadData(dpTanggal.getValue());
    }
}
