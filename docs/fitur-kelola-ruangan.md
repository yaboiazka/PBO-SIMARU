# Fitur: Kelola Ruangan

## 1. Tujuan dan aktor

- **Admin** mengelola master data ruangan: menambah, mengubah, dan menghapus.
- Data ini dipakai form pengajuan, halaman ketersediaan, jadwal, dan laporan.

## 2. Alur penggunaan

1. Login sebagai Admin, pilih menu **Kelola Ruangan**.
2. Tabel menampilkan seluruh ruangan.
3. Untuk menambah: isi nama, lokasi, kapasitas, fasilitas, status, lalu tekan **Tambah**.
4. Untuk mengubah: klik baris pada tabel (form terisi otomatis), ubah data, lalu tekan **Ubah**.
5. Untuk menghapus: pilih baris, tekan **Hapus**, dan setujui konfirmasi.
6. **Bersihkan Form** mengosongkan isian dan pilihan tabel.

## 3. Kelas yang terlibat

| Kelas | Package | Peran |
|---|---|---|
| `KelolaRuanganController` | `com.simaru.controller` | Validasi form dan aksi CRUD |
| `RuanganDAO` | `com.simaru.dao` | `tambahAman`, `ubahAman`, `hapusAman`, `tampilkan` |
| `Ruangan` | `com.simaru.model` | Objek ruangan |
| `Status` | `com.simaru.util` | Konstanta status ruangan |

## 4. Query SQL utama

```sql
-- Daftar ruangan
SELECT * FROM ruangan ORDER BY nama_ruangan;

-- Tambah
INSERT INTO ruangan (nama_ruangan, lokasi, kapasitas, fasilitas, status)
VALUES (?, ?, ?, ?, ?);

-- Ubah
UPDATE ruangan SET nama_ruangan = ?, lokasi = ?, kapasitas = ?,
                   fasilitas = ?, status = ? WHERE id_ruangan = ?;

-- Hapus
DELETE FROM ruangan WHERE id_ruangan = ?;
```

## 5. Aturan bisnis dan penanganan error

| Aturan | Perilaku |
|---|---|
| Hanya Admin | Pesan "Halaman ini hanya untuk Admin." |
| Nama, lokasi, status wajib | Pesan "Nama, lokasi, dan status ruangan wajib diisi." |
| Kapasitas bilangan bulat > 0 | Pesan "Kapasitas harus berupa angka bulat." atau "Kapasitas harus lebih dari 0." |
| Ubah/hapus butuh baris terpilih | Pesan "Pilih salah satu ruangan terlebih dahulu." |
| Hapus harus dikonfirmasi | Dialog konfirmasi menyebut nama ruangan |
| Ruangan masih dipakai peminjaman | "Ruangan tidak bisa dihapus karena masih dipakai pada data peminjaman." |
| Status valid | Pilihan dibatasi `Tersedia`, `Dipakai`, `Perbaikan` |

## 6. Konsep PBO yang diterapkan

- **Enkapsulasi**: seluruh atribut `Ruangan` diakses lewat getter/setter.
- **Abstraksi**: `RuanganDAO implements CrudOperation<Ruangan>`.
- **Overloading**: `tampilkan()` dan `filter(String)` menyajikan daftar dengan dua kebutuhan berbeda.
- **Konstanta terpusat**: status ruangan memakai `Status`.

## 7. Hasil pengujian

| ID uji | Hasil | Catatan |
|---|---|---|
| T-KR1 tambah ruangan | Belum dijalankan | Baris baru muncul di tabel |
| T-KR2 ubah ruangan | Belum dijalankan | Perubahan tersimpan |
| T-KR3 hapus ruangan tanpa relasi | Belum dijalankan | Baris hilang |
| T-KR4 hapus ruangan terpakai | Belum dijalankan | Ditolak dengan pesan ramah |
| T-KR5 kapasitas bukan angka | Belum dijalankan | Ditolak validasi |
| T-KR6 kompilasi | Lulus | `mvn -q clean compile` tanpa error |

## 8. Tangkapan layar

(placeholder: `docs/img/kelola-ruangan-01.png` — diisi manusia)

## 9. Keterbatasan dan asumsi

- Penghapusan memakai `DELETE` biasa; jika ruangan masih direferensikan `peminjaman`, database menolak dan aplikasi menampilkan pesan.
- Belum ada pencarian nama ruangan; daftar diurutkan berdasarkan nama.
- Perubahan status ruangan tidak otomatis mengubah status peminjaman yang sudah ada.
