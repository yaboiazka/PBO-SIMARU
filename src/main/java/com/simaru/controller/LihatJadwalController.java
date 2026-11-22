package com.simaru.controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.simaru.dao.PeminjamanDAO;
import com.simaru.model.Peminjaman;
import com.simaru.util.Alerts;
import com.simaru.util.Session; // Pastikan import Session

import static javafx.collections.FXCollections.observableArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox; // Import VBox

public class LihatJadwalController implements Initializable{

    // Tambahkan fx:id ini ke root VBox di file LihatJadwal.fxml
    @FXML
    private VBox rootContainer; 

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
        // Panggil fungsi untuk memuat navbar
        loadNavbar();

        // Inisialisasi kolom tabel
        colRuangan.setCellValueFactory(new PropertyValueFactory<>("namaRuangan"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colJam.setCellValueFactory(new PropertyValueFactory<>("timeRange"));
        colPengguna.setCellValueFactory(new PropertyValueFactory<>("namaPengguna"));
        colKeperluan.setCellValueFactory(new PropertyValueFactory<>("keperluan"));
        
        loadData(null);
    }

    // Fungsi baru untuk memuat navigasi dinamis
    private void loadNavbar() {
        try {
            String role = Session.getCurrent().getRole();
            String fxmlNav = "admin".equalsIgnoreCase(role) ? "dashboardAdmin.fxml" : "nav.fxml";
            
            URL navUrl = getClass().getResource("/com/simaru/view/" + fxmlNav);
            if (navUrl == null) {
                System.err.println("File FXML navigasi tidak ditemukan: " + fxmlNav);
                return;
            }
            
            Node navbar = FXMLLoader.load(navUrl);
            
            if (rootContainer != null) {
                rootContainer.getChildren().add(0, navbar);
            } else {
                System.err.println("rootContainer null. Pastikan VBox paling luar di FXML punya fx:id=\"rootContainer\"");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alerts.error("Gagal memuat navigasi: " + e.getMessage());
        }
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