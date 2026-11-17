# Fitur: Dashboard Admin dan Dashboard Mahasiswa/Dosen

## 1. Tujuan dan aktor

- **Admin** memperoleh satu pintu masuk ke verifikasi peminjaman, jadwal, kelola ruangan, kelola pengguna, dan laporan penggunaan.
- **Mahasiswa/Dosen** memperoleh pintu masuk ke pengajuan peminjaman, status pengajuan, jadwal, dan daftar ruangan.
- Dashboard mempertahankan sidebar saat halaman fitur dimuat, sehingga perpindahan terasa sebagai satu aplikasi.

## 2. Alur penggunaan

1. Setelah login, `loginController` memilih dashboard berdasarkan peran.
2. Halaman dashboard menampilkan nama pengguna pada sidebar dan ringkasan singkat di area konten.
3. Klik menu di sidebar memuat halaman fitur ke area tengah (`BorderPane.setCenter`) tanpa mengganti scene.
4. Tombol **Beranda** memuat ulang dashboard.
5. Tombol **Logout** meminta konfirmasi, menghapus `Session`, lalu menampilkan halaman login.

## 3. Kelas yang terlibat

| Kelas | Package | Peran |
|---|---|---|
| `DashboardAdminController` | `com.simaru.controller` | Otorisasi Admin, navigasi menu admin, logout |
| `DashboardMahasiswaController` | `com.simaru.controller` | Otorisasi non-admin, navigasi menu pengguna |
| `Navigator` | `com.simaru.util` | `show` untuk ganti scene, `loadInto` untuk memuat isi dashboard |
| `Session` | `com.simaru.util` | Sumber identitas pengguna aktif |
| `Alerts` | `com.simaru.util` | Konfirmasi logout dan pesan kesalahan |

## 4. Query SQL utama

Tidak ada query langsung di controller dashboard. Data pengguna berasal dari objek `Pengguna` yang disimpan `Session` setelah login.

## 5. Aturan bisnis dan penanganan error

| Aturan | Perilaku |
|---|---|
| Hanya Admin boleh membuka dashboard admin | Pesan "Halaman ini hanya untuk Admin." lalu kembali ke login |
| Hanya Mahasiswa/Dosen boleh membuka dashboard pengguna | Pesan "Halaman ini untuk Mahasiswa dan Dosen." lalu kembali ke login |
| Logout harus dikonfirmasi | Dialog `CONFIRMATION` sebelum sesi dihapus |
| Halaman gagal dimuat | `Navigator.loadInto` menampilkan alert tanpa menutup dashboard |

## 6. Konsep PBO yang diterapkan

- **Pewarisan dan polimorfisme**: peran ditentukan dari objek turunan `Pengguna` (`Admin` vs `Mahasiswa`/`Dosen`) melalui `Session.isAdmin()`.
- **Enkapsulasi**: nama dan peran diambil lewat getter `Pengguna`.
- **Abstraksi**: controller hanya bergantung pada tipe `Pengguna`, bukan kelas konkret.

## 7. Hasil pengujian

| ID uji | Hasil | Catatan |
|---|---|---|
| T-D1 login admin | Belum dijalankan | Diharapkan Dashboard Admin tampil |
| T-D2 login mahasiswa | Belum dijalankan | Diharapkan Dashboard Mahasiswa tampil |
| T-D3 non-admin membuka dashboard admin | Belum dijalankan | Ditolak dan kembali ke login |
| T-D4 navigasi menu | Belum dijalankan | Halaman termuat di area tengah |
| T-D5 logout | Belum dijalankan | Kembali ke halaman login |
| T-D6 kompilasi | Lulus | `mvn -q clean compile` tanpa error |

## 8. Tangkapan layar

(placeholder: `docs/img/dashboard-01.png` — diisi manusia)

## 9. Keterbatasan dan asumsi

- Halaman fitur yang dimuat ke area tengah harus berupa root layout, bukan `Stage`/`Scene` baru.
- Halaman rekan yang masih menyertakan `nav.fxml` akan tampil ganda sampai dirapikan pada commit fitur terkait.
- Belum ada statistik dashboard dari database; konten awal berupa teks panduan.
