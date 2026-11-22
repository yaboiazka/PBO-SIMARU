# SIMARU - Sistem Manajemen Ruangan

Aplikasi desktop JavaFX + MySQL untuk mengelola peminjaman ruangan kampus. Tersedia tiga peran: **Admin** (verifikasi, kelola data, laporan), **Mahasiswa**, dan **Dosen** (ajukan peminjaman, pantau status, lihat jadwal dan ketersediaan ruangan).

README ini menggantikan panduan lama. Semua langkah di bawah mengikuti kode yang ada saat ini.

## Daftar Isi

1. [Prasyarat](#1-prasyarat)
2. [Struktur Proyek](#2-struktur-proyek)
3. [Setup Database](#3-setup-database)
4. [Konfigurasi Kredensial Database](#4-konfigurasi-kredensial-database)
5. [Menjalankan Aplikasi](#5-menjalankan-aplikasi)
6. [Akun Uji](#6-akun-uji)
7. [Fitur dan Alur Singkat](#7-fitur-dan-alur-singkat)
8. [Data Uji Manual](#8-data-uji-manual)
9. [Troubleshooting](#9-troubleshooting)
10. [Catatan Pengembangan](#10-catatan-pengembangan)

---

## 1. Prasyarat

| Kebutuhan | Keterangan |
|---|---|
| JDK | 21 disarankan (minimal 11, sesuai `pom.xml`) |
| Maven | 3.6 atau lebih baru |
| MySQL / MariaDB | Server berjalan di `localhost:3306` |
| IDE (opsional) | IntelliJ IDEA, VS Code dengan Java Extension Pack, atau NetBeans |

Cek versi:

```bash
java -version
mvn -version
```

Pada Windows, pastikan `JAVA_HOME` mengarah ke folder JDK, bukan JRE.

---

## 2. Struktur Proyek

```text
PBO-SIMARU/
├── database/
│   ├── schema.sql                     # Skema + data awal (jalankan sekali)
│   └── dev/seed_uji_bagian_saya.sql   # Data uji manual (opsional)
├── docs/                              # Dokumentasi tiap fitur
├── src/main/java/
│   ├── module-info.java
│   └── com/simaru/
│       ├── App.java                   # Entry point JavaFX
│       ├── controller/                # Controller FXML
│       ├── dao/                       # Database, DAO, TestKoneksi
│       ├── model/                     # Pengguna, Ruangan, Peminjaman, dll.
│       └── util/                      # Session, Navigator, Alerts, Hash, Status
├── src/main/resources/com/simaru/
│   ├── css/                           # Stylesheet
│   └── view/                          # File FXML
├── pom.xml
└── README.md
```

---

## 3. Setup Database

### 3.1 Kloning repositori

```bash
git clone https://github.com/yaboiazka/PBO-SIMARU.git
cd PBO-SIMARU
```

> Jika akan meninjau fitur terbaru, gunakan branch `feature/penyelesaian-semua-fitur`. Untuk pekerjaan bersama, gunakan branch fitur masing-masing, jangan langsung ke `main`.

### 3.2 Impor skema

Pastikan server MySQL aktif, lalu jalankan:

```bash
mysql -u root -p < database/schema.sql
```

Alternatif:

- **phpMyAdmin (XAMPP)**: buka `http://localhost/phpmyadmin`, pilih tab **Import**, pilih `database/schema.sql`, klik **Import**.
- **DBeaver / MySQL Workbench**: buka file `database/schema.sql`, jalankan seluruh skrip (**Execute Script**).

Skrip membuat database `manajemen_ruangan` beserta tabel `program_studi`, `pengguna`, `ruangan`, `peminjaman`, `verifikasi`, dan `jadwal`, lengkap dengan data awal.

> `schema.sql` memakai `CREATE TABLE` tanpa `DROP`, jadi impor ulang pada database yang sudah ada akan gagal. Untuk memulai dari nol (database lokal saja):
>
> ```bash
> mysql -u root -p -e "DROP DATABASE IF EXISTS manajemen_ruangan;"
> mysql -u root -p < database/schema.sql
> ```

---

## 4. Konfigurasi Kredensial Database

Aplikasi **membaca kredensial dari**:

```text
src/main/java/com/simaru/dao/Database.java
```

Sesuaikan `USER` dan `PASS` dengan MySQL di komputer Anda:

```java
private static final String URL = "jdbc:mysql://localhost:3306/manajemen_ruangan";
private static final String USER = "root";
private static final String PASS = "";   // sesuaikan password MySQL lokal
```

`TestKoneksi` sekarang memakai `Database.getConnection()`, jadi cukup satu tempat konfigurasi ini. Untuk XAMPP bawaan biasanya `USER = "root"` dan `PASS = ""`.

> Jangan commit password MySQL pribadi. Jika mengubah `Database.java`, periksa kembali `git diff` sebelum commit.

---

## 5. Menjalankan Aplikasi

### 5.1 Kompilasi

```bash
mvn -q clean compile
```

### 5.2 Tes koneksi (opsional)

Jalankan `com.simaru.dao.TestKoneksi` dari IDE (klik kanan file, **Run**). Jika berhasil:

```text
Mencoba koneksi ke database...
KONEKSI BERHASIL! Aplikasi sudah terhubung ke MySQL.
```

Atau lewat Maven:

```bash
mvn -q compile exec:java -Dexec.mainClass="com.simaru.dao.TestKoneksi"
```

### 5.3 Jalankan GUI

```bash
mvn clean javafx:run
```

Atau jalankan `com.simaru.App` langsung dari IDE. Halaman pertama yang muncul adalah **Login**.

---

## 6. Akun Uji

Data awal dari `database/schema.sql`:

| Peran | Nama | NIM/NIP | Email | Password |
|---|---|---|---|---|
| ADMIN | Administrator | `ADM001` | `admin@email.com` | `admin123` |
| MAHASISWA | Ida Bagus Rama Baskarayana | `2508561002` | `rama.baskarayana@email.com` | `rama12345` |
| MAHASISWA | I Putu Satria Mahatama | `2508561017` | `satria.mahatama@email.com` | `satria12345` |
| MAHASISWA | Azka Nafarrel Kagata | `2508561012` | `kagatazka@email.com` | `Keisuke3S` |
| MAHASISWA | Ida Bagus Gede Juana Praditya | `2508561008` | `gussjuan@email.com` | `juana12345` |
| DOSEN | I Made Satria Bimantara | `198801012015041001` | `satria.bimantara@email.com` | `dosen12345` |

Password disimpan sebagai hash SHA-256, bukan teks biasa.

---

## 7. Fitur dan Alur Singkat

### Mahasiswa / Dosen

1. Login atau registrasi mandiri (akun baru selalu berperan Mahasiswa).
2. **Ajukan Peminjaman**: pilih ruangan `Tersedia`, tanggal, jam, dan keperluan. Status awal `Pending`.
3. **Status Peminjaman**: lihat riwayat, filter status, baca catatan Admin, dan batalkan pengajuan yang masih `Pending`.
4. **Jadwal Ruangan**: lihat pemakaian yang sudah disetujui, filter per tanggal.
5. **Lihat Ruangan**: lihat daftar dan status ruangan.
6. **Logout** dari sidebar.

### Admin

1. Login, lalu masuk ke **Dashboard Admin**.
2. **Verifikasi Peminjaman**: periksa pengajuan, cek bentrok jadwal, setujui atau tolak dengan catatan.
3. **Lihat Jadwal**: jadwal pemakaian yang disetujui.
4. **Kelola Ruangan**: tambah, ubah, hapus ruangan.
5. **Kelola Pengguna**: tambah, ubah, hapus akun Mahasiswa/Dosen/Admin.
6. **Laporan Penggunaan**: filter periode/ruangan/status, lihat ringkasan dan rincian, cetak, ekspor CSV.

Dokumentasi rinci ada di folder [`docs/`](docs/), misalnya:

- [`docs/fitur-login-register.md`](docs/fitur-login-register.md)
- [`docs/fitur-ajukan-peminjaman.md`](docs/fitur-ajukan-peminjaman.md)
- [`docs/fitur-verifikasi-peminjaman.md`](docs/fitur-verifikasi-peminjaman.md)
- [`docs/fitur-laporan-penggunaan.md`](docs/fitur-laporan-penggunaan.md)

---

## 8. Data Uji Manual

Setelah `schema.sql`, seed tambahan dapat dijalankan sekali untuk menyiapkan skenario bentrok dan riwayat verifikasi:

```bash
mysql -u root -p manajemen_ruangan < database/dev/seed_uji_bagian_saya.sql
```

Seed ini menandai datanya dengan awalan `UJI:` dan aman dijalankan ulang karena menghapus data `UJI:` lama lebih dulu. Data ini **bukan** migrasi Flyway dan tidak dijalankan otomatis oleh aplikasi.

---

## 9. Troubleshooting

| Gejala | Penyebab dan solusi |
|---|---|
| `Access denied for user 'root'@'localhost'` | `USER`/`PASS` di `Database.java` tidak cocok dengan MySQL lokal |
| `Communications link failure` | Server MySQL belum berjalan atau port bukan `3306` |
| `Unknown database 'manajemen_ruangan'` | `database/schema.sql` belum diimpor |
| Tabel kosong setelah login | Impor ulang `schema.sql` pada database baru, lalu opsional jalankan seed |
| `JavaFX runtime components are missing` | Jalankan lewat `mvn clean javafx:run`, bukan `java -jar` biasa |
| `Table 'program_studi' doesn't exist` | Versi skema lama masih terpasang; impor `database/schema.sql` terbaru pada database baru |
| Build gagal terkait `module-info` | Pastikan memakai JDK 11+ dan menjalankan Maven dari root proyek |
| Perubahan tidak terlihat | Jalankan `mvn clean compile` lalu ulangi `mvn javafx:run` |

Jika masih gagal, jalankan:

```bash
mvn -e clean compile
```

lalu kirim pesan error lengkap ke tim.

---

## 10. Catatan Pengembangan

- Branch kerja: `feature/...`; jangan push langsung ke `main`.
- Format commit: `feat: ...`, `fix: ...`, `docs: ...`, `refactor: ...`.
- Jangan commit `target/`, `.idea/`, `*.iml`, atau kredensial database.
- Perubahan skema database harus disetujui tim dan dibuat sebagai file migrasi baru, bukan dengan mengedit `schema.sql` yang sudah dipakai bersama.
- Sebelum membuat Pull Request, jalankan `mvn -q clean compile` dan uji alur utama lewat GUI.
