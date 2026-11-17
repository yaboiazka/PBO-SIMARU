package com.simaru.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.simaru.dao.LaporanDAO;
import com.simaru.dao.RuanganDAO;
import com.simaru.model.Peminjaman;
import com.simaru.model.RingkasanRuangan;
import com.simaru.model.Ruangan;
import com.simaru.util.Alerts;
import com.simaru.util.LaporanExporter;
import com.simaru.util.Session;
import com.simaru.util.Status;
import com.simaru.util.StatusCell;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

public class LaporanPenggunaanController implements Initializable {

    private static final DateTimeFormatter TANGGAL = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter JAM = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter NAMA_FILE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Locale LOKAL = Locale.forLanguageTag("id-ID");

    @FXML
    private VBox root;
    @FXML
    private DatePicker dpDari;
    @FXML
    private DatePicker dpSampai;
    @FXML
    private ComboBox<Ruangan> cbRuangan;
    @FXML
    private ComboBox<String> cbStatus;
    @FXML
    private Label lblTotalPengajuan;
    @FXML
    private Label lblDisetujui;
    @FXML
    private Label lblDitolak;
    @FXML
    private Label lblJamTerpakai;
    @FXML
    private TableView<RingkasanRuangan> tvRingkasan;
    @FXML
    private TableColumn<RingkasanRuangan, String> colRNama;
    @FXML
    private TableColumn<RingkasanRuangan, Number> colRTotal;
    @FXML
    private TableColumn<RingkasanRuangan, Number> colRSetuju;
    @FXML
    private TableColumn<RingkasanRuangan, Number> colRDitolak;
    @FXML
    private TableColumn<RingkasanRuangan, Number> colRPending;
    @FXML
    private TableColumn<RingkasanRuangan, String> colRJam;
    @FXML
    private TableView<Peminjaman> tvRincian;
    @FXML
    private TableColumn<Peminjaman, Number> colNo;
    @FXML
    private TableColumn<Peminjaman, String> colRuangan;
    @FXML
    private TableColumn<Peminjaman, String> colPeminjam;
    @FXML
    private TableColumn<Peminjaman, String> colPeran;
    @FXML
    private TableColumn<Peminjaman, String> colTanggal;
    @FXML
    private TableColumn<Peminjaman, String> colJam;
    @FXML
    private TableColumn<Peminjaman, String> colKeperluan;
    @FXML
    private TableColumn<Peminjaman, String> colStatus;

    private final LaporanDAO dao = new LaporanDAO();
    private List<RingkasanRuangan> dataRingkasan = new ArrayList<>();
    private List<Peminjaman> dataRincian = new ArrayList<>();
    private LocalDate periodeDari;
    private LocalDate periodeSampai;
    private String namaRuangan;
    private String statusRincian;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (Session.getCurrent() == null || !Session.isAdmin()) {
            Alerts.error("Halaman ini hanya untuk Admin.");
            root.setDisable(true);
            return;
        }

        siapkanKolom();
        pasangCss();
        muatRuangan();
        cbStatus.setItems(FXCollections.observableArrayList(
                Status.SEMUA, Status.PENDING, Status.DISETUJUI, Status.DITOLAK));
        cbStatus.setValue(Status.DISETUJUI);
        tvRingkasan.setPlaceholder(new Label("Tidak ada data peminjaman pada periode ini."));
        tvRincian.setPlaceholder(new Label("Tidak ada data peminjaman pada periode ini."));

