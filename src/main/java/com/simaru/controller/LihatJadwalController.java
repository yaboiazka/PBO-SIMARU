package com.simaru.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class LihatJadwalController {

    @FXML
    private TableColumn<?, ?> colJam;

    @FXML
    private TableColumn<?, ?> colKeperluan;

    @FXML
    private TableColumn<?, ?> colPengguna;

    @FXML
    private TableColumn<?, ?> colRuangan;

    @FXML
    private TableColumn<?, ?> colTanggal;

    @FXML
    private DatePicker dpTanggal;

    @FXML
    private TableView<?> tvJadwal;

    @FXML
    void onSemua(ActionEvent event) {
        
    }

    @FXML
    void onTampilkan(ActionEvent event) {

    }

}
