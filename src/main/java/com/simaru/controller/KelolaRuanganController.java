package com.simaru.controller;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import com.simaru.dao.RuanganDAO;
import com.simaru.model.Ruangan;
import com.simaru.util.Alerts;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

public class KelolaRuanganController implements Initializable {

    @FXML
    private TableView<Ruangan> tvRuangan;

    @FXML
    private TableColumn<Ruangan, Integer> colId;

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

    @FXML
    private TextField txtCari;

    private final RuanganDAO dao = new RuanganDAO();

    private final ObservableList<Ruangan> dataRuangan =
            FXCollections.observableArrayList();

    private FilteredList<Ruangan> dataTersaring;

    // ==========================================
    // INITIALIZE
    // ==========================================

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        colId.setCellValueFactory(
                new PropertyValueFactory<>("idRuangan"));

        colNama.setCellValueFactory(
                new PropertyValueFactory<>("namaRuangan"));

        colLokasi.setCellValueFactory(
                new PropertyValueFactory<>("lokasi"));

        colKapasitas.setCellValueFactory(
                new PropertyValueFactory<>("kapasitas"));

        colFasilitas.setCellValueFactory(
                new PropertyValueFactory<>("fasilitas"));

        colStatus.setCellValueFactory(
                new PropertyValueFactory<>("status"));

        cbFilter.setItems(
                FXCollections.observableArrayList(
                        "Semua",
                        "Tersedia",
                        "Dipakai",
                        "Perbaikan"
                )
        );

        cbFilter.setValue("Semua");

        dataTersaring = new FilteredList<>(
                dataRuangan,
                ruangan -> true
        );

        tvRuangan.setItems(dataTersaring);

        txtCari.textProperty().addListener(
                (observable, lama, baru) -> terapkanPencarian()
        );

