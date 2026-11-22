# Fitur: Kelola Pengguna

## 1. Tujuan dan aktor

- **Admin** mengelola akun pengguna: menambah, mengubah, dan menghapus.
- Peran yang dikelola meliputi `MAHASISWA`, `DOSEN`, dan `ADMIN`, lengkap dengan NIM/NIP dan program studi.

## 2. Alur penggunaan

1. Login sebagai Admin, pilih menu **Kelola Pengguna**.
2. Tabel menampilkan nama, NIM/NIP, email, peran, dan program studi.
3. Untuk menambah: isi form (password wajib minimal 6 karakter), pilih peran dan program studi, lalu tekan **Tambah**.
4. Untuk mengubah: klik baris (form terisi, password dikosongkan), ubah data, lalu tekan **Ubah**. Password kosong berarti password lama dipertahankan.
5. Untuk menghapus: pilih baris, tekan **Hapus**, dan setujui konfirmasi. Akun yang sedang dipakai tidak dapat dihapus.
6. Memilih peran `ADMIN` menonaktifkan pilihan program studi.

## 3. Kelas yang terlibat

| Kelas | Package | Peran |
|---|---|---|
| `KelolaPenggunaController` | `com.simaru.controller` | Validasi form dan aksi CRUD |
| `PenggunaDAO` | `com.simaru.dao` | `tampilkan`, `tambahLengkap`, `ubahLengkap`, `hapusAman` |
| `ProgramStudiDAO` | `com.simaru.dao` | Pilihan program studi |
| `Pengguna`, `Admin`, `Mahasiswa`, `Dosen` | `com.simaru.model` | Objek pengguna sesuai peran |
| `ProgramStudi` | `com.simaru.model` | Objek program studi |
| `Role` | `com.simaru.util` | Konstanta peran |

## 4. Query SQL utama

```sql
-- Daftar pengguna beserta program studi
SELECT p.*, ps.fakultas, ps.program_studi
FROM pengguna p
LEFT JOIN program_studi ps ON p.id_prodi = ps.id_prodi;

-- Tambah pengguna
INSERT INTO pengguna (nama, nim_nip, email, password, role, id_prodi)
VALUES (?, ?, ?, ?, ?, ?);

-- Ubah tanpa ganti password
UPDATE pengguna SET nama = ?, nim_nip = ?, email = ?, role = ?, id_prodi = ?
WHERE id_pengguna = ?;

-- Ubah dengan password baru
UPDATE pengguna SET nama = ?, nim_nip = ?, email = ?, password = ?,
                    role = ?, id_prodi = ? WHERE id_pengguna = ?;

-- Hapus
DELETE FROM pengguna WHERE id_pengguna = ?;
```

Nilai `password` sudah berupa hash dari `Hash.hashSHA256` di aplikasi, sama dengan proses login.

## 5. Aturan bisnis dan penanganan error

| Aturan | Perilaku |
|---|---|
| Hanya Admin | Pesan "Halaman ini hanya untuk Admin." |
| Nama, NIM/NIP, email wajib | Pesan "Nama, NIM/NIP, dan email wajib diisi." |
| Format email valid | Pesan "Format email tidak valid." |
| Password tambah minimal 6 karakter | Pesan "Password minimal 6 karakter." |
| Password ubah boleh kosong | Password lama dipertahankan |
| Mahasiswa/Dosen wajib punya prodi | Pesan "Program studi wajib dipilih..." |
| Email/NIM/NIP unik | "Email atau NIM/NIP sudah terdaftar." / "...sudah dipakai pengguna lain." |
| Hapus akun sendiri | "Tidak dapat menghapus akun yang sedang digunakan." |
| Pengguna punya relasi peminjaman/verifikasi | "Pengguna tidak bisa dihapus karena memiliki data peminjaman atau verifikasi." |

## 6. Konsep PBO yang diterapkan

- **Enkapsulasi**: data pengguna dan program studi diakses lewat getter/setter.
- **Pewarisan dan polimorfisme**: objek `Pengguna` dipetakan ke `Admin`, `Mahasiswa`, atau `Dosen`; peran tabel ditentukan dengan `instanceof`.
- **Abstraksi**: `PenggunaDAO implements CrudOperation<Pengguna>`.
- **Konstanta terpusat**: peran memakai `Role`, bukan literal tersebar.

## 7. Hasil pengujian

| ID uji | Hasil | Catatan |
|---|---|---|
| T-KP1 tambah mahasiswa | Belum dijalankan | Baris baru muncul, role MAHASISWA |
| T-KP2 tambah dosen | Belum dijalankan | Prodi wajib dipilih |
| T-KP3 tambah admin | Belum dijalankan | Prodi tersimpan NULL |
| T-KP4 ubah tanpa password | Belum dijalankan | Password lama tetap bisa dipakai |
| T-KP5 ubah dengan password | Belum dijalankan | Password baru berlaku untuk login |
| T-KP6 hapus akun sendiri | Belum dijalankan | Ditolak |
| T-KP7 hapus pengguna berelasi | Belum dijalankan | Ditolak dengan pesan ramah |
| T-KP8 kompilasi | Lulus | `mvn -q clean compile` tanpa error |

## 8. Tangkapan layar

(placeholder: `docs/img/kelola-pengguna-01.png` — diisi manusia)

## 9. Keterbatasan dan asumsi

- Password hanya di-hash SHA-256 tanpa salt, mengikuti skema dan data yang sudah ada.
- Belum ada pencarian atau pagination; seluruh pengguna dimuat sekaligus.
- Peran di tabel dihitung dari tipe objek hasil pemetaan DAO.
- Program studi harus sudah tersedia pada tabel `program_studi` (data awal skema).
