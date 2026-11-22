# Fitur: Lihat Jadwal Penggunaan Ruangan

## 1. Tujuan dan aktor

- **Mahasiswa, Dosen, dan Admin** melihat jadwal pemakaian ruangan yang sudah disetujui.
- Filter tanggal membantu mengecek ketersediaan sebelum mengajukan.

## 2. Alur penggunaan

1. Buka menu **Jadwal Ruangan** dari dashboard.
2. Tabel menampilkan seluruh jadwal terurut tanggal dan jam.
3. Pilih tanggal lalu tekan **Tampilkan** untuk memfilter satu hari.
4. Tekan **Semua Tanggal** untuk menampilkan kembali seluruh jadwal.
5. Label total menunjukkan jumlah baris yang sedang ditampilkan.

## 3. Kelas yang terlibat

| Kelas | Package | Peran |
|---|---|---|
| `LihatJadwalController` | `com.simaru.controller` | Memuat dan memformat jadwal |
| `PeminjamanDAO` | `com.simaru.dao` | Query `jadwal(LocalDate)` |
| `Peminjaman` | `com.simaru.model` | Objek baris jadwal |

## 4. Query SQL utama

```sql
SELECT p.id_peminjaman, p.id_pengguna, p.id_ruangan, u.nama AS nama_pengguna,
       r.nama_ruangan, j.tanggal AS tanggal, j.jam_mulai, j.jam_selesai,
       p.keperluan, p.status_verifikasi AS status, NULL AS catatan
FROM jadwal j
JOIN peminjaman p ON p.id_peminjaman = j.id_peminjaman
JOIN pengguna u ON u.id_pengguna = p.id_pengguna
JOIN ruangan r ON r.id_ruangan = j.id_ruangan
WHERE j.tanggal = ?          -- dihilangkan bila filter "Semua Tanggal"
ORDER BY j.jam_mulai;
```

Baris `jadwal` hanya dibuat saat Admin menyetujui pengajuan, sehingga tabel ini otomatis hanya berisi pemakaian yang sah.

## 5. Aturan bisnis dan penanganan error

| Aturan | Perilaku |
|---|---|
| Tanpa filter | Seluruh jadwal ditampilkan |
| Filter tanggal kosong | Diperlakukan sebagai "Semua Tanggal" |
| Tidak ada data | Placeholder "Belum ada jadwal..." atau "Tidak ada jadwal pada tanggal tersebut." |
| Gagal query | Alert "Gagal memuat data jadwal" |

## 6. Konsep PBO yang diterapkan

- **Enkapsulasi**: data dibaca lewat getter `Peminjaman`.
- **Abstraksi**: controller tidak menulis SQL; query berada di DAO.
- **Asosiasi**: satu baris jadwal menghubungkan peminjaman, ruangan, dan pengguna melalui hasil join.

## 7. Hasil pengujian

| ID uji | Hasil | Catatan |
|---|---|---|
| T-J1 tampil semua | Belum dijalankan | Hanya pengajuan disetujui yang muncul |
| T-J2 filter tanggal | Belum dijalankan | Hanya jadwal tanggal terpilih |
| T-J3 tanpa data | Belum dijalankan | Placeholder tampil |
| T-J4 kompilasi | Lulus | `mvn -q clean compile` tanpa error |

## 8. Tangkapan layar

(placeholder: `docs/img/jadwal-ruangan-01.png` — diisi manusia)

## 9. Keterbatasan dan asumsi

- Halaman belum menampilkan penanda bentrok; pengecekan dilakukan pada verifikasi.
- Format tanggal `dd-MM-yyyy` dan jam `HH:mm` hanya untuk tampilan; database tetap menyimpan format standar.
