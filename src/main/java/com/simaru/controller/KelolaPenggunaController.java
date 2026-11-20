package com.simaru.controller;

import java.util.Map;
import java.util.Optional;

import com.simaru.dao.KelolaPenggunaDAO;
import com.simaru.model.Pengguna;
import com.simaru.util.Alerts;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.util.StringConverter;

public class KelolaPenggunaController {

    @FXML private TextField txtCari;
    @FXML private ComboBox<String> cbFilter;

    @FXML private TableView<Pengguna> tvPengguna;
    @FXML private TableColumn<Pengguna, Integer> colId;
    @FXML private TableColumn<Pengguna, String> colNama;
    @FXML private TableColumn<Pengguna, String> colNimNip;
    @FXML private TableColumn<Pengguna, String> colEmail;
    @FXML private TableColumn<Pengguna, String> colRole;
    @FXML private TableColumn<Pengguna, String> colProgramStudi;

    private final KelolaPenggunaDAO dao = new KelolaPenggunaDAO();

    private final ObservableList<Pengguna> dataPengguna =
            FXCollections.observableArrayList();

    private FilteredList<Pengguna> dataTersaring;

    @FXML
    public void initialize() {

        // Mengatur isi filter role
        cbFilter.setItems(FXCollections.observableArrayList(
                "Semua", "ADMIN", "DOSEN", "MAHASISWA"
        ));

        cbFilter.setValue("Semua");

        // Mengatur kolom tabel
        colId.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(
                        cell.getValue().getIdPengguna()
                )
        );

