# 🏢 SIMARU - Sistem Manajemen Ruangan

Aplikasi desktop berbasis **JavaFX** dan **MySQL** untuk mengelola peminjaman ruangan perkuliahan dan fasilitas kampus. Proyek ini dikembangkan menggunakan arsitektur modular Maven dengan pembagian hak akses (Role-Based Access Control) untuk Mahasiswa, Dosen, dan Admin.

---

## 📋 Daftar Isi
1. [Prasyarat Sistem](#-prasyarat-sistem)
2. [Panduan Inisiasi Proyek](#-panduan-inisiasi-proyek)
   - [Langkah 1: Kloning Repositori](#langkah-1-kloning-repositori)
   - [Langkah 2: Menyiapkan Database MySQL](#langkah-2-menyiapkan-database-mysql)
   - [Langkah 3: Konfigurasi Kredensial Database](#langkah-3-konfigurasi-kredensial-database)
   - [Langkah 4: Tes Koneksi Database](#langkah-4-tes-koneksi-database)
   - [Langkah 5: Menjalankan Aplikasi](#langkah-5-menjalankan-aplikasi)
3. [Akun Bawaan (Data Uji Coba)](#-akun-bawaan-data-uji-coba)
4. [Struktur Folder](#-struktur-folder)
5. [Troubleshooting](#-troubleshooting)

---

## ⚙️ Prasyarat Sistem

Sebelum memulai, pastikan perangkat Anda telah terpasang:
- **Java Development Kit (JDK) 11** atau versi lebih baru (misal JDK 17 / 21).
- **Apache Maven 3.6+** (terintegrasi otomatis jika memakai IntelliJ IDEA / VS Code / Eclipse).
- **MySQL Server** (bisa menggunakan XAMPP, MySQL Community Server, atau Docker).
- **IDE Java** yang disarankan: IntelliJ IDEA, Visual Studio Code (dengan Java Extension Pack), atau NetBeans.

---

## 🚀 Panduan Inisiasi Proyek

### Langkah 1: Kloning Repositori
Buka terminal / command prompt, lalu kloning repositori ini ke komputer Anda:
```bash
git clone https://github.com/<username>/sistem_manajemen_ruangan.git
cd sistem_manajemen_ruangan
```

---

### Langkah 2: Menyiapkan Database MySQL

1. Pastikan layanan MySQL sudah aktif:
   - **XAMPP**: Buka XAMPP Control Panel dan klik tombol **Start** pada modul **MySQL**.
   - **MySQL Native/Service**: Pastikan service MySQL berjalan di port `3306`.

2. Eksekusi file SQL yang berada di folder [`database/schema.sql`](database/schema.sql):

   #### Opsi A: Menggunakan Command Line (Terminal)
   ```bash
   mysql -u root -p < database/schema.sql
   ```
   *(Tekan Enter, lalu masukkan password MySQL lokal Anda jika ada).*

   #### Opsi B: Menggunakan phpMyAdmin (XAMPP)
   1. Buka browser dan kunjungi `http://localhost/phpmyadmin`.
   2. Klik tab **Import** pada menu navigasi atas.
   3. Klik tombol **Choose File**, arahkan ke file `database/schema.sql`.
   4. Klik tombol **Import** (atau **Go**) di bagian bawah.
   5. Database `manajemen_ruangan` beserta tabel-tabelnya (`pengguna`, `ruangan`, `peminjaman`, `verifikasi`, `jadwal`) dan data awal akan otomatis dibuat.

   #### Opsi C: Menggunakan DBeaver / MySQL Workbench
   1. Buat koneksi baru ke localhost MySQL.
   2. Buka menu **File** -> **Open File** -> pilih `database/schema.sql`.
   3. Jalankan seluruh skrip SQL (**Execute Script** / `Ctrl + Shift + Enter`).

---

### Langkah 3: Konfigurasi Kredensial Database

Buka berkas konfigurasi / uji koneksi pada:
📁 [`src/main/java/com/simaru/dao/TestKoneksi.java`](src/main/java/com/simaru/dao/TestKoneksi.java)

Sesuaikan konstanta `USER` dan `PASS` dengan pengaturan MySQL di komputer Anda:
```java
public class TestKoneksi {
    private static final String URL = "jdbc:mysql://localhost:3306/manajemen_ruangan";
    private static final String USER = "root";       // Ganti dengan username MySQL Anda
    private static final String PASS = "password_anda"; // Kosongkan ("") jika menggunakan default XAMPP
    ...
}
```
> [!NOTE]
> Jika Anda menggunakan XAMPP secara default, biasanya user adalah `"root"` dan password adalah string kosong `""`.

---

### Langkah 4: Tes Koneksi Database

Sebelum menjalankan antarmuka grafis (GUI), pastikan koneksi antara Java dan MySQL berfungsi dengan baik.

#### Melalui Terminal / Command Prompt:
Jalankan perintah Maven berikut:
```bash
mvn compile exec:java -Dexec.mainClass="com.simaru.dao.TestKoneksi"
```

#### Melalui IDE (IntelliJ IDEA / VS Code / Eclipse):
1. Navigasi ke file `src/main/java/com/simaru/dao/TestKoneksi.java`.
2. Klik kanan pada file atau klik ikon panah hijau **Run** di samping `public static void main(String[] args)`.

#### Output yang Diharapkan:
Jika koneksi sukses, terminal akan menampilkan pesan:
```text
Mencoba koneksi ke database...
✅ KONEKSI BERHASIL! Aplikasi sudah terhubung ke MySQL.
```

Jika muncul pesan `❌ KONEKSI GAGAL!`, silakan periksa kembali kecocokan password MySQL Anda dan pastikan service MySQL sedang aktif.

---

### Langkah 5: Menjalankan Aplikasi

Setelah koneksi database dipastikan berhasil, jalankan aplikasi JavaFX dengan perintah:

```bash
mvn clean javafx:run
```

Atau jalankan kelas utama [`com.simaru.App`](src/main/java/com/simaru/App.java) langsung dari IDE Anda.

---

## 👥 Akun Bawaan (Data Uji Coba)

Database `schema.sql` sudah dilengkapi dengan data awal untuk pengujian login:

| Role | Nama | NIM / NIP | Email | Password |
| :--- | :--- | :--- | :--- | :--- |
| **ADMIN** | Administrator | `ADM001` | `admin@email.com` | `admin123` |
| **MAHASISWA** | Ida Bagus Rama Baskarayana | `2508561002` | `rama.baskarayana@email.com` | `rama12345` |
| **MAHASISWA** | I Putu Satria Mahatama | `2508561017` | `satria.mahatama@email.com` | `satria12345` |
| **MAHASISWA** | Azka Nafarrel Kagata | `2508561012` | `kagatazka@email.com` | `Keisuke3S` |
| **MAHASISWA** | Ida Bagus Gede Juana Praditya | `2508561008` | `gussjuan@email.com` | `juana12345` |
| **DOSEN** | I Made Satria Bimantara | `198801012015041001` | `satria.bimantara@email.com` | `dosen12345` |

> [!TIP]
> Password di dalam database di-enkripsi menggunakan fungsi `SHA2(..., 256)` sesuai standar hashing yang digunakan aplikasi.

---

## 📂 Struktur Folder

```text
sistem_manajemen_ruangan/
├── database/
│   └── schema.sql              # Skrip pembuatan tabel dan data awal (seeding)
├── src/
│   └── main/
│       ├── java/
│       │   ├── module-info.java
│       │   └── com/simaru/
│       │       ├── App.java                   # Entry point aplikasi JavaFX
│       │       ├── controller/                # Controller GUI FXML
│       │       └── dao/
│       │           └── TestKoneksi.java       # Pengujian koneksi JDBC ke MySQL
│       └── resources/
│           └── com/simaru/                    # Berkas FXML dan styling tampilan
├── pom.xml                                    # Konfigurasi Maven & dependensi
└── README.md
```

---

## 🔧 Troubleshooting

- **Error: `CommunicationsException: Communications link failure`**  
  👉 Pastikan layanan MySQL Anda sudah running di port `3306`.
- **Error: `Access denied for user 'root'@'localhost'`**  
  👉 Periksa kembali username dan password yang diisi pada [`TestKoneksi.java`](src/main/java/com/simaru/dao/TestKoneksi.java).
- **Error: `Unknown database 'manajemen_ruangan'`**  
  👉 Skrip [`database/schema.sql`](database/schema.sql) belum dieksekusi di server MySQL Anda. Jalankan kembali [Langkah 2](#langkah-2-menyiapkan-database-mysql).
- **Error: JavaFX runtime components are missing**  
  👉 Pastikan menjalankan aplikasi menggunakan perintah `mvn clean javafx:run` atau konfigurasi run configuration di IDE dengan modul JavaFX yang sesuai.