        dpDari.setValue(LocalDate.now().withDayOfMonth(1));
        dpSampai.setValue(LocalDate.now());
        kosongkan();
    }

    private void siapkanKolom() {
        colRNama.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getNamaRuangan()));
        colRTotal.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTotalPengajuan()));
        colRSetuju.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getDisetujui()));
        colRDitolak.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getDitolak()));
        colRPending.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getPending()));
        colRJam.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
                formatJam(c.getValue().getTotalJamTerpakai())));

        colNo.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
                dataRincian.indexOf(c.getValue()) + 1));
        colRuangan.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getNamaRuangan()));
        colPeminjam.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getNamaPengguna()));
        colPeran.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(peran(c.getValue().getPeran())));
        colTanggal.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(format(c.getValue().getTanggal())));
        colJam.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
                formatJam(c.getValue().getJamMulai(), c.getValue().getJamSelesai())));
        colKeperluan.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getKeperluan()));
        colStatus.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getStatus()));
        colStatus.setCellFactory(kolom -> new StatusCell<>());
    }

    private void pasangCss() {
        URL css = getClass().getResource("/com/simaru/css/status.css");
        if (css != null) {
            root.getStylesheets().add(css.toExternalForm());
        }
    }

    private void muatRuangan() {
        Ruangan semua = new Ruangan(0, "Semua Ruangan", 0, "-", "-");
        semua.setStatus(Status.TERSEDIA);
        List<Ruangan> items = new ArrayList<>();
        items.add(semua);
        List<Ruangan> dariDb = new RuanganDAO().tampilkan();
        if (dariDb != null) {
            items.addAll(dariDb);
        }
        cbRuangan.setItems(FXCollections.observableArrayList(items));
        cbRuangan.setConverter(new StringConverter<Ruangan>() {
            @Override
            public String toString(Ruangan ruangan) {
                return ruangan == null ? "" : ruangan.getNamaRuangan();
            }

            @Override
            public Ruangan fromString(String teks) {
                return null;
            }
        });
        cbRuangan.setValue(semua);
    }

    private void kosongkan() {
        dataRingkasan = new ArrayList<>();
        dataRincian = new ArrayList<>();
        tvRingkasan.setItems(FXCollections.observableArrayList());
        tvRincian.setItems(FXCollections.observableArrayList());
        perbaruiKartu();
    }

    @FXML
    private void onTampilkan() {
        LocalDate dari = dpDari.getValue();
        LocalDate sampai = dpSampai.getValue();
        if (dari == null || sampai == null) {
            Alerts.warning("Tanggal 'Dari' dan 'Sampai' wajib diisi.");
            return;
        }
        if (dari.isAfter(sampai)) {
            Alerts.warning("Tanggal 'Dari' tidak boleh melewati tanggal 'Sampai'.");
            return;
        }
        Ruangan ruangan = cbRuangan.getValue();
        Integer idRuangan = (ruangan == null || ruangan.getIdRuangan() == 0)
                ? null : ruangan.getIdRuangan();
        String status = cbStatus.getValue();

        periodeDari = dari;
        periodeSampai = sampai;
        namaRuangan = idRuangan == null ? "Semua Ruangan" : ruangan.getNamaRuangan();
        statusRincian = status;
        dataRingkasan = dao.ringkasan(dari, sampai, idRuangan);
        dataRincian = dao.rincian(dari, sampai, idRuangan, status);

        tvRingkasan.setItems(FXCollections.observableArrayList(dataRingkasan));
        tvRincian.setItems(FXCollections.observableArrayList(dataRincian));
        perbaruiKartu();
    }

    private void perbaruiKartu() {
        int total = 0;
        int disetujui = 0;
        int ditolak = 0;
        double jam = 0;
        for (RingkasanRuangan r : dataRingkasan) {
            total += r.getTotalPengajuan();
            disetujui += r.getDisetujui();
            ditolak += r.getDitolak();
            jam += r.getTotalJamTerpakai();
        }
        lblTotalPengajuan.setText("Total pengajuan: " + total);
        lblDisetujui.setText("Disetujui: " + disetujui);
        lblDitolak.setText("Ditolak: " + ditolak);
        lblJamTerpakai.setText("Jam terpakai: " + formatJam(jam));
    }

    @FXML
    private void onCetak() {
        if (dataRingkasan.isEmpty() && dataRincian.isEmpty()) {
            Alerts.warning("Tidak ada data untuk dicetak. Klik Tampilkan terlebih dahulu.");
            return;
        }
        VBox laporan = buatNodeLaporan();
        if (!LaporanExporter.cetak(laporan)) {
            Alerts.warning("Pencetakan dibatalkan atau gagal.");
        }
    }

    @FXML
    private void onEkspor() {
        if (dataRingkasan.isEmpty() && dataRincian.isEmpty()) {
            Alerts.warning("Tidak ada data untuk diekspor. Klik Tampilkan terlebih dahulu.");
            return;
        }
        FileChooser pemilih = new FileChooser();
        pemilih.setTitle("Simpan Laporan Penggunaan Ruangan");
        pemilih.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        pemilih.setInitialFileName("laporan-penggunaan-" + periodeDari.format(NAMA_FILE)
                + "-" + periodeSampai.format(NAMA_FILE) + ".csv");
        File file = pemilih.showSaveDialog(root.getScene() == null
                ? null : root.getScene().getWindow());
        if (file == null) {
            return;
        }
        try {
            LaporanExporter.eksporCsv(file, periodeDari, periodeSampai, namaRuangan,
                    statusRincian, dataRingkasan, dataRincian);
            Alerts.info("Laporan berhasil disimpan ke " + file.getAbsolutePath());
        } catch (IOException e) {
            Alerts.error("Gagal menyimpan file. Periksa lokasi dan izin penyimpanan.");
            System.err.println("Gagal menyimpan file: " + e.getMessage());
        }
    }

    private VBox buatNodeLaporan() {
        VBox kotak = new VBox(6);
        kotak.setPadding(new Insets(14));
        kotak.setPrefWidth(540);

        Label judul = new Label("Laporan Penggunaan Ruangan");
        judul.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Label periode = new Label("Periode: " + format(periodeDari) + " s.d. " + format(periodeSampai));
        Label ruangan = new Label("Ruangan: " + namaRuangan);
        Label status = new Label("Status rincian: " + statusRincian);

        int total = 0;
        int disetujui = 0;
        int ditolak = 0;
        double jam = 0;
        for (RingkasanRuangan r : dataRingkasan) {
            total += r.getTotalPengajuan();
            disetujui += r.getDisetujui();
            ditolak += r.getDitolak();
            jam += r.getTotalJamTerpakai();
        }
        Label kartu = new Label("Total pengajuan: " + total + "   Disetujui: " + disetujui
                + "   Ditolak: " + ditolak + "   Jam terpakai: " + formatJam(jam));
        kotak.getChildren().addAll(judul, periode, ruangan, status, kartu);

        Label judulRingkasan = new Label("Ringkasan per Ruangan");
        judulRingkasan.setStyle("-fx-font-weight: bold;");
        kotak.getChildren().add(judulRingkasan);
        GridPane tabelRingkasan = new GridPane();
        tabelRingkasan.setHgap(10);
        tabelRingkasan.setVgap(2);
        tambahSel(tabelRingkasan, 0, 0, "Ruangan");
        tambahSel(tabelRingkasan, 1, 0, "Pengajuan");
        tambahSel(tabelRingkasan, 2, 0, "Disetujui");
        tambahSel(tabelRingkasan, 3, 0, "Ditolak");
        tambahSel(tabelRingkasan, 4, 0, "Pending");
        tambahSel(tabelRingkasan, 5, 0, "Jam");
        int barisRingkasan = 1;
        for (RingkasanRuangan r : dataRingkasan) {
            tambahSel(tabelRingkasan, 0, barisRingkasan, r.getNamaRuangan());
            tambahSel(tabelRingkasan, 1, barisRingkasan, String.valueOf(r.getTotalPengajuan()));
            tambahSel(tabelRingkasan, 2, barisRingkasan, String.valueOf(r.getDisetujui()));
            tambahSel(tabelRingkasan, 3, barisRingkasan, String.valueOf(r.getDitolak()));
            tambahSel(tabelRingkasan, 4, barisRingkasan, String.valueOf(r.getPending()));
            tambahSel(tabelRingkasan, 5, barisRingkasan, formatJam(r.getTotalJamTerpakai()));
            barisRingkasan++;
        }
        kotak.getChildren().add(tabelRingkasan);

        Label judulRincian = new Label("Rincian Peminjaman");
        judulRincian.setStyle("-fx-font-weight: bold;");
        kotak.getChildren().add(judulRincian);
        GridPane tabelRincian = new GridPane();
        tabelRincian.setHgap(8);
        tabelRincian.setVgap(2);
        String[] header = {"No", "Ruangan", "Tanggal", "Jam", "Peminjam", "Status"};
        for (int i = 0; i < header.length; i++) {
            tambahSel(tabelRincian, i, 0, header[i]);
        }
        int barisRincian = 1;
        int nomor = 1;
        for (Peminjaman p : dataRincian) {
            tambahSel(tabelRincian, 0, barisRincian, String.valueOf(nomor++));
            tambahSel(tabelRincian, 1, barisRincian, p.getNamaRuangan());
            tambahSel(tabelRincian, 2, barisRincian, format(p.getTanggal()));
            tambahSel(tabelRincian, 3, barisRincian, formatJam(p.getJamMulai(), p.getJamSelesai()));
            tambahSel(tabelRincian, 4, barisRincian, p.getNamaPengguna());
            tambahSel(tabelRincian, 5, barisRincian, p.getStatus());
            barisRincian++;
        }
        kotak.getChildren().add(tabelRincian);
        return kotak;
    }

    private void tambahSel(GridPane tabel, int kolom, int baris, String teks) {
        Label label = new Label(teks == null ? "-" : teks);
        label.setStyle("-fx-font-size: 10px;");
        tabel.add(label, kolom, baris);
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

    private static String formatJam(double jam) {
        return String.format(LOKAL, "%.2f", jam);
    }
}
