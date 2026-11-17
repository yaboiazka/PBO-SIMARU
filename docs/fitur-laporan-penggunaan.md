# Fitur: Laporan Penggunaan Ruangan

## 1. Tujuan dan aktor
Admin menampilkan ringkasan dan rincian peminjaman pada periode tertentu,
lalu mencetaknya atau mengekspornya ke CSV.

## 2. Alur penggunaan
1. Admin membuka `com/simaru/view/LaporanPenggunaan.fxml`; controller memeriksa `Session.isAdmin()`.
2. Isi **Dari**, **Sampai**, pilih **Ruangan** (Semua/tertentu) dan **Status**
   (default Disetujui).
3. Tekan **Tampilkan**. Validasi: tanggal wajib, `Dari <= Sampai`.
4. Kartu total, tabel ringkasan per ruangan, dan tabel rincian terisi.
5. **Cetak** membuka dialog printer untuk node laporan khusus (bukan `TableView` mentah).
6. **Ekspor CSV** menyimpan file UTF-8 dengan BOM dan pemisah `;`.

## 3. Kelas yang terlibat
| Kelas | Package | Peran |
|---|---|---|
| `LaporanPenggunaanController` | `com.simaru.controller` | Filter, validasi, kartu, cetak, ekspor |
| `LaporanDAO` | `com.simaru.dao` | Query ringkasan per ruangan dan rincian |
| `RingkasanRuangan` | `com.simaru.model` | DTO ringkasan (total, disetujui, ditolak, pending, jam) |
| `Peminjaman` | `com.simaru.model` | Baris rincian (peminjam, peran, jam, status) |
| `LaporanExporter` | `com.simaru.util` | Penulisan CSV dan pencetakan |
| `Status`, `StatusCell` | `com.simaru.util` | Konstanta dan pewarnaan status |

## 4. Query SQL utama
- Ringkasan: `SELECT r.id_ruangan, r.nama_ruangan, COUNT(*) ... SUM(CASE WHEN status = ? ...) ..., ROUND(SUM(CASE WHEN disetujui THEN TIME_TO_SEC(TIMEDIFF(jam_selesai, jam_mulai)) ELSE 0 END)/3600, 2) ... FROM peminjaman p JOIN ruangan r ... WHERE p.tanggal_pinjam BETWEEN ? AND ? [AND r.id_ruangan = ?] GROUP BY ... ORDER BY total_jam_terpakai DESC`.
- Rincian: `SELECT p.id_peminjaman, r.nama_ruangan, u.nama, u.role, p.tanggal_pinjam, p.jam_mulai, p.jam_selesai, p.keperluan, p.status_verifikasi FROM peminjaman p JOIN ruangan r ... JOIN pengguna u ... WHERE p.tanggal_pinjam BETWEEN ? AND ? [AND p.id_ruangan = ?] [AND p.status_verifikasi = ?] ORDER BY p.tanggal_pinjam, p.jam_mulai`.
- Filter ruangan dan status disusun dari potongan query tetap; semua nilai lewat `?`.
- Ringkasan tidak terpengaruh filter status; filter status hanya untuk rincian.

## 5. Aturan bisnis dan penanganan error
- Periode memakai `tanggal_pinjam` (tanggal pemakaian), bukan `tanggal_pengajuan`.
- `Dari` > `Sampai` → "Tanggal 'Dari' tidak boleh melewati tanggal 'Sampai'."
- Tanggal kosong → "Tanggal 'Dari' dan 'Sampai' wajib diisi."
- Tidak ada data → "Tidak ada data peminjaman pada periode ini."
- Ekspor gagal → "Gagal menyimpan file. Periksa lokasi dan izin penyimpanan."
- CSV: UTF-8 + BOM, pemisah `;`, nilai berisi `;`, `"`, atau baris baru dikutip ganda.
- Cetak memakai `javafx.print.PrinterJob` pada `VBox` laporan, bukan `TableView`.

## 6. Konsep PBO yang diterapkan
- Enkapsulasi: atribut `RingkasanRuangan` dan `Peminjaman` private.
- DTO sebagai objek: `RingkasanRuangan` dipetakan dari query agregasi.
- Polimorfisme/abstraksi: otorisasi lewat `Session.isAdmin()`.
- DAO baca-saja dipisahkan (`LaporanDAO`) dari controller dan util ekspor (`LaporanExporter`).

## 7. Hasil pengujian
| ID uji | Hasil | Catatan |
|---|---|---|
| Kompilasi `mvn -q compile` | Lulus | Tidak ada error kompilasi |
| T-L1 s.d. T-L6 | Belum dijalankan | Butuh database MySQL lokal + seed uji |

## 8. Tangkapan layar
Placeholder: `docs/img/laporan-penggunaan-01.png`.

## 9. Keterbatasan dan asumsi
- Ekspor PDF tidak dibuat karena memerlukan dependency baru.
- Data uji tersedia di `database/dev/seed_uji_bagian_saya.sql`.
