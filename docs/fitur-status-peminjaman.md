# Fitur: Status Peminjaman

## 1. Tujuan dan aktor
Mahasiswa dan Dosen melihat seluruh pengajuan peminjaman miliknya, memantau tahap
persetujuan, melihat catatan admin, dan membatalkan pengajuan yang masih `Pending`.
Admin tidak boleh membuka halaman ini.

## 2. Alur penggunaan
1. Pengguna login, lalu dashboard membuka `com/simaru/view/StatusPeminjaman.fxml`.
2. Controller memeriksa `Session.getCurrent()`; jika kosong atau `Admin`, data tidak dimuat.
3. Pengguna melihat ringkasan jumlah per status dan tabel pengajuan miliknya (terbaru dulu).
4. Pengguna memilih filter status (Semua/Pending/Disetujui/Ditolak) atau menekan **Muat Ulang**.
5. Memilih baris menampilkan panel detail, termasuk admin pemverifikasi dan waktu verifikasi.
6. Tombol **Batalkan Pengajuan** aktif hanya untuk baris `Pending`; aksi meminta konfirmasi
   lalu menghapus baris `peminjaman`.

## 3. Kelas yang terlibat
| Kelas | Package | Peran |
|---|---|---|
| `StatusPeminjamanController` | `com.simaru.controller` | Logika halaman, filter, detail, pembatalan |
| `PeminjamanDAO.tampilkanMilik` | `com.simaru.dao` | Query daftar milik pengguna (overloading dengan filter status) |
| `PeminjamanDAO.batalkan` | `com.simaru.dao` | Menghapus pengajuan Pending milik pengguna |
| `Peminjaman` | `com.simaru.model` | Model baris (ditambah atribut detail secara append-only) |
| `Session` | `com.simaru.util` | Sumber id pengguna aktif |
| `Status`, `StatusCell` | `com.simaru.util` | Konstanta status dan pewarnaan sel |

## 4. Query SQL utama
- Daftar milik pengguna: `SELECT ... FROM peminjaman p JOIN ruangan r ... LEFT JOIN verifikasi v ... LEFT JOIN pengguna a ... WHERE p.id_pengguna = ? ORDER BY p.tanggal_pengajuan DESC`.
  Subquery mengambil catatan, nama admin, dan waktu verifikasi terbaru agar satu baris tidak duplikat.
- Filter status menambahkan `AND p.status_verifikasi = ?` dengan parameter.
- Pembatalan: `DELETE FROM peminjaman WHERE id_peminjaman = ? AND id_pengguna = ? AND status_verifikasi = 'Pending'`.
  Jumlah baris terdampak 0 berarti pengajuan sudah diproses atau bukan milik pengguna.

## 5. Aturan bisnis dan penanganan error
- Id pengguna hanya dari `Session`, tidak dari input UI.
- Hanya status `Pending` yang bisa dibatalkan; tombol dinonaktifkan untuk status lain.
- Konfirmasi `CONFIRMATION` sebelum menghapus.
- Pesan gagal: "Pengajuan tidak bisa dibatalkan karena sudah diproses atau bukan milik Anda."
- Kondisi kosong: "Belum ada pengajuan peminjaman."

## 6. Konsep PBO yang diterapkan
- Enkapsulasi: atribut `Peminjaman` private dengan getter/setter.
- Polimorfisme: `Session.isAdmin()` memeriksa turunan `Pengguna` (`Admin` vs `Mahasiswa`/`Dosen`).
- Overloading: `tampilkanMilik(int)` dan `tampilkanMilik(int, String)`.
- Pewarnaan status memakai `StatusCell` dengan CSS `.status-pending`, `.status-disetujui`, `.status-ditolak`.

## 7. Hasil pengujian
| ID uji | Hasil | Catatan |
|---|---|---|
| Kompilasi `mvn -q compile` | Lulus | Tidak ada error kompilasi |
| T-S1 s.d. T-S5 | Belum dijalankan | Butuh database MySQL lokal + seed uji |

## 8. Tangkapan layar
Placeholder: `docs/img/status-peminjaman-01.png`.

## 9. Keterbatasan dan asumsi
- Kredensial database mengikuti `com.simaru.dao.Database` yang sudah ada (hardcoded), bukan `config.properties`.
- Navigasi dari dashboard belum diubah karena berada di luar cakupan; halaman dapat dimuat lewat `Navigator.show("StatusPeminjaman", ...)`.
