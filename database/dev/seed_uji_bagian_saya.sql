-- Seed data uji manual (BUKAN migrasi Flyway).
-- Jalankan setelah database/schema.sql, sekali secara manual:
--   mysql -u root -p manajemen_ruangan < database/dev/seed_uji_bagian_saya.sql
--
-- Data uji memakai akun dan ruangan yang sudah dibuat schema.sql:
--   admin@email.com / admin123          (ADMIN)
--   rama.baskarayana@email.com / rama12345    (MAHASISWA)
--   satria.mahatama@email.com / satria12345   (MAHASISWA)
--
-- Skrip ini idempoten untuk data berlabel "UJI:" karena baris lama dihapus
-- lebih dulu. Baris verifikasi dan jadwal ikut terhapus lewat ON DELETE CASCADE.

USE manajemen_ruangan;

SET @admin := (SELECT id_pengguna FROM pengguna WHERE role = 'ADMIN' ORDER BY id_pengguna LIMIT 1);
SET @rama := (SELECT id_pengguna FROM pengguna WHERE email = 'rama.baskarayana@email.com');
SET @satria := (SELECT id_pengguna FROM pengguna WHERE email = 'satria.mahatama@email.com');
SET @mipa21 := (SELECT id_ruangan FROM ruangan WHERE nama_ruangan = 'MIPA 2.1' LIMIT 1);
SET @mipa14 := (SELECT id_ruangan FROM ruangan WHERE nama_ruangan = 'MIPA 1.4' LIMIT 1);
SET @lab := (SELECT id_ruangan FROM ruangan WHERE nama_ruangan = 'Lab Komputer' LIMIT 1);

DELETE FROM peminjaman WHERE keperluan LIKE 'UJI:%';

-- 1) Peminjaman disetujui: Rama, MIPA 2.1, 10-09-2026 08:00-10:00
INSERT INTO peminjaman (id_pengguna, id_ruangan, tanggal_pinjam, jam_mulai, jam_selesai, keperluan, status_verifikasi)
VALUES (@rama, @mipa21, '2026-09-10', '08:00:00', '10:00:00', 'UJI: kuliah PBO (disetujui)', 'Disetujui');
SET @p_disetujui := LAST_INSERT_ID();

INSERT INTO verifikasi (id_peminjaman, id_admin, status_verifikasi, catatan, tanggal_verifikasi)
VALUES (@p_disetujui, @admin, 'Disetujui', 'Sesuai jadwal kuliah', '2026-09-01 09:00:00');

INSERT INTO jadwal (id_ruangan, id_peminjaman, tanggal, jam_mulai, jam_selesai)
VALUES (@mipa21, @p_disetujui, '2026-09-10', '08:00:00', '10:00:00');

-- 2) Peminjaman ditolak: Rama, MIPA 1.4, 12-09-2026 13:00-15:00
INSERT INTO peminjaman (id_pengguna, id_ruangan, tanggal_pinjam, jam_mulai, jam_selesai, keperluan, status_verifikasi)
VALUES (@rama, @mipa14, '2026-09-12', '13:00:00', '15:00:00', 'UJI: kegiatan lain (ditolak)', 'Ditolak');
SET @p_ditolak := LAST_INSERT_ID();

INSERT INTO verifikasi (id_peminjaman, id_admin, status_verifikasi, catatan, tanggal_verifikasi)
VALUES (@p_ditolak, @admin, 'Ditolak', 'Ruangan dipakai kegiatan lain', '2026-09-02 10:15:00');

-- 3) Satria, MIPA 2.1, 10-09-2026 09:00-11:00 -> HARUS bentrok dengan data (1)
INSERT INTO peminjaman (id_pengguna, id_ruangan, tanggal_pinjam, jam_mulai, jam_selesai, keperluan, status_verifikasi)
VALUES (@satria, @mipa21, '2026-09-10', '09:00:00', '11:00:00',
        'UJI: beririsan dengan #1 (harus bentrok)', 'Pending');

-- 4) Satria, MIPA 2.1, 10-09-2026 10:00-12:00 -> menempel, TIDAK bentrok
INSERT INTO peminjaman (id_pengguna, id_ruangan, tanggal_pinjam, jam_mulai, jam_selesai, keperluan, status_verifikasi)
VALUES (@satria, @mipa21, '2026-09-10', '10:00:00', '12:00:00',
        'UJI: menempel setelah #1 (tidak bentrok)', 'Pending');

-- 5) Satria, Lab Komputer, 11-09-2026 09:00-10:00 -> ruangan lain, TIDAK bentrok
INSERT INTO peminjaman (id_pengguna, id_ruangan, tanggal_pinjam, jam_mulai, jam_selesai, keperluan, status_verifikasi)
VALUES (@satria, @lab, '2026-09-11', '09:00:00', '10:00:00',
        'UJI: ruangan lain (tidak bentrok)', 'Pending');
