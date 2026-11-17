package com.simaru.controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import com.simaru.dao.PeminjamanDAO;
import com.simaru.model.Peminjaman;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

public class StatusPeminjamanController implements Initializable {

    private static final DateTimeFormatter TANGGAL = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter JAM = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter WAKTU = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    @FXML
    private VBox root;
    @FXML
    private ComboBox<String> cbFilter;
    @FXML
    private Label lblRingkasan;
    @FXML
    private TableView<Peminjaman> tvPeminjaman;
    @FXML
    private TableColumn<Peminjaman, Number> colId;
    @FXML
    private TableColumn<Peminjaman, String> colRuangan;
    @FXML
    private TableColumn<Peminjaman, String> colLokasi;
    @FXML
    private TableColumn<Peminjaman, String> colTanggal;
    @FXML
    private TableColumn<Peminjaman, String> colJam;
    @FXML
    private TableColumn<Peminjaman, String> colKeperluan;
    @FXML
    private TableColumn<Peminjaman, String> colStatus;
    @FXML
    private TableColumn<Peminjaman, String> colCatatan;
    @FXML
    private TableColumn<Peminjaman, String> colDiajukan;
    @FXML
    private Label lblDRuangan;
    @FXML
    private Label lblDLokasi;
    @FXML
    private Label lblDTanggal;
    @FXML
    private Label lblDJam;
    @FXML
    private Label lblDKeperluan;
    @FXML
    private Label lblDStatus;
    @FXML
    private Label lblDCatatan;
    @FXML
    private Label lblDAdmin;
    @FXML
    private Label lblDWaktuVerifikasi;
    @FXML
    private Button btnBatalkan;

    private final PeminjamanDAO dao = new PeminjamanDAO();
    private List<Peminjaman> dataSemua = List.of();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (Session.getCurrent() == null || Session.isAdmin()) {
            Alerts.error("Halaman ini untuk Mahasiswa dan Dosen.");
            root.setDisable(true);
            return;
        }

        siapkanKolom();
        pasangCss();
        cbFilter.setItems(FXCollections.observableArrayList(
                Status.SEMUA, Status.PENDING, Status.DISETUJUI, Status.DITOLAK));
        cbFilter.setValue(Status.SEMUA);
        cbFilter.valueProperty().addListener((obs, lama, baru) -> muatTabel());

        tvPeminjaman.setPlaceholder(new Label("Belum ada pengajuan peminjaman."));
        tvPeminjaman.getSelectionModel().selectedItemProperty()
                .addListener((obs, lama, baru) -> tampilkanDetail(baru));

        muatData();
    }

    private void siapkanKolom() {
        colId.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getIdPeminjaman()));
        colRuangan.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getNamaRuangan()));
        colLokasi.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getLokasi()));
        colTanggal.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(format(c.getValue().getTanggal())));
        colJam.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
                formatJam(c.getValue().getJamMulai(), c.getValue().getJamSelesai())));
        colKeperluan.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getKeperluan()));
        colStatus.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getStatus()));
        colStatus.setCellFactory(kolom -> new StatusCell<>());
        colCatatan.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getCatatan()));
        colDiajukan.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
                formatWaktu(c.getValue().getTanggalPengajuan())));
    }

    private void pasangCss() {
        URL css = getClass().getResource("/com/simaru/css/status.css");
        if (css != null) {
            root.getStylesheets().add(css.toExternalForm());
        }
    }

    private void muatData() {
        dataSemua = dao.tampilkanMilik(Session.getCurrent().getIdPengguna());
        int pending = 0;
        int disetujui = 0;
        int ditolak = 0;
        for (Peminjaman p : dataSemua) {
            if (Status.PENDING.equalsIgnoreCase(p.getStatus())) {
                pending++;
            } else if (Status.DISETUJUI.equalsIgnoreCase(p.getStatus())) {
                disetujui++;
            } else if (Status.DITOLAK.equalsIgnoreCase(p.getStatus())) {
                ditolak++;
            }
        }
        lblRingkasan.setText("Pending: " + pending + " | Disetujui: " + disetujui
                + " | Ditolak: " + ditolak);
        muatTabel();
    }

    private void muatTabel() {
        String filter = cbFilter.getValue();
        if (filter == null || Status.SEMUA.equalsIgnoreCase(filter)) {
            tvPeminjaman.setItems(FXCollections.observableArrayList(dataSemua));
        } else {
            tvPeminjaman.setItems(FXCollections.observableArrayList(
                    dao.tampilkanMilik(Session.getCurrent().getIdPengguna(), filter)));
        }
        tampilkanDetail(null);
    }

    private void tampilkanDetail(Peminjaman p) {
        if (p == null) {
            lblDRuangan.setText("-");
            lblDLokasi.setText("-");
            lblDTanggal.setText("-");
            lblDJam.setText("-");
            lblDKeperluan.setText("-");
            lblDStatus.setText("-");
            lblDCatatan.setText("-");
            lblDAdmin.setText("-");
            lblDWaktuVerifikasi.setText("-");
            btnBatalkan.setDisable(true);
            return;
        }
        lblDRuangan.setText(p.getNamaRuangan());
        lblDLokasi.setText(p.getLokasi() == null ? "-" : p.getLokasi());
        lblDTanggal.setText(format(p.getTanggal()));
        lblDJam.setText(formatJam(p.getJamMulai(), p.getJamSelesai()));
        lblDKeperluan.setText(p.getKeperluan());
        lblDStatus.setText(p.getStatus());
        lblDCatatan.setText(p.getCatatan() == null || p.getCatatan().isBlank() ? "-" : p.getCatatan());
        lblDAdmin.setText(p.getNamaAdmin() == null ? "-" : p.getNamaAdmin());
        lblDWaktuVerifikasi.setText(p.getTanggalVerifikasi() == null ? "-"
                : formatWaktu(p.getTanggalVerifikasi()));
        btnBatalkan.setDisable(!Status.PENDING.equalsIgnoreCase(p.getStatus()));
    }

    @FXML
    private void onSegarkan() {
        cbFilter.setValue(Status.SEMUA);
        muatData();
    }

    @FXML
    private void onBatalkan() {
        Peminjaman p = tvPeminjaman.getSelectionModel().getSelectedItem();
        if (p == null) {
            Alerts.warning("Pilih salah satu pengajuan terlebih dahulu.");
            return;
        }
        if (!Status.PENDING.equalsIgnoreCase(p.getStatus())) {
            Alerts.warning("Hanya pengajuan berstatus Pending yang bisa dibatalkan.");
            return;
        }
        if (!Alerts.confirm("Batalkan pengajuan peminjaman " + p.getNamaRuangan()
                + " pada " + format(p.getTanggal()) + "?")) {
            return;
        }
        int baris = dao.batalkan(p.getIdPeminjaman(), Session.getCurrent().getIdPengguna());
        if (baris == 0) {
            Alerts.error("Pengajuan tidak bisa dibatalkan karena sudah diproses atau bukan milik Anda.");
        } else {
            Alerts.info("Pengajuan berhasil dibatalkan.");
        }
        muatData();
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