        loadData();
    }

    // ==========================================
    // READ: MEMUAT DATA RUANGAN
    // ==========================================

    private void loadData() {

        try {

            List<Ruangan> hasil = dao.filter(cbFilter.getValue());

            if (hasil == null) {
                dataRuangan.clear();
                Alerts.error("Data ruangan gagal dimuat.");
                return;
            }

            dataRuangan.setAll(hasil);

        } catch (Exception e) {

            Alerts.error(
                    "Gagal memuat data ruangan: " + e.getMessage()
            );

            System.err.println(e.getMessage());
        }
    }

    // ==========================================
    // SEARCH: PENCARIAN NAMA DAN LOKASI
    // ==========================================

    private void terapkanPencarian() {

        String keyword = txtCari.getText();

        if (keyword == null || keyword.isBlank()) {
            dataTersaring.setPredicate(ruangan -> true);
            return;
        }

        String kata = keyword.toLowerCase(Locale.ROOT);

        dataTersaring.setPredicate(ruangan ->
                cocok(ruangan.getNamaRuangan(), kata)
                || cocok(ruangan.getLokasi(), kata)
        );
    }

    private boolean cocok(String nilai, String kata) {

        return nilai != null
                && nilai.toLowerCase(Locale.ROOT).contains(kata);
    }

    // ==========================================
    // FILTER STATUS
    // ==========================================

    @FXML
    private void onFilter() {
        loadData();
    }

    // ==========================================
    // REFRESH DATA
    // ==========================================

    @FXML
    private void onRefresh() {

        txtCari.clear();
        cbFilter.setValue("Semua");

        loadData();
    }

    // ==========================================
    // CREATE: TAMBAH RUANGAN
    // ==========================================

    @FXML
    private void onTambah() {

        Dialog<Ruangan> dialog = new Dialog<>();

        dialog.setTitle("Tambah Ruangan");
        dialog.setHeaderText("Form Tambah Ruangan Baru");

        ButtonType simpanButtonType = new ButtonType(
                "Simpan",
                ButtonBar.ButtonData.OK_DONE
        );

        dialog.getDialogPane().getButtonTypes().addAll(
                simpanButtonType,
                ButtonType.CANCEL
        );

        // INPUT FORM

        TextField txtNama = new TextField();
        txtNama.setPromptText("Masukkan nama ruangan");

        TextField txtLokasi = new TextField();
        txtLokasi.setPromptText("Masukkan lokasi ruangan");

        TextField txtKapasitas = new TextField();
        txtKapasitas.setPromptText("Contoh: 40");

        TextArea txtFasilitas = new TextArea();
        txtFasilitas.setPromptText("Masukkan fasilitas ruangan");
        txtFasilitas.setPrefRowCount(3);
        txtFasilitas.setWrapText(true);

        ComboBox<String> cbStatus = new ComboBox<>();

        cbStatus.setItems(
                FXCollections.observableArrayList(
                        "Tersedia",
                        "Dipakai",
                        "Perbaikan"
                )
        );

        cbStatus.setValue("Tersedia");
        cbStatus.setMaxWidth(Double.MAX_VALUE);

        // LAYOUT FORM

        GridPane grid = new GridPane();

        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(15));

        grid.add(new Label("Nama Ruangan"), 0, 0);
        grid.add(txtNama, 1, 0);

        grid.add(new Label("Lokasi"), 0, 1);
        grid.add(txtLokasi, 1, 1);

        grid.add(new Label("Kapasitas"), 0, 2);
        grid.add(txtKapasitas, 1, 2);

        grid.add(new Label("Fasilitas"), 0, 3);
        grid.add(txtFasilitas, 1, 3);

        grid.add(new Label("Status"), 0, 4);
        grid.add(cbStatus, 1, 4);

        txtNama.setPrefWidth(280);
        txtLokasi.setPrefWidth(280);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(450);

        // VALIDASI INPUT

        Node tombolSimpan = dialog.getDialogPane()
                .lookupButton(simpanButtonType);

        tombolSimpan.addEventFilter(
                ActionEvent.ACTION,
                event -> {

                    if (txtNama.getText().isBlank()
                            || txtLokasi.getText().isBlank()
                            || txtKapasitas.getText().isBlank()) {

                        Alerts.error(
                                "Nama, lokasi, dan kapasitas wajib diisi."
                        );

                        event.consume();
                        return;
                    }

                    try {

                        int kapasitas = Integer.parseInt(
                                txtKapasitas.getText().trim()
                        );

                        if (kapasitas <= 0) {

                            Alerts.error(
                                    "Kapasitas harus lebih dari 0."
                            );

                            event.consume();
                        }

                    } catch (NumberFormatException e) {

                        Alerts.error(
                                "Kapasitas harus berupa angka yang valid."
                        );

                        event.consume();
                    }
                }
        );

        // MEMBENTUK OBJEK RUANGAN

        dialog.setResultConverter(button -> {

            if (button == simpanButtonType) {

                int kapasitas = Integer.parseInt(
                        txtKapasitas.getText().trim()
                );

                Ruangan ruangan = new Ruangan(
                        0,
                        txtNama.getText().trim(),
                        kapasitas,
                        txtFasilitas.getText().trim(),
                        txtLokasi.getText().trim()
                );

                ruangan.setStatus(cbStatus.getValue());

                return ruangan;
            }

            return null;
        });

        // MENYIMPAN KE DATABASE

        Optional<Ruangan> hasil = dialog.showAndWait();

        hasil.ifPresent(ruangan -> {

            boolean berhasil = dao.tambahRuangan(ruangan);

            if (berhasil) {

                cbFilter.setValue("Semua");
                txtCari.clear();

                loadData();

                Alerts.info(
                        "Ruangan berhasil ditambahkan ke database."
                );
            }
        });
    }

    // ==========================================
    // UPDATE: EDIT RUANGAN
    // ==========================================

    @FXML
    private void onEdit() {

        Ruangan terpilih = tvRuangan.getSelectionModel()
                .getSelectedItem();

        if (terpilih == null) {

            Alerts.error(
                    "Silakan pilih ruangan yang ingin diedit."
            );

            return;
        }

        Dialog<Ruangan> dialog = new Dialog<>();

        dialog.setTitle("Edit Ruangan");
        dialog.setHeaderText("Perbarui Informasi Ruangan");

        ButtonType simpanButtonType = new ButtonType(
                "Simpan Perubahan",
                ButtonBar.ButtonData.OK_DONE
        );

        dialog.getDialogPane().getButtonTypes().addAll(
                simpanButtonType,
                ButtonType.CANCEL
        );

        // INPUT FORM

        TextField txtNama = new TextField(
                terpilih.getNamaRuangan()
        );

        TextField txtLokasi = new TextField(
                terpilih.getLokasi()
        );

        TextField txtKapasitas = new TextField(
                String.valueOf(terpilih.getKapasitas())
        );

        TextArea txtFasilitas = new TextArea(
                terpilih.getFasilitas() == null
                        ? ""
                        : terpilih.getFasilitas()
        );

        txtFasilitas.setPrefRowCount(3);
        txtFasilitas.setWrapText(true);

        ComboBox<String> cbStatus = new ComboBox<>();

        cbStatus.setItems(
                FXCollections.observableArrayList(
                        "Tersedia",
                        "Dipakai",
                        "Perbaikan"
                )
        );

        String statusAwal = terpilih.getStatus();

        if (statusAwal == null || statusAwal.isBlank()) {
            statusAwal = "Tersedia";
        }

        cbStatus.setValue(statusAwal);
        cbStatus.setMaxWidth(Double.MAX_VALUE);

        // LAYOUT FORM

        GridPane grid = new GridPane();

        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(15));

        grid.add(new Label("Nama Ruangan"), 0, 0);
        grid.add(txtNama, 1, 0);

        grid.add(new Label("Lokasi"), 0, 1);
        grid.add(txtLokasi, 1, 1);

        grid.add(new Label("Kapasitas"), 0, 2);
        grid.add(txtKapasitas, 1, 2);

        grid.add(new Label("Fasilitas"), 0, 3);
        grid.add(txtFasilitas, 1, 3);

        grid.add(new Label("Status"), 0, 4);
        grid.add(cbStatus, 1, 4);

        txtNama.setPrefWidth(280);
        txtLokasi.setPrefWidth(280);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(450);

        // VALIDASI INPUT

        Node tombolSimpan = dialog.getDialogPane()
                .lookupButton(simpanButtonType);

        tombolSimpan.addEventFilter(
                ActionEvent.ACTION,
                event -> {
                    if (txtNama.getText().isBlank()
                            || txtLokasi.getText().isBlank()
                            || txtKapasitas.getText().isBlank()
                            || cbStatus.getValue() == null) {
                        Alerts.error(
                                "Semua data wajib diisi."
                        );
                        event.consume();
                        return;
                    }
                    try {
                        int kapasitas = Integer.parseInt(
                                txtKapasitas.getText().trim()
                        );
                        if (kapasitas <= 0) {
                            Alerts.error(
                                    "Kapasitas harus lebih dari 0."
                            );
                            event.consume();
                        }
                    } catch (NumberFormatException e) {
                        Alerts.error(
                                "Kapasitas harus berupa angka."
                        );
                        event.consume();
                    }
                }
        );

        // MEMBENTUK OBJEK RUANGAN BARU
        dialog.setResultConverter(button -> {
            if (button == simpanButtonType) {
                Ruangan ruangan = new Ruangan(
                        terpilih.getIdRuangan(),
                        txtNama.getText().trim(),
                        Integer.parseInt(
                                txtKapasitas.getText().trim()
                        ),
                        txtFasilitas.getText().trim(),
                        txtLokasi.getText().trim()
                );
                ruangan.setStatus(cbStatus.getValue());
                return ruangan;
            }
            return null;
        });

        // MENYIMPAN PERUBAHAN KE DATABASE
        Optional<Ruangan> hasil = dialog.showAndWait();
        hasil.ifPresent(ruangan -> {
            dao.ubah(ruangan);
            cbFilter.setValue("Semua");
            txtCari.clear();
            loadData();
        });
    }

    // ==========================================
    // DELETE: HAPUS RUANGAN
    // ==========================================

    @FXML
    private void onHapus() {
        Ruangan terpilih = tvRuangan.getSelectionModel()
                .getSelectedItem();
        if (terpilih == null) {
            Alerts.error(
                    "Silakan pilih ruangan yang ingin dihapus."
            );
            return;
        }

        Alert konfirmasi = new Alert(
                Alert.AlertType.CONFIRMATION
        );
        konfirmasi.setTitle("Konfirmasi Hapus Ruangan");
        konfirmasi.setHeaderText(
                "Hapus ruangan: " + terpilih.getNamaRuangan() + "?"
        );

        konfirmasi.setContentText(
                "Apakah Anda yakin ingin menghapus ruangan ini?"
        );

        Optional<ButtonType> jawaban = konfirmasi.showAndWait();
        if (jawaban.isPresent()
                && jawaban.get() == ButtonType.OK) {
            dao.hapus(terpilih.getIdRuangan());
            loadData();
        }
    }
}
