# Fitur: Ajukan Peminjaman Ruangan

## 1. Tujuan dan aktor

- **Mahasiswa/Dosen** mengajukan pemakaian ruangan untuk tanggal, jam, dan keperluan tertentu.
- Pengajuan baru selalu berstatus `Pending` dan menunggu verifikasi Admin.

## 2. Alur penggunaan

1. Login sebagai Mahasiswa/Dosen, pilih menu **Ajukan Peminjaman**.
2. Pilih ruangan dari daftar yang berstatus `Tersedia`.
3. Pilih tanggal (tanggal lampau dinonaktifkan).
4. Isi jam mulai dan jam selesai dengan format `HH:mm`, serta keperluan.
5. Tekan **Ajukan Peminjaman**.
6. Jika valid, data tersimpan berstatus `Pending`, form dikosongkan, dan pesan sukses tampil.
7. Admin akan memeriksa bentrok jadwal saat verifikasi.

## 3. Kelas yang terlibat

| Kelas | Package | Peran |
|---|---|---|
| `AjukanPeminjamanController` | `com.simaru.controller` | Validasi input, memuat ruangan tersedia, menyimpan pengajuan |
| `PeminjamanDAO` | `com.simaru.dao` | Method `ajukan(...)` menyimpan pengajuan baru |
| `RuanganDAO` | `com.simaru.dao` | `tampilkanTersedia()` memasok pilihan ruangan |
| `Peminjaman`, `Ruangan` | `com.simaru.model` | Objek domain |
| `Session` | `com.simaru.util` | Sumber id pengguna yang mengajukan |
| `Status` | `com.simaru.util` | Konstanta `Pending` dan `Tersedia` |

## 4. Query SQL utama

```sql
-- Pilihan ruangan pada form
SELECT * FROM ruangan WHERE status = 'Tersedia' ORDER BY nama_ruangan;

-- Simpan pengajuan baru
INSERT INTO peminjaman (id_pengguna, id_ruangan, tanggal_pinjam, jam_mulai,
                        jam_selesai, keperluan, status_verifikasi)
VALUES (?, ?, ?, ?, ?, ?, 'Pending');
```

`id_pengguna` diambil dari `Session.getCurrent()`, tidak pernah dari input pengguna.

## 5. Aturan bisnis dan penanganan error

| Aturan | Perilaku |
|---|---|
| Hanya Mahasiswa/Dosen | Pesan "Halaman ini untuk Mahasiswa dan Dosen." dan form tidak dipakai |
| Ruangan, tanggal, keperluan wajib | Pesan "Ruangan, tanggal, dan keperluan wajib diisi." |
| Format jam `HH:mm` | Pesan "Format jam harus HH:mm, contoh 08:00." |
| Jam selesai harus setelah jam mulai | Pesan "Jam selesai harus setelah jam mulai." |
| Ruangan harus masih `Tersedia` | Pesan "Ruangan tidak tersedia untuk diajukan." |
| Tanggal lampau | Dinonaktifkan pada `DatePicker` |
| Database tidak tersedia | Pesan koneksi, data tidak dikirim |

## 6. Konsep PBO yang diterapkan

- **Enkapsulasi**: data pengajuan diakses lewat getter/setter `Peminjaman` dan `Ruangan`.
- **Abstraksi**: controller tidak menulis SQL; seluruh akses data lewat DAO.
- **Polimorfisme**: pemeriksaan peran memakai `Session.isAdmin()` yang berbasis tipe turunan `Pengguna`.
- **Konstanta terpusat**: status memakai `Status.PENDING`/`Status.TERSEDIA`.

## 7. Hasil pengujian

| ID uji | Hasil | Catatan |
|---|---|---|
| T-P1 ajukan valid | Belum dijalankan | Baris `peminjaman` baru berstatus Pending |
| T-P2 jam selesai lebih awal | Belum dijalankan | Ditolak validasi |
| T-P3 format jam salah | Belum dijalankan | Ditolak validasi |
| T-P4 ruangan perbaikan | Belum dijalankan | Tidak muncul di pilihan |
| T-P5 admin membuka halaman | Belum dijalankan | Ditolak |
| T-P6 kompilasi | Lulus | `mvn -q clean compile` tanpa error |

## 8. Tangkapan layar

(placeholder: `docs/img/ajukan-peminjaman-01.png` — diisi manusia)

## 9. Keterbatasan dan asumsi

- Bentrok jadwal tidak dicek saat pengajuan; pengecekan dilakukan Admin pada transaksi verifikasi.
- Pengajuan ganda untuk ruangan dan jam yang sama masih dapat dikirim; Admin yang memutuskan.
- Daftar ruangan difilter hanya `Tersedia`, mengikuti teks pada form.
