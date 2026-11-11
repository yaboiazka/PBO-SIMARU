package com.simaru.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.simaru.dao.RuanganDAO;
import com.simaru.model.Ruangan;
import com.simaru.util.Alerts;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class LihatRuanganController implements Initializable {

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
    private ComboBox<String> cbFilter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colNama.setCellValueFactory(new PropertyValueFactory<>("namaRuangan"));
        colLokasi.setCellValueFactory(new PropertyValueFactory<>("lokasi"));
        colKapasitas.setCellValueFactory(new PropertyValueFactory<>("kapasitas"));
        colFasilitas.setCellValueFactory(new PropertyValueFactory<>("fasilitas"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        cbFilter.setItems(FXCollections.observableArrayList("Semua", "Tersedia", "Dipakai", "Perbaikan"));
        cbFilter.setValue("Semua");
        loadData();
    }

    private void loadData() {
        try {
            RuanganDAO dao = new RuanganDAO();
            List<Ruangan> dataList = dao.filter(cbFilter.getValue());
            System.out.println("Jumlah data dari database: " + dataList.size());
            ObservableList<Ruangan> listData = FXCollections.observableArrayList(dataList);
            tvRuangan.setItems(listData);
            
        } catch (Exception e) {
            Alerts.error("Gagal memuat data ruangan: " + e.getMessage());
        }
    }

    @FXML
    private void onFilter() {
        System.out.println();
        loadData();
    }
}