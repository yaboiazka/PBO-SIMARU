# Fitur: Verifikasi Peminjaman

## 1. Tujuan dan aktor
Admin meninjau pengajuan `Pending`, memeriksa bentrok jadwal, lalu menyetujui
(disertai pembuatan `jadwal`) atau menolak dengan catatan wajib.

## 2. Alur penggunaan
1. Admin login dan membuka `com/simaru/view/VerifikasiPeminjaman.fxml`.
2. Controller memastikan `Session.isAdmin()`; jika bukan, data tidak dimuat.
3. Tab **Menunggu Verifikasi** menampilkan pengajuan Pending, terlama dulu.
4. Memilih baris langsung menjalankan cek bentrok. Jika bentrok, peringatan tampil
   dan tombol **Setujui** dinonaktifkan; tombol **Tolak** tetap aktif.
5. Isi catatan (wajib untuk Tolak), tekan **Setujui** atau **Tolak**, lalu konfirmasi.
6. Setelah berhasil, catatan dikosongkan dan kedua tab dimuat ulang.
7. Tab **Riwayat Verifikasi** menampilkan pengajuan yang sudah diproses beserta admin,
   catatan, dan waktu verifikasi, dengan filter status.

## 3. Kelas yang terlibat
| Kelas | Package | Peran |
|---|---|---|
| `VerifikasiPeminjamanController` | `com.simaru.controller` | Logika dua tab, detail, aksi verifikasi |
| `VerifikasiDAO` | `com.simaru.dao` | Query menunggu/riwayat, cek bentrok, transaksi setujui/tolak |
| `Verifikasi` | `com.simaru.model` | Asosiasi `Peminjaman` + `Admin`, status, catatan, waktu |
| `Peminjaman` | `com.simaru.model` | Data pengajuan dan informasi ruangan/pemohon |
| `Alerts`, `Session`, `Status`, `StatusCell` | `com.simaru.util` | Dialog, sesi admin, konstanta, pewarnaan |

## 4. Query SQL utama
- Menunggu: `SELECT p.*, u.nama, u.nim_nip, u.role, r.nama_ruangan, r.lokasi, r.kapasitas, r.status ... WHERE p.status_verifikasi = 'Pending' ORDER BY p.tanggal_pengajuan ASC`.
- Riwayat: join `verifikasi` → `peminjaman` → `pengguna` (pemohon) → `ruangan` → `pengguna` (admin), filter status, urut `tanggal_verifikasi DESC`.
- Cek bentrok: `... FROM jadwal j ... WHERE j.id_ruangan = ? AND j.tanggal = ? AND j.jam_mulai < ? AND j.jam_selesai > ?` (rentang yang menempel bukan bentrok).
- Transaksi setujui:
  1. `SELECT id_ruangan FROM peminjaman ...`
  2. `SELECT status FROM ruangan WHERE id_ruangan = ? FOR UPDATE` (serialisasi per ruangan)
  3. `SELECT ... FROM peminjaman WHERE id_peminjaman = ? FOR UPDATE`, cek masih Pending
  4. cek bentrok, validasi `jam_selesai > jam_mulai`
  5. `UPDATE peminjaman ... WHERE id_peminjaman = ? AND status_verifikasi = 'Pending'`
  6. `INSERT INTO verifikasi ... 'Disetujui'`
  7. `INSERT INTO jadwal ...`
- Transaksi tolak: langkah 3, `UPDATE ... 'Ditolak'`, `INSERT verifikasi ... 'Ditolak'`, tanpa jadwal.
- Semua operasi memakai satu `Connection`: `setAutoCommit(false)`, `commit()`, `rollback()` di `catch`, dan `setAutoCommit(true)` di `finally`.

## 5. Aturan bisnis dan penanganan error
- R1: Hanya `Pending` yang dapat diproses; jika sudah diproses → "Pengajuan ini sudah diproses."
- R2: Bentrok bila rentang beririsan; menempel tidak bentrok.
- R3: Ruangan `Perbaikan` tidak boleh disetujui → "Ruangan sedang dalam perbaikan."
- R4/R6: Setujui mengubah peminjaman + insert verifikasi + insert jadwal; Tolak tanpa jadwal.
- R5: `id_admin` dari `Session.getCurrent().getIdPengguna()`.
- R7: `jam_selesai > jam_mulai` divalidasi sebelum update.
- Catatan wajib saat menolak → "Catatan wajib diisi saat menolak pengajuan."
- Pesan bentrok menyebut ruangan, jam, dan pemohon.

## 6. Konsep PBO yang diterapkan
- Enkapsulasi: seluruh atribut `Verifikasi` private.
- Asosiasi: `Verifikasi` menyimpan objek `Peminjaman` dan `Admin`.
- Komposisi: `Verifikasi` tidak bermakna tanpa `Peminjaman` (DB: `ON DELETE CASCADE`).
- Polimorfisme/abstraksi: otorisasi memakai `Session.isAdmin()` (turunan `Pengguna`).
- Interface: `VerifikasiDAO implements CrudOperation<Verifikasi>`.

## 7. Hasil pengujian
| ID uji | Hasil | Catatan |
|---|---|---|
| Kompilasi `mvn -q compile` | Lulus | Tidak ada error kompilasi |
| T-V1 s.d. T-V8 | Belum dijalankan | Butuh database MySQL lokal + seed uji |

## 8. Tangkapan layar
Placeholder: `docs/img/verifikasi-peminjaman-01.png`.

## 9. Keterbatasan dan asumsi
- Dashboard admin belum memiliki controller di branch ini, sehingga halaman diuji
  lewat pemuatan FXML langsung, bukan lewat menu dashboard.
- Data uji tersedia di `database/dev/seed_uji_bagian_saya.sql`.
