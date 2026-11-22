# Fitur: Login, Registrasi, dan Logout

## 1. Tujuan dan aktor

- **Mahasiswa/Dosen/Admin** masuk ke aplikasi memakai email dan password.
- **Calon Mahasiswa** mendaftarkan akun sendiri; akun baru otomatis berperan `MAHASISWA`.
- **Semua pengguna** dapat keluar dan kembali ke halaman login.

## 2. Alur penggunaan

1. Pengguna membuka aplikasi, halaman **Login** tampil.
2. Mengisi email dan password, lalu menekan **Login**.
3. Sistem memverifikasi hash password. Jika cocok:
   - role `ADMIN` diarahkan ke **Dashboard Admin**;
   - role `MAHASISWA`/`DOSEN` diarahkan ke **Dashboard Mahasiswa**.
4. Jika gagal, muncul pesan "Email atau password salah." tanpa membocorkan bagian mana yang salah.
5. Dari halaman login, tombol **Register** membuka form registrasi: nama, NIM, email, program studi, password, dan konfirmasi password.
6. Setelah registrasi berhasil, pengguna dikembalikan ke halaman login.
7. Tombol **Logout** pada sidebar dashboard menghapus sesi dan menampilkan kembali halaman login.

## 3. Kelas yang terlibat

| Kelas | Package | Peran |
|---|---|---|
| `loginController` | `com.simaru.controller` | Menangani form login dan navigasi ke dashboard |
| `RegisterController` | `com.simaru.controller` | Validasi form registrasi dan penyimpanan akun |
| `PenggunaDAO` | `com.simaru.dao` | Query login, registrasi, dan pemetaan objek `Pengguna` |
| `ProgramStudiDAO` | `com.simaru.dao` | Mengisi pilihan program studi (baca saja) |
| `ProgramStudi` | `com.simaru.model` | Model program studi |
| `Pengguna`, `Admin`, `Mahasiswa`, `Dosen` | `com.simaru.model` | Objek pengguna sesuai peran (polimorfisme) |
| `Hash` | `com.simaru.util` | Hash SHA-256 password |
| `Session` | `com.simaru.util` | Menyimpan pengguna aktif |
| `Navigator` | `com.simaru.util` | Perpindahan halaman |
| `Role` | `com.simaru.util` | Konstanta `ADMIN`, `DOSEN`, `MAHASISWA` |

## 4. Query SQL utama

```sql
-- Login: cocokkan email dan hash password, ambil data program studi
SELECT p.*, ps.fakultas, ps.program_studi
FROM pengguna p
LEFT JOIN program_studi ps ON p.id_prodi = ps.id_prodi
WHERE p.email = ? AND p.password = ?;

-- Registrasi mandiri (role selalu MAHASISWA)
INSERT INTO pengguna (nama, nim_nip, email, password, role, id_prodi)
VALUES (?, ?, ?, ?, ?, ?);  -- role = 'MAHASISWA', password sudah di-hash

-- Pilihan program studi pada form registrasi
SELECT id_prodi, fakultas, program_studi
FROM program_studi
ORDER BY program_studi;
```

Password tidak pernah disimpan mentah: `Hash.hashSHA256` dipanggil sebelum query login dan insert registrasi.

## 5. Aturan bisnis dan penanganan error

| Aturan | Perilaku |
|---|---|
| Email dan password wajib diisi | Pesan "Email dan password wajib diisi." |
| Email harus berformat valid | Pesan "Format email tidak valid." |
| Password minimal 6 karakter | Pesan "Password minimal 6 karakter." |
| Konfirmasi harus sama | Pesan "Konfirmasi password tidak sama." |
| Program studi wajib dipilih | Pesan "Semua data wajib diisi, termasuk program studi." |
| Email/NIM sudah dipakai | Pesan "Email atau NIM/NIP sudah terdaftar." |
| Database tidak tersedia | Pesan "Tidak dapat terhubung ke database. Pastikan server MySQL berjalan." |

## 6. Konsep PBO yang diterapkan

- **Enkapsulasi**: atribut `Pengguna` dan `ProgramStudi` `private`, diakses lewat getter/setter.
- **Pewarisan**: `Admin`, `Mahasiswa`, dan `Dosen` adalah turunan `Pengguna`.
- **Polimorfisme**: objek hasil login bertipe `Pengguna`; percabangan memakai `instanceof Admin` melalui `Session.isAdmin()`.
- **Abstraksi**: `Pengguna` tetap `abstract`; DAO memilih kelas turunan konkret saat memetakan `ResultSet`.
- **Konstanta terpusat**: string peran memakai `com.simaru.util.Role`, bukan literal tersebar.

## 7. Hasil pengujian

| ID uji | Hasil | Catatan |
|---|---|---|
| T-A1 login valid | Belum dijalankan | Butuh server MySQL dan data pengguna |
| T-A2 password salah | Belum dijalankan | Diharapkan pesan "Email atau password salah." |
| T-A3 register valid | Belum dijalankan | Akun baru berperan `MAHASISWA` |
| T-A4 email/NIM duplikat | Belum dijalankan | Diharapkan pesan duplikat |
| T-A5 logout | Belum dijalankan | Sesi bersih dan kembali ke login |
| T-A6 kompilasi | Lulus | `mvn -q clean compile` tanpa error |

## 8. Tangkapan layar

(placeholder: `docs/img/login-register-01.png` — diisi manusia)

## 9. Keterbatasan dan asumsi

- Registrasi mandiri hanya untuk peran `MAHASISWA`; akun `DOSEN` dan `ADMIN` dibuat melalui halaman kelola pengguna oleh Admin.
- Password di-hash dengan SHA-256 tanpa salt mengikuti skema dan data yang sudah ada. Untuk produksi sebaiknya memakai algoritma khusus password (mis. bcrypt/Argon2).
- Kredensial database masih tertulis di `Database.java`; konfigurasi berbasis file belum diterapkan.
- Login belum membatasi percobaan berulang (rate limiting).
