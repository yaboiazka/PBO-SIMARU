package com.simaru.controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.simaru.dao.PeminjamanDAO;
import com.simaru.dao.RuanganDAO;
import com.simaru.model.Ruangan;
import com.simaru.util.Alerts;

import static javafx.collections.FXCollections.observableArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AjukanPeminjamanController implements Initializable {

    @FXML
    private ComboBox<Ruangan> cbRuangan;

    @FXML
    private DatePicker dpTanggal;

    @FXML
    private TextArea taKeperluan;

    @FXML
    private TextField tfJamMulai;

    @FXML
    private TextField tfJamSelesai;

    @FXML
    private TextField tfMenitMulai;

    @FXML
    private TextField tfMenitSelesai;

    private RuanganDAO ruang = new RuanganDAO();
    private PeminjamanDAO pinjam = new PeminjamanDAO();
    private List <Ruangan> ruanganzzz = new ArrayList<>();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ruanganzzz = ruang.tampilkan();
        cbRuangan.setItems((observableArrayList(ruanganzzz)));
    }
    
    @FXML
    void onAjukan(ActionEvent event) {
        String jamMulaiString = tfJamMulai.getText();
        String menitMulaiString = tfMenitMulai.getText();
        String jamSelesaiString = tfJamSelesai.getText();
        String menitSelesaiString = tfMenitSelesai.getText();

        Ruangan terpilih  = cbRuangan.getValue();
        String keperluan = taKeperluan.getText();
        LocalDate tanggal = dpTanggal.getValue();
        LocalTime jamMulai = convert(jamMulaiString, menitMulaiString);
        LocalTime jamSelesai = convert(jamSelesaiString, menitSelesaiString);
        if (terpilih == null || keperluan == null || tanggal == null) {
            Alerts.info("Masih ada data yang belum lengkap");
        }
        if (jamMulai.isBefore(jamSelesai)) {
            pinjam.preTambah(terpilih, tanggal, jamMulai, jamSelesai, keperluan);
        }else{
            Alerts.error("Jam Mulai sebelum jam selesai");
        }
    }

    LocalTime convert(String jam, String menit){
        String jamMulaiString = jam + ":" + menit;
        LocalTime lengkap;
        if (jam == null|| menit == null) {
            Alerts.info("Jam atau menit tidak lengkap");
            return null;
        }
        try {
            lengkap = LocalTime.parse(jamMulaiString);
        } catch (Exception e) {
            Alerts.error("harap masukkan angka yang valid (misal 00:00)" + e.getMessage());
            lengkap = null;
        }
        return lengkap;
    }

}
