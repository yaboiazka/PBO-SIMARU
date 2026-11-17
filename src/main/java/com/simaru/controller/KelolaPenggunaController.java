package com.simaru.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.simaru.dao.PenggunaDAO;
import com.simaru.dao.ProgramStudiDAO;
import com.simaru.model.Admin;
import com.simaru.model.Dosen;
import com.simaru.model.Mahasiswa;
import com.simaru.model.Pengguna;
import com.simaru.model.ProgramStudi;
import com.simaru.util.Alerts;
import com.simaru.util.Role;
import com.simaru.util.Session;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class KelolaPenggunaController implements Initializable {

    @FXML
    private TableView<Pengguna> tvPengguna;
    @FXML
    private TableColumn<Pengguna, String> colNama;
    @FXML
    private TableColumn<Pengguna, String> colNimNip;
    @FXML
    private TableColumn<Pengguna, String> colEmail;
    @FXML
    private TableColumn<Pengguna, String> colRole;
    @FXML
    private TableColumn<Pengguna, String> colProdi;

    @FXML
    private TextField tfNama;
    @FXML
    private TextField tfNimNip;
    @FXML
    private TextField tfEmail;
    @FXML
    private PasswordField pfPassword;
    @FXML
    private ComboBox<String> cbRole;
    @FXML
    private ComboBox<ProgramStudi> cbProdi;

    private final PenggunaDAO penggunaDAO = new PenggunaDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!Session.isAdmin()) {
            Alerts.error("Halaman ini hanya untuk Admin.");
            return;
        }

        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colNimNip.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
                cell.getValue().getNomorIdentitas() == null ? "-" : cell.getValue().getNomorIdentitas()));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(cell -> new ReadOnlyStringWrapper(roleDari(cell.getValue())));
        colProdi.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
                cell.getValue().getNamaProdi() == null ? "-" : cell.getValue().getNamaProdi()));

        cbRole.setItems(FXCollections.observableArrayList(Role.MAHASISWA, Role.DOSEN, Role.ADMIN));
        cbRole.setValue(Role.MAHASISWA);
        cbProdi.setItems(FXCollections.observableArrayList(new ProgramStudiDAO().tampilkan()));

        cbRole.valueProperty().addListener((obs, lama, baru) -> {
            boolean admin = Role.ADMIN.equals(baru);
            cbProdi.setDisable(admin);
            if (admin) {
                cbProdi.setValue(null);
            }
        });

        tvPengguna.setPlaceholder(new Label("Belum ada data pengguna."));
        tvPengguna.getSelectionModel().selectedItemProperty().addListener((obs, lama, baru) -> {
            if (baru != null) {
                isiForm(baru);
            }
        });

        muatUlang();
    }

    private void muatUlang() {
        tvPengguna.setItems(FXCollections.observableArrayList(penggunaDAO.tampilkan()));
    }

    private void isiForm(Pengguna pengguna) {
        tfNama.setText(pengguna.getNama());
        tfNimNip.setText(pengguna.getNomorIdentitas());
        tfEmail.setText(pengguna.getEmail());
        pfPassword.clear();
        String role = roleDari(pengguna);
        cbRole.setValue(role);
        if (!Role.ADMIN.equals(role)) {
            ProgramStudi prodi = cariProdi(pengguna.getIdProdi());
            cbProdi.setValue(prodi);
        }
    }

    @FXML
    private void onTambah(ActionEvent event) {
        if (!validasiUmum(true)) {
            return;
        }
        String role = cbRole.getValue();
        Integer idProdi = Role.ADMIN.equals(role) ? null : prodiTerpilih();
        if (penggunaDAO.tambahLengkap(teks(tfNama), teks(tfNimNip), teks(tfEmail),
                pfPassword.getText(), role, idProdi)) {
            Alerts.info("Pengguna berhasil ditambahkan.");
            bersihkan();
            muatUlang();
        }
    }

    @FXML
    private void onUbah(ActionEvent event) {
        Pengguna terpilih = tvPengguna.getSelectionModel().getSelectedItem();
        if (terpilih == null) {
            Alerts.warning("Pilih salah satu pengguna terlebih dahulu.");
            return;
        }
        if (!validasiUmum(false)) {
            return;
        }
        String role = cbRole.getValue();
        Integer idProdi = Role.ADMIN.equals(role) ? null : prodiTerpilih();
        if (penggunaDAO.ubahLengkap(terpilih.getIdPengguna(), teks(tfNama), teks(tfNimNip),
                teks(tfEmail), pfPassword.getText(), role, idProdi)) {
            Alerts.info("Data pengguna berhasil diubah.");
            bersihkan();
            muatUlang();
        }
    }

    @FXML
    private void onHapus(ActionEvent event) {
        Pengguna terpilih = tvPengguna.getSelectionModel().getSelectedItem();
        if (terpilih == null) {
            Alerts.warning("Pilih salah satu pengguna terlebih dahulu.");
            return;
        }
        if (Session.getCurrent() != null
                && terpilih.getIdPengguna() == Session.getCurrent().getIdPengguna()) {
            Alerts.warning("Tidak dapat menghapus akun yang sedang digunakan.");
            return;
        }
        if (!Alerts.confirm("Hapus pengguna " + terpilih.getNama() + "?")) {
            return;
        }
        if (penggunaDAO.hapusAman(terpilih.getIdPengguna())) {
            Alerts.info("Pengguna berhasil dihapus.");
            bersihkan();
            muatUlang();
        }
    }

    @FXML
    private void onBersihkan(ActionEvent event) {
        bersihkan();
    }

    private boolean validasiUmum(boolean wajibPassword) {
        if (teks(tfNama).isEmpty() || teks(tfNimNip).isEmpty() || teks(tfEmail).isEmpty()) {
            Alerts.warning("Nama, NIM/NIP, dan email wajib diisi.");
            return false;
        }
        if (!teks(tfEmail).matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            Alerts.warning("Format email tidak valid.");
            return false;
        }
        if (wajibPassword && (pfPassword.getText() == null || pfPassword.getText().length() < 6)) {
            Alerts.warning("Password minimal 6 karakter.");
            return false;
        }
        if (!wajibPassword && pfPassword.getText() != null && !pfPassword.getText().isEmpty()
                && pfPassword.getText().length() < 6) {
            Alerts.warning("Password baru minimal 6 karakter atau kosongkan.");
            return false;
        }
        if (cbRole.getValue() == null) {
            Alerts.warning("Peran pengguna wajib dipilih.");
            return false;
        }
        if (!Role.ADMIN.equals(cbRole.getValue()) && prodiTerpilih() == null) {
            Alerts.warning("Program studi wajib dipilih untuk Mahasiswa atau Dosen.");
            return false;
        }
        return true;
    }

    private Integer prodiTerpilih() {
        ProgramStudi prodi = cbProdi.getValue();
        return prodi == null ? null : prodi.getIdProdi();
    }

    private ProgramStudi cariProdi(Integer idProdi) {
        if (idProdi == null) {
            return null;
        }
        for (ProgramStudi prodi : cbProdi.getItems()) {
            if (prodi.getIdProdi() == idProdi) {
                return prodi;
            }
        }
        return null;
    }

    private void bersihkan() {
        tfNama.clear();
        tfNimNip.clear();
        tfEmail.clear();
        pfPassword.clear();
        cbRole.setValue(Role.MAHASISWA);
        cbProdi.setDisable(false);
        cbProdi.setValue(null);
        tvPengguna.getSelectionModel().clearSelection();
    }

    private static String roleDari(Pengguna pengguna) {
        if (pengguna instanceof Admin) {
            return Role.ADMIN;
        }
        if (pengguna instanceof Dosen) {
            return Role.DOSEN;
        }
        if (pengguna instanceof Mahasiswa) {
            return Role.MAHASISWA;
        }
        return "-";
    }

    private static String teks(TextField field) {
        return field.getText() == null ? "" : field.getText().trim();
    }
}