        colNama.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getNama()
                )
        );

        colNimNip.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getNimNip()
                )
        );

        colEmail.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getEmail()
                )
        );

        colRole.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getRole()
                )
        );

        colProgramStudi.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getInformasiProdi()
                )
        );

        // Mengaktifkan pencarian dan filter
        dataTersaring = new FilteredList<>(dataPengguna, p -> true);

        SortedList<Pengguna> dataTerurut =
                new SortedList<>(dataTersaring);

        dataTerurut.comparatorProperty().bind(
                tvPengguna.comparatorProperty()
        );

        tvPengguna.setItems(dataTerurut);

        txtCari.textProperty().addListener(
                (observable, lama, baru) -> terapkanFilter()
        );

        muatData();
    }

    // MEMUAT DATA PENGGUNA
    private void muatData() {

        dataPengguna.setAll(dao.tampilkan());

        terapkanFilter();
    }

    // SEARCH DAN FILTER
    private void terapkanFilter() {

        if (dataTersaring == null) return;

        String keyword = txtCari.getText() == null
                ? ""
                : txtCari.getText().trim().toLowerCase();

        String role = cbFilter.getValue();

        dataTersaring.setPredicate(pengguna -> {

            boolean cocokKeyword =
                    pengguna.getNama().toLowerCase().contains(keyword)
                    || (pengguna.getNimNip() != null
                    && pengguna.getNimNip().toLowerCase().contains(keyword))
                    || pengguna.getEmail().toLowerCase().contains(keyword);

            boolean cocokRole =
                    role == null
                    || role.equals("Semua")
                    || pengguna.getRole().equals(role);

            return cocokKeyword && cocokRole;
        });
    }

    @FXML
    private void onFilter() {
        terapkanFilter();
    }

    @FXML
    private void onRefresh() {

        txtCari.clear();
        cbFilter.setValue("Semua");

        muatData();

        Alerts.info("Data pengguna berhasil diperbarui.");
    }

    // TAMBAH PENGGUNA
    @FXML
    private void onTambah() {
        tampilkanForm(null);
    }

    // EDIT PENGGUNA
    @FXML
    private void onEdit() {

        Pengguna terpilih =
                tvPengguna.getSelectionModel().getSelectedItem();

        if (terpilih == null) {
            Alerts.warning("Pilih pengguna yang ingin diedit.");
            return;
        }

        tampilkanForm(terpilih);
    }

    // HAPUS PENGGUNA
    @FXML
    private void onHapus() {

        Pengguna terpilih =
                tvPengguna.getSelectionModel().getSelectedItem();

        if (terpilih == null) {
            Alerts.warning("Pilih pengguna yang ingin dihapus.");
            return;
        }

        boolean konfirmasi = Alerts.confirm(
                "Apakah kamu yakin ingin menghapus pengguna "
                + terpilih.getNama() + "?"
        );

        if (!konfirmasi) return;

        if (dao.hapus(terpilih.getIdPengguna())) {

            Alerts.info("Pengguna berhasil dihapus.");

            muatData();
        }
    }

    // FORM TAMBAH DAN EDIT
    private void tampilkanForm(Pengguna penggunaEdit) {

        boolean modeEdit = penggunaEdit != null;

        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setTitle(
                modeEdit ? "Edit Pengguna" : "Tambah Pengguna"
        );

        dialog.setHeaderText(
                modeEdit
                ? "Ubah informasi pengguna SIMARU"
                : "Masukkan data pengguna baru"
        );

        dialog.getDialogPane().getButtonTypes().addAll(
                ButtonType.OK, ButtonType.CANCEL
        );

        TextField txtNama = new TextField();
        TextField txtNimNip = new TextField();
        TextField txtEmail = new TextField();
        PasswordField txtPassword = new PasswordField();

        ComboBox<String> cbRole = new ComboBox<>();

        cbRole.setItems(FXCollections.observableArrayList(
                "ADMIN", "DOSEN", "MAHASISWA"
        ));

        ComboBox<PilihanProdi> cbProdi = new ComboBox<>();

        cbProdi.setMaxWidth(Double.MAX_VALUE);

        cbProdi.setConverter(new StringConverter<PilihanProdi>() {

            @Override
            public String toString(PilihanProdi pilihan) {
                return pilihan == null ? "" : pilihan.getNama();
            }

            @Override
            public PilihanProdi fromString(String string) {
                return null;
            }
        });

        // Mengambil daftar program studi dari database
        Map<Integer, String> daftarProdi =
                dao.tampilkanProgramStudi();

        for (Map.Entry<Integer, String> entry : daftarProdi.entrySet()) {
            cbProdi.getItems().add(
                    new PilihanProdi(entry.getKey(), entry.getValue())
            );
        }

        txtPassword.setPromptText(
                modeEdit
                ? "Kosongkan jika password tidak diubah"
                : "Minimal 8 karakter"
        );

        // Admin tidak wajib memilih program studi
        cbRole.valueProperty().addListener(
                (observable, lama, baru) -> {

                    boolean admin = "ADMIN".equals(baru);

                    cbProdi.setDisable(admin);

                    if (admin) {
                        cbProdi.getSelectionModel().clearSelection();
                    }
                }
        );

        // Mengisi data ketika mode edit
        if (modeEdit) {

            txtNama.setText(penggunaEdit.getNama());
            txtNimNip.setText(penggunaEdit.getNimNip());
            txtEmail.setText(penggunaEdit.getEmail());

            cbRole.setValue(penggunaEdit.getRole());

            Integer idProdi = penggunaEdit.getIdProdi();

            if (idProdi != null) {
                for (PilihanProdi pilihan : cbProdi.getItems()) {

                    if (pilihan.getId() == idProdi) {
                        cbProdi.setValue(pilihan);
                        break;
                    }
                }
            }

        } else {
            cbRole.setValue("MAHASISWA");
        }

        GridPane grid = new GridPane();

        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(15));

        grid.addRow(0, new Label("Nama"), txtNama);
        grid.addRow(1, new Label("NIM/NIP"), txtNimNip);
        grid.addRow(2, new Label("Email"), txtEmail);
        grid.addRow(3, new Label("Password"), txtPassword);
        grid.addRow(4, new Label("Role"), cbRole);
        grid.addRow(5, new Label("Program Studi"), cbProdi);

        dialog.getDialogPane().setContent(grid);

        // Validasi sebelum form disimpan
        Button tombolOK = (Button) dialog.getDialogPane()
                .lookupButton(ButtonType.OK);

        tombolOK.addEventFilter(ActionEvent.ACTION, event -> {

            String password = txtPassword.getText();

            boolean passwordTidakValid =
                    !modeEdit && (password == null || password.length() < 8);

            boolean passwordEditTidakValid =
                    modeEdit && password != null
                    && !password.isBlank() && password.length() < 8;

            if (txtNama.getText().isBlank()
                    || txtNimNip.getText().isBlank()
                    || txtEmail.getText().isBlank()
                    || cbRole.getValue() == null
                    || passwordTidakValid
                    || passwordEditTidakValid
                    || !txtEmail.getText().matches(
                            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

                Alerts.warning(
                        "Periksa kembali data. Password minimal 8 karakter."
                );

                event.consume();
            }
        });

        Optional<ButtonType> hasil = dialog.showAndWait();

        if (hasil.isEmpty() || hasil.get() != ButtonType.OK) {
            return;
        }

        String nama = txtNama.getText().trim();
        String nimNip = txtNimNip.getText().trim();
        String email = txtEmail.getText().trim();
        String role = cbRole.getValue();

        Integer idProdi = null;

        if (!"ADMIN".equals(role) && cbProdi.getValue() != null) {
            idProdi = cbProdi.getValue().getId();
        }

        String password = txtPassword.getText();

        boolean berhasil;

        if (modeEdit) {

            berhasil = dao.ubah(
                    penggunaEdit.getIdPengguna(),
                    nama,
                    nimNip,
                    email,
                    role,
                    idProdi,
                    password.isBlank() ? null : password
            );

        } else {

            berhasil = dao.tambah(
                    nama,
                    nimNip,
                    email,
                    password,
                    role,
                    idProdi
            );
        }

        if (berhasil) {

            Alerts.info(
                    modeEdit
                    ? "Data pengguna berhasil diperbarui."
                    : "Pengguna baru berhasil ditambahkan."
            );

            muatData();
        }
    }

    // MODEL PILIHAN PROGRAM STUDI
    private static class PilihanProdi {

        private final int id;
        private final String nama;

        public PilihanProdi(int id, String nama) {
            this.id = id;
            this.nama = nama;
        }

        public int getId() {
            return id;
        }

        public String getNama() {
            return nama;
        }

        @Override
        public String toString() {
            return nama;
        }
    }
}
