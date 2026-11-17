package com.simaru.controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import com.simaru.dao.VerifikasiDAO;
import com.simaru.model.Peminjaman;
import com.simaru.model.Verifikasi;
import com.simaru.util.Alerts;
import com.simaru.util.Session;
import com.simaru.util.Status;
import com.simaru.util.StatusCell;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class VerifikasiPeminjamanController implements Initializable {

    private static final DateTimeFormatter TANGGAL = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter JAM = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter WAKTU = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    @FXML
    private VBox root;
    @FXML
    private Tab tabMenunggu;
    @FXML
    private TableView<Peminjaman> tvMenunggu;
    @FXML
    private TableColumn<Peminjaman, Number> colMNo;
    @FXML
    private TableColumn<Peminjaman, String> colMPemohon;
    @FXML
    private TableColumn<Peminjaman, String> colMPeran;
    @FXML
    private TableColumn<Peminjaman, String> colMRuangan;
    @FXML
    private TableColumn<Peminjaman, String> colMTanggal;
    @FXML
    private TableColumn<Peminjaman, String> colMJam;
    @FXML
    private TableColumn<Peminjaman, String> colMKeperluan;
    @FXML
    private TableColumn<Peminjaman, String> colMDiajukan;
    @FXML
    private Label lblDPemohon;
    @FXML
    private Label lblDIdentitas;
    @FXML
    private Label lblDRuangan;
    @FXML
    private Label lblDKapasitas;
    @FXML
    private Label lblDTanggal;
    @FXML
    private Label lblDJam;
    @FXML
    private Label lblDKeperluan;
    @FXML
    private Label lblDStatusRuangan;
    @FXML
    private Label lblBentrok;
    @FXML
    private TextArea taCatatan;
    @FXML
    private Button btnSetujui;
    @FXML
    private Button btnTolak;
    @FXML
    private ComboBox<String> cbRiwayat;
    @FXML
    private TableView<Verifikasi> tvRiwayat;
    @FXML
    private TableColumn<Verifikasi, Number> colRNo;
    @FXML
    private TableColumn<Verifikasi, String> colRPemohon;
    @FXML
    private TableColumn<Verifikasi, String> colRRuangan;
    @FXML
    private TableColumn<Verifikasi, String> colRTanggal;
    @FXML
    private TableColumn<Verifikasi, String> colRJam;
    @FXML
    private TableColumn<Verifikasi, String> colRStatus;
    @FXML
    private TableColumn<Verifikasi, String> colRAdmin;
    @FXML
    private TableColumn<Verifikasi, String> colRCatatan;
    @FXML
    private TableColumn<Verifikasi, String> colRWaktu;

    private final VerifikasiDAO dao = new VerifikasiDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (Session.getCurrent() == null || !Session.isAdmin()) {
            Alerts.error("Halaman ini hanya untuk Admin.");
            root.setDisable(true);
            return;
        }

        siapkanKolom();
        pasangCss();
        cbRiwayat.setItems(FXCollections.observableArrayList(
                Status.SEMUA, Status.DISETUJUI, Status.DITOLAK));
        cbRiwayat.setValue(Status.SEMUA);
        cbRiwayat.valueProperty().addListener((obs, lama, baru) -> muatRiwayat());

        tvMenunggu.setPlaceholder(new Label("Tidak ada pengajuan yang menunggu verifikasi."));
        tvRiwayat.setPlaceholder(new Label("Belum ada riwayat verifikasi."));
        tvMenunggu.getSelectionModel().selectedItemProperty()
                .addListener((obs, lama, baru) -> tampilkanDetail(baru));
        tvRiwayat.getSelectionModel().selectedItemProperty()
                .addListener((obs, lama, baru) -> tampilkanDetailRiwayat(baru));

        muatSemua();
    }

    private void siapkanKolom() {
        colMNo.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getIdPeminjaman()));
        colMPemohon.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getNamaPengguna()));
        colMPeran.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(peran(c.getValue().getPeran())));
        colMRuangan.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getNamaRuangan()));
        colMTanggal.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(format(c.getValue().getTanggal())));
        colMJam.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
                formatJam(c.getValue().getJamMulai(), c.getValue().getJamSelesai())));
        colMKeperluan.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getKeperluan()));
        colMDiajukan.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
                formatWaktu(c.getValue().getTanggalPengajuan())));

        colRNo.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
                c.getValue().getPeminjaman().getIdPeminjaman()));
        colRPemohon.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
                c.getValue().getPeminjaman().getNamaPengguna()));
        colRRuangan.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
                c.getValue().getPeminjaman().getNamaRuangan()));
        colRTanggal.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
                format(c.getValue().getPeminjaman().getTanggal())));
        colRJam.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
                formatJam(c.getValue().getPeminjaman().getJamMulai(),
                        c.getValue().getPeminjaman().getJamSelesai())));
        colRStatus.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
                c.getValue().getStatusVerifikasi()));
        colRStatus.setCellFactory(kolom -> new StatusCell<>());
        colRAdmin.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
                c.getValue().getAdmin().getNama()));
        colRCatatan.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getCatatan()));
        colRWaktu.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
                formatWaktu(c.getValue().getTanggalVerifikasi())));
    }

    private void pasangCss() {
        URL css = getClass().getResource("/com/simaru/css/status.css");
        if (css != null) {
            root.getStylesheets().add(css.toExternalForm());
        }
    }

    private void muatSemua() {
        muatMenunggu();
        muatRiwayat();
    }

    private void muatMenunggu() {
        List<Peminjaman> data = dao.tampilkanMenunggu();
        tvMenunggu.setItems(FXCollections.observableArrayList(data));
        tabMenunggu.setText("Menunggu Verifikasi (" + data.size() + ")");
        tampilkanDetail(null);
    }

    private void muatRiwayat() {
        tvRiwayat.setItems(FXCollections.observableArrayList(
                dao.tampilkanRiwayat(cbRiwayat.getValue())));
    }

    private void tampilkanDetail(Peminjaman p) {
        if (p == null) {
            lblDPemohon.setText("-");
            lblDIdentitas.setText("-");
            lblDRuangan.setText("-");
            lblDKapasitas.setText("-");
            lblDTanggal.setText("-");
            lblDJam.setText("-");
            lblDKeperluan.setText("-");
            lblDStatusRuangan.setText("-");
            lblBentrok.setText("");
            btnSetujui.setDisable(true);
            btnTolak.setDisable(true);
            return;
        }
        lblDPemohon.setText(p.getNamaPengguna() + " (" + peran(p.getPeran()) + ")");
        lblDIdentitas.setText(p.getNomorIdentitas() == null ? "-" : p.getNomorIdentitas());
        lblDRuangan.setText(p.getNamaRuangan());
        lblDKapasitas.setText(p.getKapasitas() + " orang");
        lblDTanggal.setText(format(p.getTanggal()));
        lblDJam.setText(formatJam(p.getJamMulai(), p.getJamSelesai()));
        lblDKeperluan.setText(p.getKeperluan());
        lblDStatusRuangan.setText(p.getStatusRuangan());
        btnTolak.setDisable(false);

        if (Status.PERBAIKAN.equalsIgnoreCase(p.getStatusRuangan())) {
            lblBentrok.setText("Ruangan sedang dalam perbaikan.");
            btnSetujui.setDisable(true);
            return;
        }

        List<Peminjaman> bentrok = dao.cekBentrok(p.getIdRuangan(), p.getTanggal(),
                p.getJamMulai(), p.getJamSelesai());
        if (bentrok.isEmpty()) {
            lblBentrok.setText("");
            btnSetujui.setDisable(false);
            return;
        }
        StringBuilder pesan = new StringBuilder("Bentrok dengan jadwal:");
        for (Peminjaman b : bentrok) {
            pesan.append("\n- ").append(b.getNamaRuangan())
                    .append(", ").append(formatJam(b.getJamMulai(), b.getJamSelesai()))
                    .append(", ").append(b.getNamaPengguna())
                    .append(" (").append(b.getKeperluan()).append(")");
        }
        lblBentrok.setText(pesan.toString());
        btnSetujui.setDisable(true);
    }

    private void tampilkanDetailRiwayat(Verifikasi v) {
        if (v == null) {
            return;
        }
        Peminjaman p = v.getPeminjaman();
        lblDPemohon.setText(p.getNamaPengguna() + " (" + peran(p.getPeran()) + ")");
        lblDIdentitas.setText(p.getNomorIdentitas() == null ? "-" : p.getNomorIdentitas());
        lblDRuangan.setText(p.getNamaRuangan());
        lblDKapasitas.setText(p.getKapasitas() + " orang");
        lblDTanggal.setText(format(p.getTanggal()));
        lblDJam.setText(formatJam(p.getJamMulai(), p.getJamSelesai()));
        lblDKeperluan.setText(p.getKeperluan());
        lblDStatusRuangan.setText(p.getStatusRuangan());
        lblBentrok.setText("Diverifikasi oleh " + v.getAdmin().getNama()
                + " pada " + formatWaktu(v.getTanggalVerifikasi())
                + " dengan status " + v.getStatusVerifikasi() + ".");
        taCatatan.setText(v.getCatatan() == null ? "" : v.getCatatan());
        btnSetujui.setDisable(true);
        btnTolak.setDisable(true);
    }

    @FXML
    private void onSegarkan() {
        taCatatan.clear();
        muatSemua();
    }

    @FXML
    private void onSetujui() {
        Peminjaman p = tvMenunggu.getSelectionModel().getSelectedItem();
        if (p == null) {
            Alerts.warning("Pilih salah satu pengajuan terlebih dahulu.");
            return;
        }
        if (Status.PERBAIKAN.equalsIgnoreCase(p.getStatusRuangan())) {
            Alerts.warning("Ruangan sedang dalam perbaikan.");
            return;
        }
        if (!Alerts.confirm("Setujui peminjaman " + p.getNamaRuangan() + " oleh "
                + p.getNamaPengguna() + "?")) {
            return;
        }
        String catatan = taCatatan.getText() == null ? "" : taCatatan.getText().trim();
        String pesan = dao.setujui(p.getIdPeminjaman(), Session.getCurrent().getIdPengguna(), catatan);
        if (pesan == null) {
            Alerts.info("Pengajuan berhasil disetujui.");
            taCatatan.clear();
            muatSemua();
        } else {
            Alerts.error(pesan);
            muatSemua();
        }
    }

    @FXML
    private void onTolak() {
        Peminjaman p = tvMenunggu.getSelectionModel().getSelectedItem();
        if (p == null) {
            Alerts.warning("Pilih salah satu pengajuan terlebih dahulu.");
            return;
        }
        String catatan = taCatatan.getText() == null ? "" : taCatatan.getText().trim();
        if (catatan.isEmpty()) {
            Alerts.warning("Catatan wajib diisi saat menolak pengajuan.");
            taCatatan.requestFocus();
            return;
        }
        if (!Alerts.confirm("Tolak peminjaman " + p.getNamaRuangan() + " oleh "
                + p.getNamaPengguna() + "?")) {
            return;
        }
        String pesan = dao.tolak(p.getIdPeminjaman(), Session.getCurrent().getIdPengguna(), catatan);
        if (pesan == null) {
            Alerts.info("Pengajuan berhasil ditolak.");
            taCatatan.clear();
            muatSemua();
        } else {
            Alerts.error(pesan);
            muatSemua();
        }
    }

    private static String peran(String role) {
        if (role == null) {
            return "-";
        }
        switch (role.toUpperCase()) {
            case "MAHASISWA":
                return "Mahasiswa";
            case "DOSEN":
                return "Dosen";
            case "ADMIN":
                return "Admin";
            default:
                return role;
        }
    }

    private static String format(LocalDate tanggal) {
        return tanggal == null ? "-" : tanggal.format(TANGGAL);
    }

    private static String formatJam(LocalTime mulai, LocalTime selesai) {
        if (mulai == null || selesai == null) {
            return "-";
        }
        return mulai.format(JAM) + " - " + selesai.format(JAM);
    }

    private static String formatWaktu(LocalDateTime waktu) {
        return waktu == null ? "-" : waktu.format(WAKTU);
    }
}
