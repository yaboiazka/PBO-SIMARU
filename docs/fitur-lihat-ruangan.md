# Fitur: Lihat Ketersediaan Ruangan

## 1. Tujuan dan aktor

- **Mahasiswa, Dosen, dan Admin** melihat daftar ruangan beserta lokasi, kapasitas, fasilitas, dan statusnya.
- Filter status membantu menemukan ruangan yang sedang tersedia atau dalam perbaikan.

## 2. Alur penggunaan

1. Buka menu **Lihat Ruangan** dari dashboard.
2. Pilih filter status: Semua, Tersedia, Dipakai, atau Perbaikan.
3. Daftar langsung diperbarui; tombol **Segarkan** memuat ulang data.
4. Bila tidak ada data, tabel menampilkan pesan yang sesuai.

## 3. Kelas yang terlibat

| Kelas | Package | Peran |
|---|---|---|
| `LihatRuanganController` | `com.simaru.controller` | Menampilkan daftar dan filter |
| `RuanganDAO` | `com.simaru.dao` | `tampilkan()` dan `filter(String)` |
| `Ruangan` | `com.simaru.model` | Objek ruangan |

## 4. Query SQL utama

```sql
-- Semua ruangan
SELECT * FROM ruangan ORDER BY nama_ruangan;

-- Filter status
SELECT * FROM ruangan WHERE status = ? ORDER BY nama_ruangan;
```

## 5. Aturan bisnis dan penanganan error

| Aturan | Perilaku |
|---|---|
| Filter "Semua" | Memakai `tampilkan()` |
| Filter status lain | Memakai `filter(status)` dengan parameter |
| Data kosong | Placeholder sesuai filter |
| Gagal query | Alert "Gagal memuat data ruangan" |

## 6. Konsep PBO yang diterapkan

- **Enkapsulasi**: atribut `Ruangan` diakses lewat getter.
- **Abstraksi**: controller bergantung pada DAO, bukan JDBC langsung.
- **Polimorfisme method**: `filter` memilih jalur query berdasarkan nilai filter.

## 7. Hasil pengujian

| ID uji | Hasil | Catatan |
|---|---|---|
| T-R1 tampil semua | Belum dijalankan | Daftar ruangan muncul |
| T-R2 filter status | Belum dijalankan | Hanya status terpilih |
| T-R3 data kosong | Belum dijalankan | Placeholder tampil |
| T-R4 kompilasi | Lulus | `mvn -q clean compile` tanpa error |

## 8. Tangkapan layar

(placeholder: `docs/img/lihat-ruangan-01.png` — diisi manusia)

## 9. Keterbatasan dan asumsi

- Halaman bersifat baca-saja; perubahan data ruangan dilakukan Admin di menu Kelola Ruangan.
- Status ditampilkan sebagai teks, belum diberi warna khusus.
